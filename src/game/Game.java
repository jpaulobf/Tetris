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
import java.util.List;
import java.util.ArrayList;

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

    private List<Audio> audioList           = new ArrayList<Audio>();

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
    private MenuScreen menu                 = null;
    private OptionsScreen options           = null;
	private Board board						= null;
    private Score score                     = null;
    private ScreenTransition screenT        = null;
    private GameLevel gameLevel			    = null;
    private ExitScreen exitScreen           = null;
    private boolean isToShowPieceGhost      = true;
    private boolean isToAllowHold           = true;

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
        this.music1             = LoadingStuffs.getInstance().getAudio("theme1");
        this.music2             = LoadingStuffs.getInstance().getAudio("theme2");
        this.music3             = LoadingStuffs.getInstance().getAudio("theme3");
        this.theme              = this.music1;

        this.currentMusicTheme  = 0;
        this.menu               = new MenuScreen(this);
        this.options            = new OptionsScreen(this);
        this.score              = new Score(this, new Point(9, 45), new Point(1173, 45), new Point(75, 412), new Point(58, 618));
        this.screenT            = new ScreenTransition(this);
        this.exitScreen         = new ExitScreen(this, this.wwm, this.whm);

        this.audioList          = LoadingStuffs.getInstance().getAudioList();
    }
    
    /**
     * Update the game logic / receives the frametime
     * @param frametime
     */
    @Override
    public synchronized void update(long frametime) {
        
        //if changing stage or stopped do nothing.
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
                    
                    //update menu
                    this.menu.update(frametime);

                    //then, check the selections
                    if (this.menu.goOptions()) {
                        
                        //reset the menu selection parameters
                        this.menu.reset();

                        //define the current state as option
                        this.gameState.setCurrentState(StateMachine.OPTIONS);

                    } else if (this.menu.goGame()) {

                        //stop the game music
                        this.menu.stopMusic();
                        
                        //get the level defined in the menu
                        this.gameLevel  = new GameLevel(this.menu.getLevel());
                        this.board      = new Board(this);

                        //reset the menu selection parameters
                        this.menu.reset();

                        //go to staging status
                        this.gameState.setCurrentState(StateMachine.STAGING);
                    } else if (this.menu.goExit()) {
                        System.exit(0);
                    }
                }
            } else if (this.gameState.getCurrentState() == StateMachine.OPTIONS) {

                //sum framecounter
                this.framecounter += frametime;

                //update just one time
                if (this.framecounter == frametime) { 
                    //if necessary
                    this.menu.firstUpdate(frametime);
                
                } else {

                    //update the options screen
                    this.options.update(frametime);

                    if (this.options.goMenu()) {
                        //reset the options options
                        this.options.reset();
                        
                        //revert the state to menu
                        this.gameState.setCurrentState(StateMachine.MENU);

                        //skip next draw
                        this.skipDraw = true;
                    }
                }

            } else if (this.gameState.getCurrentState() == StateMachine.STAGING) {
                
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
                
                if (this.framecounter == frametime) {
                    this.exitScreen.playOpening();
                }
                
                this.exitScreen.update(frametime);

                
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
                } else if (this.gameState.getCurrentState() == StateMachine.OPTIONS) {
                    this.options.draw(frametime);
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
        } else if (this.gameState.getCurrentState() == StateMachine.OPTIONS) {
            this.options.keyMovement(keyDirection);
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

        if (this.gameState.getCurrentState() == StateMachine.IN_GAME) {
            //when ESC is pressed
            if (keyCode == 27) {
                this.gameState.setCurrentState(StateMachine.EXITING);
                this.framecounter = 0;
            }
        } else if (this.gameState.getCurrentState() == StateMachine.EXITING) {
            this.exitScreen.move(keyCode);
        }
    }

    /**
     * Exit the game (after confirmation)
     */
    public void exitGame() {
        System.exit(0);
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

    /**
     * Generic audio control
     */
    public void audioMuteControl(byte type, boolean mute) {
        for (Audio audio : audioList) {
            if (audio.getType() == type) {
                if (mute) {
                    audio.mute();
                } else {
                    audio.unmute();
                }
            }
        }
    }

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

    /**
     * Change the game state
     */
    @Override
    public void changeGameState(int state) {
        this.gameState.currentState = state;
    }

    /**
     * Verify if has or not to show the ghost piece
     * @return
     */
    public boolean isToAllowGhostPiece() {
        return (this.isToShowPieceGhost);
    }

    public void setIsToAllowGhostPiece(boolean show) {
        this.isToShowPieceGhost = show;
    }

    @Override
    public boolean isToAllowHold() {
        return (this.isToAllowHold);
    }

    @Override
    public void setIsToAllowHold(boolean hold) {
        this.isToAllowHold = hold;
    }
}