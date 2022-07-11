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

    //sounds
    private Audio start                     = null;

    //menu control
    private byte selectorPosition           = 0;
    private volatile boolean goMenuApply    = false;
    private volatile boolean goMenuCancel   = false;
    private int resolutionW                 = 0;
    private int resolutionH                 = 0;

    public void reset() {
        this.goMenuApply = false;
        this.goMenuCancel = false;
    }

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
        this.g2d.clearRect(0, 0, resolutionW, resolutionH);
        this.g2d.drawImage(this.selector, 116, 480, null);
        this.g2d.drawString("Aqui...", 100, 100);
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
}