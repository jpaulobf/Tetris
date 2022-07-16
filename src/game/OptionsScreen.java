package game;

import interfaces.GameInterface;
import java.awt.Graphics2D;
import java.awt.Color;
import util.LoadingStuffs;
import java.awt.image.BufferedImage;
import util.Audio;

public class OptionsScreen {
    
    //game parameters
    private volatile long framecounter      = 0L;
    private GameInterface gameRef           = null;
    private Graphics2D g2d                  = null;
    private BufferedImage selector          = null;
    private BufferedImage optionsLogo       = null;
    private BufferedImage labelPlayMusic    = null;

    private BufferedImage toogleOn          = null;
    private BufferedImage toogleOff         = null;
    private BufferedImage toogleMusic       = null;
    private BufferedImage toogleSfx         = null;

    private BufferedImage labelPlaySfx      = null;
    private BufferedImage labelMusicVolume  = null;
    private BufferedImage labelSfxVolume    = null;
    private BufferedImage labelExit         = null;

    private BufferedImage volume1On         = null;
    private BufferedImage volume2On         = null;
    private BufferedImage volume3On         = null;
    private BufferedImage volume4On         = null;
    private BufferedImage volume5On         = null;
    private BufferedImage volume6On         = null;

    private BufferedImage volume2Off        = null;
    private BufferedImage volume3Off        = null;
    private BufferedImage volume4Off        = null;
    private BufferedImage volume5Off        = null;
    private BufferedImage volume6Off        = null;

    private BufferedImage musicVolume1      = null;
    private BufferedImage musicVolume2      = null;
    private BufferedImage musicVolume3      = null;
    private BufferedImage musicVolume4      = null;
    private BufferedImage musicVolume5      = null;
    private BufferedImage musicVolume6      = null;

    private BufferedImage sfxVolume1        = null;
    private BufferedImage sfxVolume2        = null;
    private BufferedImage sfxVolume3        = null;
    private BufferedImage sfxVolume4        = null;
    private BufferedImage sfxVolume5        = null;
    private BufferedImage sfxVolume6        = null;
    
    //sounds
    private Audio start                     = null;

    //menu control
    private byte selectorPosition           = 0;
    private volatile boolean goMenuApply    = false;
    private volatile boolean goMenuCancel   = false;
    private int resolutionW                 = 0;
    private int resolutionH                 = 0;
    private int selectorO                   = 0;
    private int selectorP                   = 223 + (selectorO * 85);

    /**
     * Constructor
     * @param game
     */
    public OptionsScreen(GameInterface game) {

        //get the game pointer
        this.gameRef            = game;
        this.g2d                = this.getG2D();
        this.resolutionW        = this.gameRef.getInternalResolutionWidth();
        this.resolutionH        = this.gameRef.getInternalResolutionHeight();

        //load images
        this.selector           = (BufferedImage)LoadingStuffs.getInstance().getStuff("selector");
        this.optionsLogo        = (BufferedImage)LoadingStuffs.getInstance().getStuff("options-logo");
        this.labelPlayMusic     = (BufferedImage)LoadingStuffs.getInstance().getStuff("label-play-music");
        this.toogleOn           = (BufferedImage)LoadingStuffs.getInstance().getStuff("toogle-on");
        this.toogleOff          = (BufferedImage)LoadingStuffs.getInstance().getStuff("toogle-off");
        this.labelPlaySfx       = (BufferedImage)LoadingStuffs.getInstance().getStuff("label-play-sfx");
        this.labelMusicVolume   = (BufferedImage)LoadingStuffs.getInstance().getStuff("label-music-vol");
        this.labelSfxVolume     = (BufferedImage)LoadingStuffs.getInstance().getStuff("label-sfx-vol");
        this.labelExit          = (BufferedImage)LoadingStuffs.getInstance().getStuff("label-exit-option");
        this.volume1On          = (BufferedImage)LoadingStuffs.getInstance().getStuff("v1-on");
        this.volume2On          = (BufferedImage)LoadingStuffs.getInstance().getStuff("v2-on");
        this.volume3On          = (BufferedImage)LoadingStuffs.getInstance().getStuff("v3-on");
        this.volume4On          = (BufferedImage)LoadingStuffs.getInstance().getStuff("v4-on");
        this.volume5On          = (BufferedImage)LoadingStuffs.getInstance().getStuff("v5-on");
        this.volume6On          = (BufferedImage)LoadingStuffs.getInstance().getStuff("v6-on");
        this.volume2Off         = (BufferedImage)LoadingStuffs.getInstance().getStuff("v2-off");
        this.volume3Off         = (BufferedImage)LoadingStuffs.getInstance().getStuff("v3-off");
        this.volume4Off         = (BufferedImage)LoadingStuffs.getInstance().getStuff("v4-off");
        this.volume5Off         = (BufferedImage)LoadingStuffs.getInstance().getStuff("v5-off");
        this.volume6Off         = (BufferedImage)LoadingStuffs.getInstance().getStuff("v6-off");

        this.toogleMusic        = this.toogleOn;
        this.toogleSfx          = this.toogleOn;

        this.musicVolume1       = this.volume1On;
        this.musicVolume2       = this.volume2On;
        this.musicVolume3       = this.volume3On;
        this.musicVolume4       = this.volume4On;
        this.musicVolume5       = this.volume5On;
        this.musicVolume6       = this.volume6On;
        this.sfxVolume1         = this.volume1On;
        this.sfxVolume2         = this.volume2On;
        this.sfxVolume3         = this.volume3On;
        this.sfxVolume4         = this.volume4On;
        this.sfxVolume5         = this.volume5On;
        this.sfxVolume6         = this.volume6On;

        //load the sounds
        this.start              = (Audio)LoadingStuffs.getInstance().getStuff("start");

    }

