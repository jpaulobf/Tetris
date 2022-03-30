package game;

import util.Audio;
import util.LoadingStuffs;
import java.awt.Color;
import java.awt.Graphics2D;
import interfaces.GameInterface;
import java.awt.image.VolatileImage;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Point;

/**
 * Class responsable for the game
 */
public class Game implements GameInterface {

    //the game statemachine goes here
    private StateMachine gameState          = null;

    //some support and the graphical device itself
    private Graphics2D g2d                  = null;

    //the game variables go here...
    //private Score score                     = null;
    //private GameOver gameOver               = null;
    //private volatile Audio gameoverTheme    = null;

    private volatile byte currentMusicTheme = 0;
    private volatile Audio theme            = null;
    private volatile Audio music1           = null;
    private volatile Audio music2           = null;
    private volatile Audio music3           = null;
    private volatile long framecounter      = 0;
    private volatile boolean mute           = false;
    private volatile boolean stopped        = false;
    private volatile boolean changingStage  = false;
    private volatile boolean skipDraw       = false;

    //width and height of window for base metrics of the game
    private final int wwm                   = 1366;
    private final int whm                   = 768;

    //graphic device elements
    private VolatileImage bufferImage       = null;
    private GraphicsEnvironment ge          = null;
    private GraphicsDevice dsd              = null;
    private Graphics2D g2dFS                = null;
    private boolean sortPiece 				= true;

    //game components
	private Board board						= null;
    private Score score                     = null;

    /**
     * Game constructor
     */
    public Game() {
        //create the double-buffering image
        this.ge                 = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.dsd                = ge.getDefaultScreenDevice();
        this.bufferImage        = dsd.getDefaultConfiguration().createCompatibleVolatileImage(this.wwm, this.whm);
        this.g2d                = (Graphics2D)bufferImage.getGraphics();
        this.g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        //////////////////////////////////////////////////////////////////////
        // ->>>  create the game elements objects
        //////////////////////////////////////////////////////////////////////
        this.gameState          = new StateMachine(this);
        this.music1             = (Audio)LoadingStuffs.getInstance().getStuff("theme1");
        this.music2             = (Audio)LoadingStuffs.getInstance().getStuff("theme2");
        this.music3             = (Audio)LoadingStuffs.getInstance().getStuff("theme3");
        this.theme              = this.music1;
        this.currentMusicTheme  = 0;
        this.board              = new Board(this);
        this.score              = new Score(this, new Point(9, 45), new Point(1173, 45), new Point(75, 412), new Point(58, 618));
    }

    /**
     * Switch the game theme
     * @param theme
     */
    private void toogleSoundTheme() {
        this.theme.stop();
        this.currentMusicTheme = (byte)(++this.currentMusicTheme%3);

        switch (this.currentMusicTheme) {
            case 0:
                this.music1.stop();
                this.theme = this.music1;
                break;
            case 1:
                this.music2.stop();
                this.theme = this.music2;
                break;
            case 2:
                this.music3.stop();
                this.theme = this.music3;
                break;
        }

        this.theme.playContinuously();
    }
    
    /**
     * Update the game logic / receives the frametime
     * @param frametime
     */
    @Override
    public synchronized void update(long frametime) {
        
        if (!this.changingStage && !this.stopped) {
            
            //update based on game state
            if (this.gameState.getCurrentState() == StateMachine.STAGING) {
                
                //sum framecounter
                this.framecounter += frametime;
                
                //update just one time
                if (this.framecounter == frametime) { 
                    //if necessary
                } else {
                    //TODO: in a condition, advance to the next game-state...
                    this.framecounter = 0;
                    this.skipDraw = true;
                    this.gameState.setCurrentState(StateMachine.IN_GAME);
                }
            } else if (this.gameState.getCurrentState() == StateMachine.IN_GAME) {
                
                //sum framecounter
                this.framecounter += frametime;

                //update just once
                if (this.framecounter == frametime) {
                    this.theme.playContinuously();
                    this.skipDraw = true;
                } else {
                    if (this.sortPiece) {
                        this.board.sortPiecesList();
                        this.sortPiece = false;
                    }
                    
                    this.board.update(frametime);
                }

                this.score.update(frametime);
               
                // if (true) { //this.tetris.getLives() == 0) { //after possible colision, check lives.
                //     this.gameState.setCurrentState(StateMachine.GAME_OVER);
                //     //this.score.storeNewHighScore();
                //     //this.score.reset();
                //     this.framecounter = 0;
                // }
            } else if (this.gameState.getCurrentState() == StateMachine.GAME_OVER) {
                
                //sum framecounter
                this.framecounter += frametime;

                //update just once
                if (this.framecounter == frametime) { //run just once
                    this.theme.stop();
                    //this.gameoverTheme.play();
                } else if (this.framecounter >= 7_000_000_000L) {
                    this.framecounter = 0;
                    this.softReset();
                    this.gameState.setCurrentState(StateMachine.IN_GAME);
                }
            }
        }
    }

