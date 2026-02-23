package engine;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import interfaces.CanvasEngine;
import interfaces.ControllerListener;
import interfaces.GameInterface;
import util.JoystickController;

/**
    Project:    Tetris Game
    Author:     Joao P. B. Faria
    Date:       March, 2002
    WTCD:       This class, encapsulate the Engine & Canvas
*/
public class Tetris implements Runnable {

    /**
     * Game Canvas
     */
    private class Canvas extends JFrame implements CanvasEngine, ControllerListener {

        private static final long serialVersionUID  = 1L;

        //this window properties
        private int positionX                       = 0;
        private int positionY                       = 0;

        //width and height of the window
        private int windowWidth                     = 1366;
        private int windowHeight                    = 768;
        private int fullScreenWidth                 = 0;
        private int fullScreenHeight                = 0;
        private int fullScreenXPos                  = 0;
        private int fullScreenYPos                  = 0;
        private int fullscreenState                 = 0;
        
        //the first 'canvas' & the backbuffer (for simple doublebuffer strategy)
        private JPanel canvas                       = null;
        private GameInterface game                  = null;

        //some support and the graphical device itself
        private GraphicsEnvironment ge              = null;
        private GraphicsDevice dsd                  = null;
        private BufferStrategy bufferStrategy       = null;
        private volatile Graphics2D g2d             = null;
        private Dimension size                      = null;

        //add support to joystick
        private JoystickController controller       = null;

        //show or hide the game FPS
        private boolean showFPS                     = true;

        //FPS calculation
        private long[] fpsHistory                   = new long[20];
        private int fpsIndex                        = 0;
        private int fpsCount                        = 0;
        private long fpsTotalTime                   = 0;
        private Font fpsFont;

        //control and fullscreen controller
        private boolean fullscreen                  = false;
        private boolean isFullScreenAvailable       = false;

        /**
         * Game canvas constructor
        */
        public Canvas() {
            //////////////////////////////////////////////////////////////////////
            //set some properties for this window
            //////////////////////////////////////////////////////////////////////

            //recover the desktop resolution
            this.size = Toolkit.getDefaultToolkit(). getScreenSize();

            //Verify if Windows width/height fits the current resolution, otherwise, resize it.
            double heightMinus50 = this.size.getHeight() - 50;
            if (this.windowHeight > heightMinus50) {
                this.windowWidth = (int)((double)this.windowWidth / (double)this.windowHeight * heightMinus50);
                this.windowHeight = (int)heightMinus50;
            } if (this.windowWidth > this.size.getWidth()) {
                this.windowHeight = (int)((double)this.windowHeight * (double)this.size.getWidth() / (double)this.windowWidth);
                this.windowWidth = (int)this.size.getWidth();
            }

            //define windows properties
            Dimension basic = new Dimension(this.windowWidth, this.windowHeight);
            this.setPreferredSize(basic);
            this.setMinimumSize(basic);
            this.setUndecorated(true);
            this.setResizable(false);

            //default operation on close (exit in this case)
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);

            //center the current window regards the desktop resolution
            this.positionX  = (int)((size.getWidth() / 2) - (this.windowWidth / 2));
            this.positionY  = (int)((size.getHeight() / 2) - (this.windowHeight / 2));
            this.setLocation(this.positionX, this.positionY);

            //create the backbuffer from the size of screen resolution to avoid any resize process penalty
            this.ge     = GraphicsEnvironment.getLocalGraphicsEnvironment();
            this.dsd    = ge.getDefaultScreenDevice();

            //verify if fullscreen is posible
            this.isFullScreenAvailable  = dsd.isFullScreenSupported();

            //default fullscreen size
            this.fullScreenHeight   = (int)size.getHeight();
            this.fullScreenWidth    = (int)size.getWidth();
            this.fullScreenXPos     = 0;
            this.fullScreenYPos     = 0;

            //////////////////////////////////////////////////////////////////////
            // ->>>  now, for the canvas
            //////////////////////////////////////////////////////////////////////
            //initialize the canvas
            this.canvas = new JPanel(null);
            this.canvas.setSize(this.windowWidth, this.windowHeight);
            this.canvas.setBackground(Color.BLACK);
            this.canvas.setOpaque(true);
            