    /**
     * Game logic update
     * @param frametime
     */
    public synchronized void update(long frametime) {
    }

    /**
     * Draw method
     * @param frametime
     */    
    public synchronized void draw(long frametime) {

        this.g2d.setBackground(new Color(0, 66, 147));
        this.g2d.clearRect(0, 0, resolutionW, resolutionH);
        
        this.g2d.drawImage(this.selector, 0, selectorP, null);
        this.g2d.drawImage(this.optionsLogo, 852, 23, null);
        this.g2d.drawImage(this.labelPlayMusic, 69, 218, null);
        this.g2d.drawImage(this.labelPlaySfx, 69, 303, null);
        this.g2d.drawImage(this.labelMusicVolume, 69, 388, null);
        this.g2d.drawImage(this.labelSfxVolume, 69, 472, null);
        this.g2d.drawImage(this.toogleMusic, 1141, 218, null);
        this.g2d.drawImage(this.toogleSfx, 1141, 303, null);
        this.g2d.drawImage(this.labelExit, 69, 685, null);

        this.g2d.drawImage(this.musicVolume6, 1141, 392, null);
        this.g2d.drawImage(this.musicVolume5, 1172, 396, null);
        this.g2d.drawImage(this.musicVolume4, 1203, 400, null);
        this.g2d.drawImage(this.musicVolume3, 1234, 404, null);
        this.g2d.drawImage(this.musicVolume2, 1265, 408, null);
        this.g2d.drawImage(this.musicVolume1, 1296, 412, null);

        this.g2d.drawImage(this.sfxVolume6, 1141, 476, null);
        this.g2d.drawImage(this.sfxVolume5, 1172, 480, null);
        this.g2d.drawImage(this.sfxVolume4, 1203, 484, null);
        this.g2d.drawImage(this.sfxVolume3, 1234, 488, null);
        this.g2d.drawImage(this.sfxVolume2, 1265, 492, null);
        this.g2d.drawImage(this.sfxVolume1, 1296, 496, null);

        //selector x=(0), 223, 308, 393, 478, 696
        //v6 2 x=1141, y=476
        //v5 2 x=1172, y=480
        //v4 2 x=1203, y=484
        //v3 2 x=1234, y=488
        //v2 2 x=1265, y=492
        //v1 2 x=1296, y=496

    }

    /**
     * 
     * @param frametime
     */
    public synchronized void firstUpdate(long frametime) {
    }

    //getters
    public boolean goMenuApply()                {   return (this.goMenuApply);              }
    public boolean goMenuCancel()               {   return (this.goMenuCancel);             }
    public Graphics2D getG2D()					{ 	return (this.gameRef.getG2D());		    }

    /**
     * 
     * @param key
     */
    public void keyMovement(int key) {
        
        if (key == 38) { //UP
            
            
        } else if (key == 40) { //DOWN
            

        } else if (key == 10) { //ENTER
            
            //verify if has to enter or exit the options menu
            this.goMenuApply   = false;
            this.goMenuCancel  = false;
            if (this.selectorPosition == 10) {
                this.goMenuApply = true;
            } else if (this.selectorPosition == 20) {
                this.goMenuCancel = true;
            }

        } else if (key == 27) { //Esc - Back to Menu
            this.goMenuCancel  = true;
        }
    }

    /**
     * Reset game options
     */
    public void reset() {
        this.goMenuApply = false;
        this.goMenuCancel = false;
    }
}