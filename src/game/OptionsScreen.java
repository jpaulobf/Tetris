package game;

import interfaces.GameInterface;
import java.awt.Graphics2D;
import java.awt.Color;
import util.LoadingStuffs;
import java.awt.image.BufferedImage;
import util.Audio;

public class OptionsScreen {
    
    //game parameters
    //private volatile long framecounter      = 0L;
    private GameInterface gameRef           = null;
    private Graphics2D g2d                  = null;
    private BufferedImage selector          = null;
    private BufferedImage optionsLogo       = null;
    private BufferedImage labelPlayMusic    = null;
    private BufferedImage toogleOn          = null;
    private BufferedImage toogleOff         = null;
    private BufferedImage toogleMusic       = null;
    private BufferedImage toogleSfx         = null;
    private BufferedImage toogleGhost       = null;
    private BufferedImage toogleHold        = null;
    private BufferedImage labelPlaySfx      = null;
    private BufferedImage labelMusicVolume  = null;
    private BufferedImage labelSfxVolume    = null;
    private BufferedImage labelExit         = null;
    private BufferedImage labelGhostPiece   = null;
    private BufferedImage labelHoldPiece    = null;
    private BufferedImage labelHowManyNext  = null;
    private BufferedImage [] volumeOn       = null;
    private BufferedImage [] volumeOff      = null;
    private BufferedImage [] musicVolIcon   = null;
    private BufferedImage [] sfxVolIcon     = null;
    private BufferedImage [] howManyIcon    = null;
    private final byte TOTAL_OPTIONS        = 8;

    //sounds
    private Audio item                      = null;

    //menu control
    private final short SELECTOR_START      = 193;
    private final byte SELECTOR_DIFF        = 69;
    private byte selectorPosition           = 0;
    private volatile boolean goMenu         = false;
    private int resolutionW                 = 0;
    private int resolutionH                 = 0;
    private short selectorO                 = SELECTOR_START;
    private boolean isMusicOn               = true;
    private boolean isSfxOn                 = true;
    private boolean isGhostOn               = true;
    private boolean isHoldOn                = true;
    private short selectorP                 = (short)(selectorO + (selectorPosition * SELECTOR_DIFF));
    private byte musicVolume                = 6;
    private byte sfxVolume                  = 6;
    private byte howManyNext                = 6;

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

        //define images arrays
        this.volumeOn           = new BufferedImage[6];
        this.volumeOff          = new BufferedImage[6];
        this.musicVolIcon       = new BufferedImage[6];
        this.sfxVolIcon         = new BufferedImage[6];
        this.howManyIcon        = new BufferedImage[6];

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
        this.labelGhostPiece    = LoadingStuffs.getInstance().getImage("label-ghost-piece");
        this.labelHoldPiece     = LoadingStuffs.getInstance().getImage("label-hold-piece");
        this.labelHowManyNext   = LoadingStuffs.getInstance().getImage("label-how-many-next");
        

        //get the volume icons
        this.volumeOn[0]        = LoadingStuffs.getInstance().getImage("v1-on");
        this.volumeOn[1]        = LoadingStuffs.getInstance().getImage("v2-on");
        this.volumeOn[2]        = LoadingStuffs.getInstance().getImage("v3-on");
        this.volumeOn[3]        = LoadingStuffs.getInstance().getImage("v4-on");
        this.volumeOn[4]        = LoadingStuffs.getInstance().getImage("v5-on");
        this.volumeOn[5]        = LoadingStuffs.getInstance().getImage("v6-on");
        this.volumeOff[0]       = LoadingStuffs.getInstance().getImage("v1-off");
        this.volumeOff[1]       = LoadingStuffs.getInstance().getImage("v2-off");
        this.volumeOff[2]       = LoadingStuffs.getInstance().getImage("v3-off");
        this.volumeOff[3]       = LoadingStuffs.getInstance().getImage("v4-off");
        this.volumeOff[4]       = LoadingStuffs.getInstance().getImage("v5-off");
        this.volumeOff[5]       = LoadingStuffs.getInstance().getImage("v6-off");

        //define the music & sfx toogle image
        this.toogleMusic        = this.toogleOn;
        this.toogleSfx          = this.toogleOn;
        this.toogleGhost        = this.toogleOn;
        this.toogleHold         = this.toogleOn;

        //define the music volume images
        for (byte i = 0; i < this.volumeOn.length; i++) {
            this.musicVolIcon[i]    = this.volumeOn[i];
            this.sfxVolIcon[i]      = this.volumeOn[i];
            this.howManyIcon[i]     = this.volumeOn[i];
        }

