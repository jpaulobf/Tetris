package interfaces;

import java.awt.Graphics2D;
import java.awt.image.VolatileImage;

/**
 * All games need to implement the IGame interface
 */
public interface GameInterface {
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
     * Key pressed
     * @param keyCode
     */
    public void keyPressed(int keyCode);

    /**
     * Key released
     * @param keyCode
     */
    public void keyReleased(int keyCode);
}