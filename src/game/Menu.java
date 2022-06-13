package game;

import interfaces.GameInterface;
import java.awt.Graphics2D;
import java.awt.Color;
import util.LoadingStuffs;
import java.awt.image.BufferedImage;
import util.Audio;

/**
 * Class representing the menu
 */
public class Menu {
    
    @SuppressWarnings("unused")
    private volatile long framecounter      = 0L;
    private GameInterface gameRef           = null;
    private Graphics2D g2d                  = null;

    //images
    private BufferedImage tlogo             = null;
    private BufferedImage selector          = null;
    private BufferedImage labelPlay         = null;
    private BufferedImage labelOptions      = null;
    private BufferedImage labelExit         = null;
    private BufferedImage starSelected      = null;
    private BufferedImage starUnselected    = null;
    private BufferedImage [] stars          = null;

    //sounds
    private Audio intro                     = null;
    private Audio item                      = null;

    //menu control
    private byte selectorPosition           = 0;
    private byte selectors[]                = {0, 2, 3};
    private short optionsSpace              = 65;
    private byte level                      = 1;
    private volatile boolean goOptions      = false;
    private volatile boolean goGame         = false;
    private volatile boolean goExit         = false;
    private final Color menuColor           = new Color(0, 66, 147);
    private int resolutionW                 = 0;
    private int resolutionH                 = 0;

    /**
     * Constructor
     * @param game
     */
    public Menu(GameInterface game) {

        //get the game pointer
        this.gameRef            = game;
        this.g2d                = this.getG2D();
        this.resolutionW        = this.gameRef.getInternalResolutionWidth();
        this.resolutionH        = this.gameRef.getInternalResolutionHeight();

        //load the images
        this.tlogo              = (BufferedImage)LoadingStuffs.getInstance().getStuff("tlogo");
        this.selector           = (BufferedImage)LoadingStuffs.getInstance().getStuff("selector");
        this.labelPlay          = (BufferedImage)LoadingStuffs.getInstance().getStuff("lplaygame");
        this.labelOptions       = (BufferedImage)LoadingStuffs.getInstance().getStuff("loptions");
        this.labelExit          = (BufferedImage)LoadingStuffs.getInstance().getStuff("lexit");
        this.starSelected       = (BufferedImage)LoadingStuffs.getInstance().getStuff("starSelected");
        this.starUnselected     = (BufferedImage)LoadingStuffs.getInstance().getStuff("starUnselected");

        //load the sounds
        this.intro              = (Audio)LoadingStuffs.getInstance().getStuff("intro");
        this.item               = (Audio)LoadingStuffs.getInstance().getStuff("menuitem");

        //create stars array
        this.stars              = new BufferedImage[8];
        this.stars[0]           = this.starSelected;
        this.stars[1]           = this.starUnselected;
        this.stars[2]           = this.starUnselected;
        this.stars[3]           = this.starUnselected;
        this.stars[4]           = this.starUnselected;
        this.stars[5]           = this.starUnselected;
        this.stars[6]           = this.starUnselected;
        this.stars[7]           = this.starUnselected;
    }

    /**
     * Game logic update
     * @param frametime
     */
    public synchronized void update(long frametime) {
        for (byte i = 0; i < this.level; i++) {
            this.stars[i] = this.starSelected;
        }
    }

    /**
     * Revert each start image to unselected
     */
    private void allStarsUnselected() {
        for (byte i = 0; i < this.stars.length; i++) {
            this.stars[i] = this.starUnselected;
        }
    }

    /**
     * Stop the menu music
     */
    public void stopMusic() {
        this.intro.stop();
    }

    /**
     * Draw method
     * @param frametime
     */    
    public synchronized void draw(long frametime) {
        this.g2d.setBackground(this.menuColor);
        this.g2d.clearRect(0, 0, resolutionW, resolutionH);
        this.g2d.drawImage(this.tlogo, 401, 18, null);
        this.g2d.drawImage(this.selector, 116, 480 + (selectors[selectorPosition] * optionsSpace), null);
        this.g2d.drawImage(this.labelPlay, 545, 475, null);
        this.g2d.drawImage(this.labelOptions, 611, 605, null);
        this.g2d.drawImage(this.labelExit, 592, 670, null);
        this.g2d.drawImage(this.stars[0], 500, 527, null);
        this.g2d.drawImage(this.stars[1], 546, 527, null);
        this.g2d.drawImage(this.stars[2], 593, 527, null);
        this.g2d.drawImage(this.stars[3], 639, 527, null);
        this.g2d.drawImage(this.stars[4], 686, 527, null);
        this.g2d.drawImage(this.stars[5], 732, 527, null);
        this.g2d.drawImage(this.stars[6], 779, 527, null);
        this.g2d.drawImage(this.stars[7], 826, 527, null);
    }

    /**
     * 
     * @param frametime
     */
    public synchronized void firstUpdate(long frametime) {
        this.intro.playContinuously();
    }

    //getters
    public boolean goOptions()                  {   return (this.goOptions);            }   
    public boolean goGame()                     {   return (this.goGame);               }
    public boolean goExit()                     {   return (this.goExit);               }
    public Graphics2D getG2D()					{ 	return (this.gameRef.getG2D());		}

    /**
     * 
     * @param key
     */
    public void keyMovement(int key) {
        
        if (key == 38) { //UP
            if (this.selectorPosition > 0) {
                this.selectorPosition--;
            } else {
                this.selectorPosition = 2;
            }
            this.item.play();
        } else if (key == 40) { //DOWN
            if (this.selectorPosition < 2) {
                this.selectorPosition++;
            } else {
                this.selectorPosition = 0;
            }
            this.item.play();
        } else if (key == 10) { //ENTER
            this.goExit     = false;
            this.goOptions  = false;
            this.goGame     = false;
            if (this.selectorPosition == 0) {
                this.goGame = true;
            } else if (this.selectorPosition == 1) {
                this.goOptions = true;
            } else if (this.selectorPosition == 2) {
                this.goExit = true;
            }
        }

        if (this.selectorPosition == 0) {
            this.allStarsUnselected();
            if (key == 37) { //LEFT
                if (this.level > 1) {
                    this.level--;
                } else {
                    this.level = 8;
                }
            } else if (key == 39) { //RIGHT
                if (this.level < 8) {
                    this.level++;
                } else {
                    this.level = 1;
                }
            }
        }
    }

    public byte getLevel() {return (this.level);}
}