        //load the sounds
        this.item = LoadingStuffs.getInstance().getAudio("star");
    }

    /**
     * Game logic update
     * @param frametime
     */
    public synchronized void update(long frametime) {
        
        //calc the selector position
        this.selectorP = (short)(this.selectorO + (this.selectorPosition * SELECTOR_DIFF));
       
        //if the selector is in the exit option (go 22 pixel dows)
        if (this.selectorPosition == TOTAL_OPTIONS - 1) {
            this.selectorP += 30;
        }

        //define the music toogle button
        if (this.isMusicOn) {
            this.toogleMusic = this.toogleOn;
        } else {
            this.toogleMusic = this.toogleOff;
        }

        //define the sfx toogle button
        if (this.isSfxOn) {
            this.toogleSfx = this.toogleOn;
        } else {
            this.toogleSfx = this.toogleOff;
        }

        //define the ghost toogle button
        if (this.isGhostOn) {
            this.toogleGhost = this.toogleOn;
        } else {
            this.toogleGhost = this.toogleOff;
        }

        //define the hold toogle button
        if (this.isHoldOn) {
            this.toogleHold = this.toogleOn;
        } else {
            this.toogleHold = this.toogleOff;
        }

        //define the volume image
        for (byte i = 0; i < this.volumeOn.length; i++) {
            this.musicVolIcon[i]    = this.volumeOn[i];
            this.sfxVolIcon[i]      = this.volumeOn[i];
            this.howManyIcon[i]     = this.volumeOn[i];
        }

        switch(this.musicVolume) { //no break, execute in chain
            case 1:
                this.musicVolIcon[1] = this.volumeOff[1];
            case 2:
                this.musicVolIcon[2] = this.volumeOff[2];
            case 3:
                this.musicVolIcon[3] = this.volumeOff[3];
            case 4:
                this.musicVolIcon[4] = this.volumeOff[4];    
            case 5:
                this.musicVolIcon[5] = this.volumeOff[5];    
        }

        switch(this.sfxVolume) { //no break, execute in chain
            case 1:
                this.sfxVolIcon[1] = this.volumeOff[1];
            case 2:
                this.sfxVolIcon[2] = this.volumeOff[2];
            case 3:
                this.sfxVolIcon[3] = this.volumeOff[3];
            case 4:
                this.sfxVolIcon[4] = this.volumeOff[4];
            case 5:
                this.sfxVolIcon[5] = this.volumeOff[5];
        }

        switch(this.howManyNext) { //no break, execute in chain
            case 1:
                this.howManyIcon[1] = this.volumeOff[1];
            case 2:
                this.howManyIcon[2] = this.volumeOff[2];
            case 3:
                this.howManyIcon[3] = this.volumeOff[3];
            case 4:
                this.howManyIcon[4] = this.volumeOff[4];
            case 5:
                this.howManyIcon[5] = this.volumeOff[5];
        }
    }

    /**
     * Draw method
     * @param frametime
     */    
    public synchronized void draw(long frametime) {

        this.g2d.setBackground(new Color(0, 66, 147));
        this.g2d.clearRect(0, 0, resolutionW, resolutionH);
        
        //selector
        this.g2d.drawImage(this.selector,           0, this.selectorP, null);
        
        //logo
        this.g2d.drawImage(this.optionsLogo,        852, 23, null);
        
        //labels
        this.g2d.drawImage(this.labelPlayMusic,     128, 188, null);
        this.g2d.drawImage(this.labelMusicVolume,   128, 258, null);
        
        //music volume & toogle
        this.g2d.drawImage(this.toogleMusic,        1141, 188, null);
        this.g2d.drawImage(this.musicVolIcon[5],    1141, 261, null);
        this.g2d.drawImage(this.musicVolIcon[4],    1172, 265, null);
        this.g2d.drawImage(this.musicVolIcon[3],    1203, 269, null);
        this.g2d.drawImage(this.musicVolIcon[2],    1234, 273, null);
        this.g2d.drawImage(this.musicVolIcon[1],    1265, 277, null);
        this.g2d.drawImage(this.musicVolIcon[0],    1296, 281, null);

        //labels
        this.g2d.drawImage(this.labelPlaySfx,       128, 327, null);
        this.g2d.drawImage(this.labelSfxVolume,     128, 396, null);

        //music volume & toogle
        this.g2d.drawImage(this.toogleSfx,          1141, 327, null);
        this.g2d.drawImage(this.sfxVolIcon[5],      1141, 399, null);
        this.g2d.drawImage(this.sfxVolIcon[4],      1172, 403, null);
        this.g2d.drawImage(this.sfxVolIcon[3],      1203, 407, null);
        this.g2d.drawImage(this.sfxVolIcon[2],      1234, 411, null);
        this.g2d.drawImage(this.sfxVolIcon[1],      1265, 415, null);
        this.g2d.drawImage(this.sfxVolIcon[0],      1296, 419, null);

        //labels
        this.g2d.drawImage(this.labelGhostPiece,    128, 465, null);
        this.g2d.drawImage(this.labelHoldPiece,     128, 534, null);
        this.g2d.drawImage(this.labelHowManyNext,   128, 603, null);

        //toogle
        this.g2d.drawImage(this.toogleGhost,        1141, 465, null);
        this.g2d.drawImage(this.toogleHold,         1141, 534, null);
        this.g2d.drawImage(this.howManyIcon[5],     1141, 606, null);
        this.g2d.drawImage(this.howManyIcon[4],     1172, 610, null);
        this.g2d.drawImage(this.howManyIcon[3],     1203, 614, null);
        this.g2d.drawImage(this.howManyIcon[2],     1234, 618, null);
        this.g2d.drawImage(this.howManyIcon[1],     1265, 622, null);
        this.g2d.drawImage(this.howManyIcon[0],     1296, 626, null);
        
        //label exit
        this.g2d.drawImage(this.labelExit,          38, 695, null);
    }

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

        } else if (key == 37 || key == 39) { //LEFT

            if (this.selectorPosition == 0) {
                this.isMusicOn = !this.isMusicOn;
                if (!this.isMusicOn) {
                    this.gameRef.audioMuteControl(Audio.MUSIC, true);
                } else {
                    this.gameRef.audioMuteControl(Audio.MUSIC, false);
                }

                //todo: disable music volume

            } else if (this.selectorPosition == 1 && key == 37) { //music - left
                this.musicVolume = (byte)((this.musicVolume + 1)%7);
                if (this.musicVolume == 0) {
                    this.musicVolume = 1;
                    this.gameRef.decVolumeMusic(25f);
                } else {
                    this.gameRef.incVolumeMusic(5f);
                }
            } else if (this.selectorPosition == 1 && key == 39) { //music - right
                this.musicVolume--;
                if (this.musicVolume < 1) {
                    this.musicVolume = 6;
                    this.gameRef.incVolumeMusic(25f);
                } else {
                    this.gameRef.decVolumeMusic(5f);
                }
            } else if (this.selectorPosition == 2) {
                this.isSfxOn = !this.isSfxOn;
                if (!this.isSfxOn) {
                    this.gameRef.audioMuteControl(Audio.SFX, true);
                } else {
                    this.gameRef.audioMuteControl(Audio.SFX, false);
                }

                //todo: disable sfx volume

            } else if (this.selectorPosition == 3 && key == 37) { //sfx - left
                this.sfxVolume = (byte)((this.sfxVolume + 1)%7);
                if (this.sfxVolume == 0) {
                    this.sfxVolume = 1;
                    this.gameRef.decVolumeSFX(25f);
                } else {
                    this.gameRef.incVolumeSFX(5f);
                }
            } else if (this.selectorPosition == 3 && key == 39) { //sfx - right
                this.sfxVolume--;
                if (this.sfxVolume < 1) {
                    this.sfxVolume = 6;
                    this.gameRef.incVolumeSFX(25f);
                } else {
                    this.gameRef.decVolumeSFX(5f);
                }
            } else if (this.selectorPosition == 4) {
                this.isGhostOn = !this.isGhostOn;
                this.gameRef.setIsToAllowGhostPiece(this.isGhostOn);
            } else if (this.selectorPosition == 5) {
                this.isHoldOn = !this.isHoldOn;
                this.gameRef.setIsToAllowHold(this.isHoldOn);
            } else if (this.selectorPosition == 6 && key == 37) { //sfx - left
                this.howManyNext = (byte)((this.howManyNext + 1)%7);
                if (this.howManyNext == 0) {
                    this.howManyNext = 1;
                }
                this.gameRef.setHowManyNextPieces(this.howManyNext);
            } else if (this.selectorPosition == 6 && key == 39) { //sfx - right
                this.howManyNext--;
                if (this.howManyNext < 1) {
                    this.howManyNext = 6;
                }
                this.gameRef.setHowManyNextPieces(this.howManyNext);
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

    /**
     * First option execution.
     * @param frametime
     */
    public synchronized void firstUpdate(long frametime) {

    }

    //getters
    public boolean goMenu()                     {   return (this.goMenu);                   }
    public Graphics2D getG2D()					{ 	return (this.gameRef.getG2D());		    }
}