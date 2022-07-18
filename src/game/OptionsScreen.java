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
    private final byte TOTAL_OPTIONS        = 5;

    //sounds
    private Audio item                      = null;

    //menu control
    private byte selectorPosition           = 0;
    private volatile boolean goMenu         = false;
    private int resolutionW                 = 0;
    private int resolutionH                 = 0;
    private short selectorO                  = 223;
    private boolean isMusicOn               = true;
    private boolean isSfxOn                 = true;
    private short selectorP                 = (short)(selectorO + (selectorPosition * 85));
    private byte musicVolume                = 6;
    private byte sfxVolume                  = 6;

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
        this.selector           = LoadingStuffs.getInstance().getImage("selector");
        this.optionsLogo        = LoadingStuffs.getInstance().getImage("options-logo");
        this.labelPlayMusic     = LoadingStuffs.getInstance().getImage("label-play-music");
        this.toogleOn           = LoadingStuffs.getInstance().getImage("toogle-on");
        this.toogleOff          = LoadingStuffs.getInstance().getImage("toogle-off");
        this.labelPlaySfx       = LoadingStuffs.getInstance().getImage("label-play-sfx");
        this.labelMusicVolume   = LoadingStuffs.getInstance().getImage("label-music-vol");
        this.labelSfxVolume     = LoadingStuffs.getInstance().getImage("label-sfx-vol");
        this.labelExit          = LoadingStuffs.getInstance().getImage("label-exit-option");
        this.volume1On          = LoadingStuffs.getInstance().getImage("v1-on");
        this.volume2On          = LoadingStuffs.getInstance().getImage("v2-on");
        this.volume3On          = LoadingStuffs.getInstance().getImage("v3-on");
        this.volume4On          = LoadingStuffs.getInstance().getImage("v4-on");
        this.volume5On          = LoadingStuffs.getInstance().getImage("v5-on");
        this.volume6On          = LoadingStuffs.getInstance().getImage("v6-on");
        this.volume2Off         = LoadingStuffs.getInstance().getImage("v2-off");
        this.volume3Off         = LoadingStuffs.getInstance().getImage("v3-off");
        this.volume4Off         = LoadingStuffs.getInstance().getImage("v4-off");
        this.volume5Off         = LoadingStuffs.getInstance().getImage("v5-off");
        this.volume6Off         = LoadingStuffs.getInstance().getImage("v6-off");

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
        this.item               = LoadingStuffs.getInstance().getAudio("star");
    }

    /**
     * Game logic update
     * @param frametime
     */
    public synchronized void update(long frametime) {
        this.selectorP = (short)(this.selectorO + (this.selectorPosition * 85));
        if (this.selectorPosition == TOTAL_OPTIONS - 1) {
            this.selectorP += 133;
        }

        if (this.isMusicOn) {
            this.toogleMusic = this.toogleOn;
        } else {
            this.toogleMusic = this.toogleOff;
        }

        if (this.isSfxOn) {
            this.toogleSfx = this.toogleOn;
        } else {
            this.toogleSfx = this.toogleOff;
        }

        this.musicVolume6       = this.volume6On;
        this.musicVolume5       = this.volume5On;
        this.musicVolume4       = this.volume4On;
        this.musicVolume3       = this.volume3On;
        this.musicVolume2       = this.volume2On;
        this.musicVolume1       = this.volume1On;
        this.sfxVolume6         = this.volume6On;
        this.sfxVolume5         = this.volume5On;
        this.sfxVolume4         = this.volume4On;
        this.sfxVolume3         = this.volume3On;
        this.sfxVolume2         = this.volume2On;
        this.sfxVolume1         = this.volume1On;

        switch(musicVolume) { //no break, in chain
            case 1:
                this.musicVolume2 = this.volume2Off;
            case 2:
                this.musicVolume3 = this.volume3Off;
            case 3:
                this.musicVolume4 = this.volume4Off;
            case 4:
                this.musicVolume5 = this.volume5Off;    
            case 5:
                this.musicVolume6 = this.volume6Off;    
        }

        switch(sfxVolume) { //no break, in chain
            case 1:
                this.sfxVolume2 = this.volume2Off;
            case 2:
                this.sfxVolume3 = this.volume3Off;
            case 3:
                this.sfxVolume4 = this.volume4Off;
            case 4:
                this.sfxVolume5 = this.volume5Off;    
            case 5:
                this.sfxVolume6 = this.volume6Off;    
        }
    }

    /**
     * Draw method
     * @param frametime
     */    
    public synchronized void draw(long frametime) {

        this.g2d.setBackground(new Color(0, 66, 147));
        this.g2d.clearRect(0, 0, resolutionW, resolutionH);
        
        this.g2d.drawImage(this.selector,           0, this.selectorP, null);
        
        this.g2d.drawImage(this.optionsLogo,        852, 23, null);
        this.g2d.drawImage(this.labelPlayMusic,     69, 218, null);
        this.g2d.drawImage(this.labelPlaySfx,       69, 303, null);
        this.g2d.drawImage(this.labelMusicVolume,   69, 388, null);
        this.g2d.drawImage(this.labelSfxVolume,     69, 472, null);
        this.g2d.drawImage(this.toogleMusic,        1141, 218, null);
        this.g2d.drawImage(this.toogleSfx,          1141, 303, null);
        this.g2d.drawImage(this.labelExit,          69, 685, null);

        this.g2d.drawImage(this.musicVolume6,       1141, 392, null);
        this.g2d.drawImage(this.musicVolume5,       1172, 396, null);
        this.g2d.drawImage(this.musicVolume4,       1203, 400, null);
        this.g2d.drawImage(this.musicVolume3,       1234, 404, null);
        this.g2d.drawImage(this.musicVolume2,       1265, 408, null);
        this.g2d.drawImage(this.musicVolume1,       1296, 412, null);

        this.g2d.drawImage(this.sfxVolume6,         1141, 476, null);
        this.g2d.drawImage(this.sfxVolume5,         1172, 480, null);
        this.g2d.drawImage(this.sfxVolume4,         1203, 484, null);
        this.g2d.drawImage(this.sfxVolume3,         1234, 488, null);
        this.g2d.drawImage(this.sfxVolume2,         1265, 492, null);
        this.g2d.drawImage(this.sfxVolume1,         1296, 496, null);

    }

    /**
     * 
     * @param frametime
     */
    public synchronized void firstUpdate(long frametime) {
    }

    //getters
    public boolean goMenu()                     {   return (this.goMenu);                   }
    public Graphics2D getG2D()					{ 	return (this.gameRef.getG2D());		    }

    /**
     * 
     * @param key
     */
    public void keyMovement(int key) {
        if (key == 38) { //UP
            this.selectorPosition = (byte)(this.selectorPosition-1);
            if (this.selectorPosition < 0) {
                this.selectorPosition = TOTAL_OPTIONS - 1;
            }
            this.item.play();
        } else if (key == 40) { //DOWN
            this.selectorPosition = (byte)((this.selectorPosition+1)%TOTAL_OPTIONS);
            this.item.play();
        } else if (key == 37) { //LEFT

            if (this.selectorPosition == 0) {
                this.isMusicOn = !this.isMusicOn;
                if (!this.isMusicOn) {
                    this.gameRef.audioMuteControl(Audio.MUSIC, true);
                } else {
                    this.gameRef.audioMuteControl(Audio.MUSIC, false);
                }
            } else if (this.selectorPosition == 1) {
                this.isSfxOn = !this.isSfxOn;
                if (!this.isSfxOn) {
                    this.gameRef.audioMuteControl(Audio.SFX, true);
                } else {
                    this.gameRef.audioMuteControl(Audio.SFX, false);
                }
            } else if (this.selectorPosition == 2) {
                this.musicVolume = (byte)((this.musicVolume + 1)%7);
                if (this.musicVolume == 0) {
                    this.musicVolume = 1;
                }
            } else if (this.selectorPosition == 3) {
                this.sfxVolume = (byte)((this.sfxVolume + 1)%7);
                if (this.sfxVolume == 0) {
                    this.sfxVolume = 1;
                }
            }
        } else if (key == 39) { //RIGHT
            if (this.selectorPosition == 0) {
                this.isMusicOn = !this.isMusicOn;
                if (!this.isMusicOn) {
                    this.gameRef.audioMuteControl(Audio.MUSIC, true);
                } else {
                    this.gameRef.audioMuteControl(Audio.MUSIC, false);
                }
            } else if (this.selectorPosition == 1) {
                this.isSfxOn = !this.isSfxOn;
                if (!this.isSfxOn) {
                    this.gameRef.audioMuteControl(Audio.SFX, true);
                } else {
                    this.gameRef.audioMuteControl(Audio.SFX, false);
                }
            } else if (this.selectorPosition == 2) {
                this.musicVolume--;
                if (this.musicVolume < 1) {
                    this.musicVolume = 6;
                }                
            } else if (this.selectorPosition == 3) {
                this.sfxVolume--;
                if (this.sfxVolume < 1) {
                    this.sfxVolume = 6;
                }
            }
        } else if (key == 10) { //ENTER
            if (this.selectorPosition == (TOTAL_OPTIONS - 1)) {
                this.goMenu = true;
            }
        } else if (key == 27) { //Esc - Back to Menu
            this.goMenu = true;
        }
    }

    /**
     * Reset game options
     */
    public void reset() {
        this.selectorPosition = 0;
        this.goMenu = false;
    }
}