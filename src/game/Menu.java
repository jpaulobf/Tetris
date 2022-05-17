package game;

import interfaces.GameInterface;
import java.awt.Graphics2D;
import java.awt.Color;
import util.LoadingStuffs;
import java.awt.image.BufferedImage;

public class Menu {
    
    @SuppressWarnings("unused")
    private volatile long framecounter      = 0L;
    private GameInterface gameRef           = null;

    private volatile boolean goOptions      = false;
    private volatile boolean goGame         = false;
    private volatile boolean goExit         = false;

    private BufferedImage tlogo             = null;
    private BufferedImage selector          = null;
    private BufferedImage labelPlay         = null;
    private BufferedImage labelOptions      = null;
    private BufferedImage labelExit         = null;

    private BufferedImage starSelected      = null;
    private BufferedImage starUnselected    = null;
    private BufferedImage [] stars          = null;

    private byte selectorPosition           = 0;
    private byte selectors[]                = {0, 2, 3};
    private short optionsSpace              = 65;
    private byte level                      = 1;

    /**
     * Constructor
     * @param game
     */
    public Menu(GameInterface game) {

        this.gameRef            = game;
        this.tlogo              = (BufferedImage)LoadingStuffs.getInstance().getStuff("tlogo");
        this.selector           = (BufferedImage)LoadingStuffs.getInstance().getStuff("selector");

        this.labelPlay          = (BufferedImage)LoadingStuffs.getInstance().getStuff("lplaygame");
        this.labelOptions       = (BufferedImage)LoadingStuffs.getInstance().getStuff("loptions");
        this.labelExit          = (BufferedImage)LoadingStuffs.getInstance().getStuff("lexit");

        this.starSelected       = (BufferedImage)LoadingStuffs.getInstance().getStuff("starSelected");
        this.starUnselected     = (BufferedImage)LoadingStuffs.getInstance().getStuff("starUnselected");

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

    private void allStarsUnselected() {
        for (byte i = 0; i < this.stars.length; i++) {
            this.stars[i] = this.starUnselected;
        }
    }
    
    public synchronized void draw(long frametime) {
       
        this.getG2D().setBackground(new Color(0, 66, 147));
        this.getG2D().clearRect(0, 0, this.gameRef.getInternalResolutionWidth(), this.gameRef.getInternalResolutionHeight());

        this.getG2D().drawImage(this.tlogo, 401, 18, null);
        this.getG2D().drawImage(this.selector, 116, 480 + (selectors[selectorPosition] * optionsSpace), null);

        this.getG2D().drawImage(this.labelPlay, 545, 475, null);
        this.getG2D().drawImage(this.labelOptions, 611, 605, null);
        this.getG2D().drawImage(this.labelExit, 592, 670, null);

        this.getG2D().drawImage(this.stars[0], 500, 527, null);
        this.getG2D().drawImage(this.stars[1], 546, 527, null);
        this.getG2D().drawImage(this.stars[2], 593, 527, null);
        this.getG2D().drawImage(this.stars[3], 639, 527, null);
        this.getG2D().drawImage(this.stars[4], 686, 527, null);
        this.getG2D().drawImage(this.stars[5], 732, 527, null);
        this.getG2D().drawImage(this.stars[6], 779, 527, null);
        this.getG2D().drawImage(this.stars[7], 826, 527, null);

    }

    public synchronized void firstUpdate(long frametime) {
        
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
        } else if (key == 40) { //DOWN
            if (this.selectorPosition < 2) {
                this.selectorPosition++;
            } else {
                this.selectorPosition = 0;
            }
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
