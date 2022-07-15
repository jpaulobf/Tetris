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

        this.toogleMusic        = this.toogleOn;
        this.toogleSfx          = this.toogleOn;

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

        this.g2d.drawImage(this.toogleMusic, 1141, 218, null);

        this.g2d.drawImage(this.toogleSfx, 1141, 303, null);


        //selector x=(0), 223, 308, 393, 478, 696
        //label music x=69, y=218
        //label sfx x=69, y=303
        //label music vol x=69, y=388
        //label sfx vol x=69, y=472
        //exit x=69, y=685
        //v6 1 x=1141, y=392
        //v5 1 x=1172, y=396
        //v4 1 x=1203, y=400
        //v3 1 x=1234, y=404
        //v2 1 x=1265, y=408
        //v1 1 x=1296, y=412
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