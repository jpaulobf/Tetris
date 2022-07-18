package game;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import engine.Tetris;
import util.LoadingStuffs;

/*
    Project:    Modern 2D Java Game Engine
    Purpose:    Provide basics functionalities to write 2D games in Java in a more modern approach
    Author:     Mr. Joao P. B. Faria
    Date:       Octuber 2021
    WTCD:       This class, provides a selection screen, that could be hide forever, that allow the user to choose between window format (full, pseudo-full, windowed)
                and than, the syncronization method, frame cap, screen size & resolution.
*/
public class SplashScreen extends JFrame implements Runnable {

    private static final long serialVersionUID  = 1L;

    //this window properties
    private int positionX                       = 0;
    private int positionY                       = 0;
    private int windowWidth                     = 800;
    private int windowHeight                    = 400;
    private int w, h, x, y                      = 0;

    //desktop properties
    private int resolutionH                     = 0;
    private int resolutionW                     = 0;
    
    //the first 'canvas' & the backbuffer (for simple doublebuffer strategy)
    private JPanel canvas                       = null;
    private VolatileImage bufferImage           = null;
    private BufferedImage splashImage           = null;

    //some support and the graphical device itself
    private GraphicsEnvironment ge              = null;
    private GraphicsDevice dsd                  = null;
    private Graphics2D g2d                      = null;

    //this screen control logic parameter   
    private int FPS                             = 0;
    
    /*
        WTMD: some responsabilites here:
            1) load some parameters from config file (if exists)
            2) center the window in the screen
            3) add a keylistener
            4) initialize the canvas and retrieve the graphical device objects
    */
    public SplashScreen(int FPS) {

        //////////////////////////////////////////////////////////////////////
        // ->>>  for the window
        //////////////////////////////////////////////////////////////////////
        LoadingStuffs.getInstance();

        //set some properties for this window
        Dimension basic = new Dimension(this.windowWidth, this.windowHeight);
        this.setPreferredSize(basic);
        this.setMinimumSize(basic);
        this.setUndecorated(true);

        //default operation on close (exit in this case)
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //recover the desktop resolution
        Dimension size = Toolkit.getDefaultToolkit(). getScreenSize();

        //and save this values
        this.resolutionH = (int)size.getHeight();
        this.resolutionW = (int)size.getWidth();

        //center the current window regards the desktop resolution
        this.positionX = (int)((size.getWidth() / 2) - (this.windowWidth / 2));
        this.positionY = (int)((size.getHeight() / 2) - (this.windowHeight / 2));
        this.setLocation(this.positionX, this.positionY);

        //create the backbuffer from the size of screen resolution to avoid any resize process penalty
        this.ge             = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.dsd            = ge.getDefaultScreenDevice();
        this.bufferImage    = dsd.getDefaultConfiguration().createCompatibleVolatileImage(this.resolutionW, this.resolutionH);
        this.g2d            = (Graphics2D)bufferImage.getGraphics();
        
        //Get the already loaded image from loader
        this.splashImage    = LoadingStuffs.getInstance().getImage("splashImage");

        //////////////////////////////////////////////////////////////////////
        // ->>>  now, for the canvas
        //////////////////////////////////////////////////////////////////////
        this.w      = this.splashImage.getWidth();
        this.h      = this.splashImage.getHeight();
        this.x      = (this.windowWidth - this.w) / 2;
        this.y      = (this.windowHeight - this.h) / 2;
        this.FPS    = FPS;

        //initialize the canvas
        this.canvas = new JPanel(null);
        this.canvas.setSize(windowWidth, windowHeight);
        this.canvas.setBackground(Color.BLACK);
        this.setVisible(true);
        this.canvas.setOpaque(true);
        
        //final parameters for the window
        this.add(canvas);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.requestFocus();
    }

    /*
        WTMD: Override the paint method, transfering the rendering control to draw.
    */
    @Override
    public void paint(Graphics g) {
        this.draw();
    }

    /*
        WTMD: This method draw the current screen, some steps described here:
            1) Clear the stage
            2) Print the main label
            3) Print the selection buttons
            4) Print the exit label
     */
    public void draw() {

        //update the window size variables if the user resize it.
        this.windowHeight = this.getHeight();
        this.windowWidth  = this.getWidth();

        if (this.g2d != null) {
            
            //clear the stage
            this.g2d.setBackground(Color.BLACK);
            this.g2d.clearRect(0, 0, this.resolutionW, this.resolutionH);

            //draw the splash image
            this.g2d.drawImage(this.splashImage, x, y, w + x, h + y, //dest w1, h1, w2, h2
                                                 0, 0, w, h, //source w1, h1, w2, h2
                                                 null);

            //At least, copy the backbuffer to the canvas screen
            this.canvas.getGraphics().drawImage(this.bufferImage, 0, 0, this);
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1_000);
            this.setVisible(false);
            //start the thread
            Thread thread = new Thread(new Tetris(this.FPS), "engine");
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
        } catch (Exception e) {}
    }
}