            //final parameters for the window
            this.add(canvas);

            //verify if fullscreen mode is supported & desired
            if (fullscreen && isFullScreenAvailable) {
                // set to Full-screen mode
                this.setIgnoreRepaint(true);
                dsd.setFullScreenWindow(this);
                this.setBufferStrategy();
                validate();
            } else {
                this.pack();
                this.setLocationRelativeTo(null);
            }

            //start the game controller
            this.game = GameFactory.getGameInstance();

            //recover the pointer to the buffer graphics2d
            this.g2d  = this.game.getG2D();

            //thread para o controle (quando presente)
            this.controller = new JoystickController(this);

            // Pré-carrega a fonte para a renderização do FPS
            this.fpsFont = new Font("Arial", Font.PLAIN, 12);

            //KeyListener
            this.addKeyListener(new KeyAdapter() {
                @Override
                public synchronized void keyPressed(KeyEvent e) {
                    game.keyPressed(e.getKeyCode());
                }
                @Override
                public synchronized void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == 113) {
                        if (fullscreen) {
                            toogleFullscreenResolution();
                        }
                    }
                    if (e.getKeyCode() == 114) {toogleFullscreen();}
                    game.keyReleased(e.getKeyCode());
                }
            });     

            //show the game screen
            this.setVisible(true);
            this.requestFocus();
        }

        /**
         * Update the game logic / receives the frametime
         * @param frametime
         */
        public synchronized void update(long frametime) {
            this.game.update(frametime);
            this.controller.update(frametime);
        }
        
        /**
         * Draw the game / receives the frametime
         * WTMD: This method draw the current screen, some steps described here:
                  1) Clear the stage
        * @param frametime
        */
        public synchronized void draw(long frametime) {

            if (fullscreen && isFullScreenAvailable) {
                if (this.bufferStrategy != null) {
                    //set the buffer strategy
                    this.g2d = (Graphics2D)this.bufferStrategy.getDrawGraphics();
                    this.g2d.setBackground(Color.BLACK);
                    this.g2d.clearRect(0, 0, this.getWidth(), this.getHeight());

                    //update the game graphics
                    this.game.updateGraphics2D(this.g2d);

                    //render the game elements
                    this.game.draw(frametime);
                    this.game.drawFullscreen(frametime, this.fullScreenXPos, this.fullScreenYPos, this.fullScreenWidth, this.fullScreenHeight);

                    //render the fps counter
                    this.renderFPSLayer(frametime);

                    //show the buffer content
                    this.g2d.dispose();
                    if (!this.bufferStrategy.contentsLost()) {
                        this.bufferStrategy.show();
                    }
                }
            } else {
                //verify if the Graphics element isn't lost
                if (this.g2d != null) {

                    //render the game elements
                    this.game.draw(frametime);
        
                    //render the fps counter
                    this.renderFPSLayer(frametime);

                    //At least, copy the backbuffer to the canvas screen
                    this.canvas.getGraphics().drawImage(this.game.getBufferedImage(), 0, 0, this.windowWidth, this.windowHeight, //destine
                                                                                      0, 0, game.getInternalResolutionWidth(), 
                                                                                            game.getInternalResolutionHeight(), //source
                                                                                      this);
                }
            }
        }

        /**
         * Change the window to normal or fullscreen (F3)
         */
        public synchronized void toogleFullscreen() {
            if (this.fullscreen) { 
                //back to window
                this.dsd.setFullScreenWindow(null);

                //recover the G2D (not from bufferstrategy)
                this.g2d = this.game.getG2D();

                //toogle fullscreen flag
                this.fullscreen = false;

                //config the window
                this.setIgnoreRepaint(false);
                this.setLocationRelativeTo(null);
                this.pack();

            } else if (this.isFullScreenAvailable) { //fullscreen mode just if fs is available

                //toogle fullscreen flag
                this.fullscreen = true;

                //set to fullscreen
                this.dsd.setFullScreenWindow(this);

                //if already set the bufferstrategy, ignore, otherwise, set
                if (this.bufferStrategy == null) {
                    this.setBufferStrategy();
                }
                
                //ignore repaint & validate
                this.setIgnoreRepaint(true);
                validate();
            }
        }

        /**
         * Change screen stretch on the fly (F2)
         */
        public synchronized void toogleFullscreenResolution() {
            this.fullscreenState = (this.fullscreenState + 1)%3;

            switch (this.fullscreenState) {
                case 0:
                    this.fullScreenHeight   = (int)size.getHeight();
                    this.fullScreenWidth    = (int)this.getWidth();
                    this.fullScreenXPos     = 0;
                    this.fullScreenYPos     = 0;
                    break;
                case 1:
                    // calc fullscreen width/height
                    this.fullScreenHeight   = (int)size.getHeight();
                    this.fullScreenWidth    = (int)((double)this.windowWidth/(double)this.windowHeight*(double)size.getHeight());
                    if (this.fullScreenWidth > this.size.getWidth()) {
                        this.fullScreenHeight = (int)((double)this.fullScreenHeight * (double)this.size.getWidth() / (double)this.fullScreenWidth);
                        this.fullScreenWidth = (int)this.size.getWidth();
                    }
                    this.fullScreenXPos     = (int)((size.getWidth() - this.fullScreenWidth) / 2);
                    this.fullScreenYPos     = (int)((size.getHeight() - this.fullScreenHeight) / 2);
                    this.fullScreenWidth    += this.fullScreenXPos;
                    this.fullScreenHeight   += this.fullScreenYPos;
                    break;
                case 2:
                    this.fullScreenHeight   = this.windowHeight;
                    this.fullScreenWidth    = this.windowWidth;
                    this.fullScreenXPos     = (int)((size.getWidth() - this.fullScreenWidth) / 2);
                    this.fullScreenYPos     = (int)((size.getHeight() - this.fullScreenHeight) / 2);
                    this.fullScreenWidth    += this.fullScreenXPos;
                    this.fullScreenHeight   += this.fullScreenYPos;
                    break;
            }
        }

        /**
         * Show FPS Layer
         * @param frametime
         */
        private void renderFPSLayer(long frametime) {
            //verify if the user want to show the FPS
            if (this.showFPS) {
                // Se o histórico estiver cheio, subtrai o valor mais antigo que será substituído.
                if (this.fpsCount == this.fpsHistory.length) {
                    this.fpsTotalTime -= this.fpsHistory[this.fpsIndex];
                }

                // Adiciona o novo frametime ao total e atualiza o histórico.
                this.fpsTotalTime += frametime;
                this.fpsHistory[this.fpsIndex] = frametime;

                this.fpsIndex = (this.fpsIndex + 1) % this.fpsHistory.length;
                if (this.fpsCount < this.fpsHistory.length) {
                    this.fpsCount++;
                }

                double average = (this.fpsCount > 0) ? (double)this.fpsTotalTime / this.fpsCount : frametime;

                g2d.setColor(Color.BLUE);
                g2d.setFont(this.fpsFont);
                g2d.drawString("fps: " + (int)(1_000_000_000D / average), windowWidth - 70, windowHeight - 10);
            }
        }

        /**
         * Create the bufferstrategy
         */
        private void setBufferStrategy() { 
            Thread t = new Thread(new Runnable() {
                public void run() {
                    createBufferStrategy(3);    
                }
            });
            t.start();
            try {     
                Thread.sleep(500);
            } catch(InterruptedException ex){}
            this.bufferStrategy = super.getBufferStrategy();
        }

        @Override
        public void notify(boolean U, boolean D, boolean L, boolean R, boolean HOLD, boolean DROP, boolean ROTATE) {
            if (U) {
                this.game.keyPressed(38, true);
            } else if (D) {
                this.game.keyPressed(40);
            } else if (L) {
                this.game.keyPressed(37);
            } else if (R) {
                this.game.keyPressed(39);
            } else if (HOLD) {
                this.game.keyPressed(17);
            } else if (DROP) {
                this.game.keyPressed(32);
            } else if (ROTATE) {
                this.game.keyPressed(38, true);
            }

            if (!U && !D && !L && !R) {
                //this.game.keyReleased(0);
            }
        }
    }

    /**
     * Class of GameEngine
     */
    private class GameEngine implements Runnable {

        private boolean isEngineRunning     = true;
        private long FPS240                 = (long)(1_000_000_000 / 240);
        private long FPS120                 = (long)(1_000_000_000 / 120);
        private long FPS90                  = (long)(1_000_000_000 / 90);
        private long FPS60                  = (long)(1_000_000_000 / 60);
        private long FPS30                  = (long)(1_000_000_000 / 30);
        private long TARGET_FRAMETIME       = FPS60;
        private boolean UNLIMITED_FPS       = false;
        private CanvasEngine game          = null;
    
        /*
            WTMD: constructor
                    receives the target FPS (0, 30, 60, 120, 240) and starts the engine
        */
        public GameEngine(int targetFPS, CanvasEngine game) {
    
            this.UNLIMITED_FPS = false;
            switch(targetFPS) {
                case 30:
                    this.TARGET_FRAMETIME = FPS30;
                    break;
                case 60:
                    this.TARGET_FRAMETIME = FPS60;
                    break;
                case 90:
                    this.TARGET_FRAMETIME = FPS90;
                    break;
                case 120:
                    this.TARGET_FRAMETIME = FPS120;
                    break;
                case 240:
                    this.TARGET_FRAMETIME = FPS240;
                    break;
                case 0:
                    this.UNLIMITED_FPS = true;
                    break;
                default:
                    this.TARGET_FRAMETIME = FPS30;
                    break;
            }
            this.game = game;
        }
        
        /* Método de execução da thread */
        public void run() {
            long lastTime           = System.nanoTime(); // Usado para calcular o delta time no modo de FPS ilimitado
            long now                = 0;
            long elapsed            = 0;
            long wait               = 0;
            long overSleep          = 0;

            if (UNLIMITED_FPS) {
                while (isEngineRunning) {
                    now = System.nanoTime();
                    elapsed = now - lastTime;
                    lastTime = now;

                    // Cap delta time to avoid huge jumps (e.g. 0.1s)
                    if (elapsed > 100_000_000) elapsed = 100_000_000;

                    this.update(elapsed);
                    this.draw(elapsed);
                    
                    // Yield to prevent CPU starvation
                    Thread.yield();
                }
            } else {
                while (isEngineRunning) {
                    now = System.nanoTime();
                    elapsed = now - lastTime;
                    lastTime = now;

                    this.update(elapsed);
                    this.draw(elapsed);

                    // Calculate time taken
                    long workTime = System.nanoTime() - now;

                    // Calculate wait time, compensating for previous over-sleep/lag
                    wait = TARGET_FRAMETIME - workTime - overSleep;

                    if (wait > 0) {
                        try {
                            // Hybrid Sleep Strategy:
                            // Sleep for (wait - 2ms) to save CPU, then spin-wait for precision
                            long sleepMs = (wait / 1_000_000) - 2;
                            if (sleepMs > 0) {
                                Thread.sleep(sleepMs);
                            }
                            
                            // Busy-wait for the remaining nanoseconds
                            while (System.nanoTime() < now + TARGET_FRAMETIME - overSleep) {
                                // Spin-wait for precision (removido yield para maior precisão nos últimos ns)
                            }
                            overSleep = 0;
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    } else {
                        // We are behind schedule
                        overSleep = -wait;
                        
                        // Limita o overSleep para evitar que o jogo tente recuperar o tempo perdido indefinidamente
                        // Isso evita o efeito de aceleração excessiva após um lag spike
                        if (overSleep > TARGET_FRAMETIME) {
                            overSleep = TARGET_FRAMETIME;
                        }
                    }
                }
            }
        }
    
        /* Método de update, só executa quando a flag permite */
        public void update(long frametime) {
            this.game.update(frametime);
        }
    
        /* Método de desenho, só executa quando a flag permite */
        public void draw(long frametime) {
            this.game.draw(frametime);
        }
    }

    //target FPS
    private int targetFPS = 0;

    /**
     * Thread Constructor
     * @param targetFPS
     */
    public Tetris(int targetFPS) {
        this.targetFPS  = targetFPS;
    }

    /**
     * Run the gameengine
     */
    @Override
    public void run() {
        new GameEngine(this.targetFPS, new Canvas()).run();
    }
}