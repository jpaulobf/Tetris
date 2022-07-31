package interfaces;

import java.awt.Graphics2D;
import java.awt.image.VolatileImage;

/**
 * All games need to implement the IGame interface
 */
public interface GameInterface {

    public static final int R = 82;
    public static final int P = 80;
    public static final int M = 77;
    public static final int N1 = 49;
    public static final int N2 = 50;

    /**
     * Game update
     * @param frametime
     */
    public void update(long frametime);

    /**
     * Game draw
     * @param frametime
     */
    public void draw(long frametime);

    /**
     * Draw the game in fullscreen
     * @param frametime
     * @param fullScreenXPos
     * @param fullScreenYPos
     * @param fullScreenWidth
     * @param fullScreenHeight
     */
    public void drawFullscreen(long frametime, int fullScreenXPos, int fullScreenYPos, int fullScreenWidth, int fullScreenHeight);

    /**
     * Recover the G2D from the buffer
     * @return
     */
    public Graphics2D getG2D();

    /**
     * Recover the bufferedImage
     * @return
     */
    public VolatileImage getBufferedImage();

    /**
     * Update Graphics for FullScreen
     * @param g2d
     */
    public void updateGraphics2D(Graphics2D g2d);

    /**
     * Get internal resolution - W
     * @return internal width resolution
     */
    public int getInternalResolutionWidth();

    /**
     * Get internal resolution - H
     * @return internal height resolution
     */
    public int getInternalResolutionHeight();

    /**
     * Generic audio control
     * @param type
     * @param mute
     */
    public void audioMuteControl(byte type, boolean mute);

    /**
     * Mute the music
     */
    public void toogleMuteTheme();

    /**
     * Decrease the Master Volume
     */
    public void decMasterVolume();

    /**
     * Increase the Master Volume
     */
    public void incMasterVolume();

    /**
     * Decrease only the theme
     */
    public void decVolumeTheme();

    /**
     * Increase the theme volume
     */    
    public void incVolumeTheme();

    /**
     * Decrease the SFX Volume
     */
    public void decVolumeSFX();

    /**
     * Increase the SFX Volume
     */
    public void incVolumeSFX();

    /**
     * Decrease the volume of the music
     * @param volume
     */
    public void decVolumeMusic(float volume);

    /**
     * Increase the volume of the music
     * @param volume
     */
    public void incVolumeMusic(float volume);

    /**
     * Decrease the volume of the SFX
     * @param volume
     */
    public void decVolumeSFX(float volume);

    /**
     * Increase the volume of the SFX
     * @param volume
     */
    public void incVolumeSFX(float volume);
    
    /**
     * Stop the music
     */
    public void stopTheme();

    /**
     * Pause the game
     */
    public void tooglePause();

    /**
     * Soft reset
     */
    public void softReset();

    /**
     * Exit the game
     */
    public void exitGame();

    /**
     * Key pressed
     * @param keyCode
     */
    public void keyPressed(int keyCode);

    /**
     * Key pressed
     * @param keyCode
     */
    public void keyPressed(int keyCode, boolean releaseAfter);

    /**
     * Key released
     * @param keyCode
     */
    public void keyReleased(int keyCode);

    /**
     * Update the 
     * @param state
     */
    public void changeGameState(int state);

    /**
     * Verify if Ghost Piece is allowed
     * @return
     */
    public boolean isToAllowGhostPiece();

    /**
     * Define the visibility of Ghost Pieces
     * @param show
     */
    public void setIsToAllowGhostPiece(boolean show);

    /**
     * Verify if Hold Piece is allowed
     * @return
     */
    public boolean isToAllowHold();

    /**
     * Define the visibility of Hold Pieces
     * @param hold
     */
    public void setIsToAllowHold(boolean hold);

    /**
     * Define how many "next pieces" is to show
     * @param pieces
     */
    public void setHowManyNextPieces(byte pieces);

    /**
     * Recover how many "next pieces" is to show
     * @return
     */
    public byte getHowManyNextPieces();

    /**
     * Return to main menu
     */
    public void toMainMenu();

    /**
     * Terminate the current game
     */
    public void gameTerminate();

    /**
     * Set the game to gameover
     */
    public void gameOver();
}