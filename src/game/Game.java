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
    private Menu menu                       = null;
	private Board board						= null;
    private Score score                     = null;
    private ScreenTransition screenT        = null;
    private GameLevel gameLevel			    = null;
    private ExitScreen exitScreen           = null;

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
        this.menu               = new Menu(this);
        this.score              = new Score(this, new Point(9, 45), new Point(1173, 45), new Point(75, 412), new Point(58, 618));
        this.screenT            = new ScreenTransition(this);
        this.exitScreen         = new ExitScreen(this, this.wwm, this.whm);
    }
    
    /**
     * Update the game logic / receives the frametime
     * @param frametime
     */
    @Override
    public synchronized void update(long frametime) {
        
        if (!this.changingStage && !this.stopped) {
            
            //update based on game state
            if (this.gameState.getCurrentState() == StateMachine.MENU) {

                //sum framecounter
                this.framecounter += frametime;
                                
                //update just one time
                if (this.framecounter == frametime) { 
                    //if necessary
                    this.menu.firstUpdate(frametime);
                } else {
                    this.menu.update(frametime);

                    if (this.menu.goOptions()) {
                        this.gameState.setCurrentState(StateMachine.OPTIONS);
                    } else if (this.menu.goGame()) {

                        //get the level defined in the menu
                        this.gameLevel  = new GameLevel(this.menu.getLevel());
                        this.board      = new Board(this);

                        //go to staging status
                        this.gameState.setCurrentState(StateMachine.STAGING);
                    } else if (this.menu.goExit()) {
                        System.exit(0);
                    }
                }
            }
            else if (this.gameState.getCurrentState() == StateMachine.STAGING) {
                
                //sum framecounter
                this.framecounter += frametime;
                
                //update just one time
                if (this.framecounter == frametime) { 
                    //if necessary
                } else {
                    //
                    //do whatever is necessary to start the game...
                    //
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
                this.screenT.update(frametime);
               
                // if (true) { //this.tetris.getLives() == 0) { //after possible colision, check lives.
                //     this.gameState.setCurrentState(StateMachine.GAME_OVER);
                //     //this.score.storeNewHighScore();
                //     //this.score.reset();
                //     this.framecounter = 0;
                // }
            } else if (this.gameState.getCurrentState() == StateMachine.EXITING) {
                
                this.framecounter += frametime;
                
                this.exitScreen.update(frametime);
                //pause

                
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
                if (this.gameState.getCurrentState() == StateMachine.MENU) { 
                    this.menu.draw(frametime);
                } else if (this.gameState.getCurrentState() == StateMachine.IN_GAME ||
                           this.gameState.getCurrentState() == StateMachine.EXITING) {
                    this.board.draw(frametime);
                    this.score.draw(frametime);
                    this.screenT.draw(frametime);
                            
                    if (this.gameState.getCurrentState() == StateMachine.EXITING) {
                        this.exitScreen.draw(frametime);
                    }

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
     * Toogle the pause button
     */
    @Override
    public synchronized void tooglePause() {
        this.toogleMuteTheme();
        this.board.tooglePause();
    }

    /**
     * Game reset
     */
    @Override
    public void softReset() {
        this.resetTheme();
        this.board.resetGame();
        this.screenT.reset();
        this.score.reset();
    }

    /**
     * Toogle changing stage controller
     */
    public synchronized void toogleChangingStage() {
        this.changingStage = !this.changingStage;
    }

    /**
     * Toogle color theme
     */
    public void toogleColorTheme() {
        this.board.toogleColorTheme();
    }

    //----------------------------------------------------//
    //--------------- Keyboard & Joystick ----------------//
    //----------------------------------------------------//
    /**
     * Control the game main character movement
     * @param keyDirection
     */
    private synchronized void movement(int keyDirection) {
        if (this.gameState.getCurrentState() == StateMachine.MENU) {
            this.menu.keyMovement(keyDirection);
        } else if (this.gameState.getCurrentState() == StateMachine.IN_GAME) {
            this.board.move(keyDirection);
        }
    }

    /**
     * Control the game main character movement
     * @param keyDirection
     */
    private synchronized void movement(int keyDirection, boolean releaseAfter) {
        if (this.gameState.getCurrentState() == StateMachine.IN_GAME) {
            this.board.move(keyDirection, releaseAfter);
        }
    }

    /**
     * Game keypress
     */
    @Override
    public void keyPressed(int keyCode) {
        if (!this.changingStage && !this.stopped) {
            this.movement(keyCode);
            if (keyCode == 45) {this.decMasterVolume();}
            if (keyCode == 61) {this.incMasterVolume();}
        }

        //WIP
        if (this.gameState.getCurrentState() == StateMachine.IN_GAME) {
            if (keyCode == 27) {
                //game pause
                //game status exiting
                //open confirmation screen
                //wait for confirmation
                //if (true)
                this.gameState.setCurrentState(StateMachine.EXITING);
                //System.exit(0);

                //else
                //close confirmation screen
                //game status InGame
                //game unpause
            }
        }
    }

    /**
     * Key pressed
     * @param keyCode
     */
    @Override
    public void keyPressed(int keyCode, boolean releaseAfter) {
        if (!this.changingStage && !this.stopped) {
            this.movement(keyCode, releaseAfter);
        }
    }

    /**
     * Game keyRelease
     */
    public void keyReleased(int keyCode) {
        if (!this.changingStage && !this.stopped) {
            if (this.gameState.getCurrentState() == StateMachine.IN_GAME) {
                this.board.canRotate = true;
                if (keyCode == N1)  {this.toogleSoundTheme();   }
                if (keyCode == N2)  {this.toogleColorTheme();   }
                if (keyCode == M)   {this.toogleMuteTheme();    }
                if (keyCode == P)   {this.tooglePause();        }
                if (keyCode == R)   {this.softReset();          }
            }
        }
    }

    //----------------------------------------------------//
    //--------------- Music & SFX  -----------------------//
    //----------------------------------------------------//
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
     * Stop the theme position
     */
    @Override
    public void stopTheme() {
        this.theme.stop();
    }

    /**
     * Aux reset method
     */
    private void resetTheme() {
        this.stopTheme();
        this.theme.playContinuously();
    }

    /**
     * Increase/Decrease the Master Volume
     */
    public void decMasterVolume() {this.decVolumeSFX(); this.decVolumeTheme();}
    public void incMasterVolume() {this.incVolumeSFX(); this.incVolumeTheme();}

    /**
     * Increase/Decrease only the theme
     */
    public void decVolumeTheme() {this.theme.decVolume(1);}
    public void incVolumeTheme() {this.theme.addVolume(1);}

    /**
     * Increase/Decrease the SFX Volume
     */
    public void decVolumeSFX() {this.board.decVolumeSFX();}
    public void incVolumeSFX() {this.board.incVolumeSFX();}

    //----------------------------------------------------//
    //--------------- 	Game Level  ----------------------//
    //----------------------------------------------------//
    /**
     * Advance to the next game level.
     */
    public void nextLevel() {
        this.screenT.reset();
        this.nextGameSpeed();
        this.toogleColorTheme();
    }
    
	/**
	 * Bridge to the gameLevel getCurrentGameLevel
	 */
	public byte getCurrentLevel() {
        return (this.gameLevel.getCurrentGameLevel());
    }

	/**
	 * Bridge to the gameLevel nextGameLevel
	 */
	public void nextGameSpeed() {
		this.gameLevel.nextGameLevel();
	}

    /**
	 * Bridge to the gameLevel resetGameLevel
	 */
    public void resetGameLevel() {
        this.gameLevel.resetGameLevel();
    }

    /**
	 * Bridge to the gameLevel getGameSpeed
	 */
    public double getGameSpeed() {
        return (this.gameLevel.getGameSpeed());
    }

	/**
	 * Private Game Level Class
	 */
	private class GameLevel {
		
		private byte firstLevelDefinition		= 1;
		private final double defaultGameSpeed 	= 1D;
		private byte level 						= this.firstLevelDefinition;
		private double gameSpeed 				= this.defaultGameSpeed;
		private double speedFactor 				= 1.3D;
		private final static byte MIN 			= 1;
		private final static byte MAX 			= 8;

		public GameLevel(byte level) {
			if (level >= MIN && level <= MAX) {
				this.firstLevelDefinition = level;
				this.level = this.firstLevelDefinition;
				this.defGameSpeed();
			}
		}

		public byte getCurrentGameLevel() {
			return (this.level);
		}

		public void nextGameLevel() {
			if (this.level < MAX) {
				this.level++;
				this.defGameSpeed();
			}
		}

		public double getGameSpeed() {
			return (this.gameSpeed);
		}

		public void resetGameLevel() {
			this.level = this.firstLevelDefinition;
			this.gameSpeed = this.defaultGameSpeed;
			this.defGameSpeed();
		}

		private void defGameSpeed() {
			for (int i = MIN; i < this.level; i++) {
				this.gameSpeed *= speedFactor;
			}
		}
	}

    //----------------------------------------------------//
    //------------------- Accessors ----------------------//
    //----------------------------------------------------//
    //public Score getScore()                     {   return (this.score);        }
    //public GameOver getGameOver()               {   return this.gameOver;       }
    public StateMachine getGameState()              {   return this.gameState;      }
    public int getInternalResolutionWidth()         {   return (this.wwm);          }
    public int getInternalResolutionHeight()        {   return (this.whm);          }
    public VolatileImage getBufferedImage()         {   return (this.bufferImage);  }
    public Graphics2D getG2D()                      {   return (this.g2d);          }
    public Score getScore()                         {   return (this.score);        }
    public Board getBoard()                         {   return (this.board);        }
    public void updateGraphics2D(Graphics2D g2d)    {   this.g2dFS = g2d;           }
}