    /**
     * Draw the game elements
     * @param frametime
     */
    @Override
    public synchronized void draw(long frametime) {

        if (!this.skipDraw) {
            //this graphical device (g2d) points to backbuffer, so, we are making things behide the scenes
            //clear the stage
            this.g2d.setBackground(Color.WHITE);
            this.g2d.clearRect(0, 0, this.wwm, this.whm);

            if (!this.changingStage) {
                
                //////////////////////////////////////////////////////////////////////
                // ->>>  draw the game elements
                //////////////////////////////////////////////////////////////////////
                if (this.gameState.getCurrentState() == StateMachine.STAGING) {
                    //todo...
                } else if (this.gameState.getCurrentState() == StateMachine.IN_GAME) {
                    this.board.draw(frametime);
                    this.score.draw(frametime);
                } else if (this.gameState.getCurrentState() == StateMachine.GAME_OVER) {
                    //this.gameOver.draw(frametime);
                }
            }
        }
        this.skipDraw = false;
    }

    /**
     * Draw the game in full screen
     */
    @Override
    public void drawFullscreen(long frametime, int fullScreenXPos, int fullScreenYPos, int fullScreenWidth, int fullScreenHeight) {
        this.g2dFS.drawImage(this.bufferImage, fullScreenXPos, fullScreenYPos, fullScreenWidth, fullScreenHeight, 
                                               0, 0, this.wwm, this.whm, null);
    }

    /**
     * Control the game main character movement
     * @param keyDirection
     */
    private synchronized void movement(int keyDirection) {
        if (this.gameState.getCurrentState() == StateMachine.IN_GAME) {
            this.board.move(keyDirection);
        }
    }

    /**
     * Mute / unmute the game theme
     */
    @Override
    public void toogleMuteTheme() {
        if (!this.mute) {
            this.theme.pause();
        } else {
            this.theme.playContinuously();
        }
        this.mute = !this.mute;
    }

    /**
     * Stop the theme position
     */
    @Override
    public void stopTheme() {
        this.theme.stop();
    }

    /**
     * Update game graphics
     * @param g2d
     */
    @Override
    public void updateGraphics2D(Graphics2D g2d) {
        this.g2dFS = g2d;
    }

    /**
     * Toogle the pause button
     */
    @Override
    public synchronized void tooglePause() {
        this.toogleMuteTheme();
        this.board.tooglePause();
    }

    /**
     * Toogle changing stage controller
     */
    public synchronized void toogleChangingStage() {
        this.changingStage = !this.changingStage;
    }

    /**
     * Game reset
     */
    @Override
    public void softReset() {
        this.reset();
        this.board.resetGame();
    }

    /**
     * Aux reset method
     */
    private void reset() {
        this.theme.stop();
        this.theme.playContinuously();
    }

    /** 
     * go to the next stage 
     */
    public synchronized void nextStage() {

        //disable elements update
        this.toogleChangingStage();

        //this.board.reset();

        //Stages.CURRENT_STAGE[0]++;

        //unpause timer & frog
        //this.board.tooglePause();
        
        //return to initial position & play
        this.theme.stop();
        this.toogleMuteTheme();

        //enable elements update
        this.toogleChangingStage();
    }

    /**
     * Game keypress
     */
    public void keyPressed(int keyCode) {
        if (!this.changingStage && !this.stopped) {
            this.movement(keyCode);
        }
    }

    /**
     * Toogle color theme
     */
    public void toogleColorTheme() {
        this.board.toogleColorTheme();
    }

    /**
     * Game keyRelease
     */
    public void keyReleased(int keyCode) {
        if (!this.changingStage && !this.stopped) {
            this.board.canRotate = true;
            if (keyCode == 49) {this.toogleSoundTheme();}
            if (keyCode == 50) {this.toogleColorTheme();}
            if (keyCode == 77) {this.toogleMuteTheme();}
            if (keyCode == 80) {this.tooglePause();}
            if (keyCode == 82) {this.softReset();}
        }
    }

    /**
     * Accessor methods
     * @return
     */
    //public Score getScore()                     {   return (this.score);        }
    //public GameOver getGameOver()               {   return this.gameOver;       }
    public StateMachine getGameState()          {   return this.gameState;      }
    public int getInternalResolutionWidth()     {   return (this.wwm);          }
    public int getInternalResolutionHeight()    {   return (this.whm);          }
    public VolatileImage getBufferedImage()     {   return (this.bufferImage);  }
    public Graphics2D getG2D()                  {   return (this.g2d);          }
}