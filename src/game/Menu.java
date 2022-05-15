package game;

import interfaces.GameInterface;
import java.awt.Graphics2D;
import java.awt.Color;
import util.LoadingStuffs;
import java.awt.image.BufferedImage;

public class Menu {
    
    @SuppressWarnings("unused")
    private volatile long framecounter  = 0L;
    private GameInterface gameRef       = null;

    private volatile boolean goOptions  = false;
    private volatile boolean goGame     = false;
    private volatile boolean goExit     = false;

    private BufferedImage tlogo         = null;
    private BufferedImage selector      = null;

    private BufferedImage labelPlay     = null;
    private BufferedImage labelOptions  = null;
    private BufferedImage labelExit     = null;


    private byte selectorPosition       = 0;
    private byte selectors[]            = {0, 2, 3};
    private short optionsSpace          = 65;

    /**
     * Constructor
     * @param game
     */
    public Menu(GameInterface game) {
        this.gameRef        = game;
        this.tlogo          = (BufferedImage)LoadingStuffs.getInstance().getStuff("tlogo");
        this.selector       = (BufferedImage)LoadingStuffs.getInstance().getStuff("selector");

        this.labelPlay      = (BufferedImage)LoadingStuffs.getInstance().getStuff("lplaygame");
        this.labelOptions   = (BufferedImage)LoadingStuffs.getInstance().getStuff("loptions");
        this.labelExit      = (BufferedImage)LoadingStuffs.getInstance().getStuff("lexit");

    }

    public synchronized void update(long frametime) {

    }
    
    public synchronized void draw(long frametime) {
       
        this.getG2D().setBackground(new Color(0, 66, 147));
        this.getG2D().clearRect(0, 0, this.gameRef.getInternalResolutionWidth(), this.gameRef.getInternalResolutionHeight());

        this.getG2D().drawImage(this.tlogo, 401, 18, null);
        this.getG2D().drawImage(this.selector, 116, 480 + (selectors[selectorPosition] * optionsSpace), null);

        this.getG2D().drawImage(this.labelPlay, 545, 475, null);
        this.getG2D().drawImage(this.labelOptions, 611, 605, null);
        this.getG2D().drawImage(this.labelExit, 592, 670, null);
    }

    public synchronized void firstUpdate(long frametime) {
        
    }

    //getters
    public boolean goOptions()                  {   return (this.goOptions);            }   
    public boolean goGame()                     {   return (this.goGame);               }
    public boolean goExit()                     {   return (this.goExit);               }
    public Graphics2D getG2D()					{ 	return (this.gameRef.getG2D());		}
}
