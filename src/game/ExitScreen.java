package game;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import interfaces.GameInterface;
import util.LoadingStuffs;
import java.awt.Color;
import java.awt.image.BufferedImage;
import util.Audio;

/**
 * Exitscreen class
 */
public class ExitScreen {

    private GameInterface game                  = null;
    private Graphics2D g2d                      = null;
    
    private short positionX                     = 0;
    private short positionY                     = 0;
    private short width                         = 300;
    private short height                        = 150;
    private short buttonWidth                   = 80;
    private short buttonHeight                  = 32;
    private int resolutionH                     = 0;
    private int resolutionW                     = 0;
    private short mainBoxMinWidth               = 0;
    private short mainBoxMinHeight              = 0;
    private short mainBoxCurWidth               = this.mainBoxMinWidth;
    private short mainBoxCurHeight              = this.mainBoxMinHeight;
    private short centerX                       = 0;
    private short centerY                       = 0;
    private short reallyXPosition               = -1000;
    private short reallyYPosition               = -1000;

    private Rectangle2D.Double mainBox          = null;
    private Rectangle2D.Double yesBox           = null;
    private Rectangle2D.Double noBox            = null;
    
    private volatile long framecounter          = 0;
    private final byte animationStep            = 20;
    private final byte halfStep                 = animationStep/2;
    
    private BufferedImage really  				= null;
    private Audio opening                       = null;
    private Audio closing                       = null;

    /**
     * Constructor
     * @param g2d
     * @param resolutionW
     * @param resolutionH
     */
    public ExitScreen(GameInterface game, int resolutionW, int resolutionH) {

        //store the screen resolution
        this.resolutionW = resolutionW;
        this.resolutionH = resolutionH;

        //define the box x, y, w, h
        this.mainBox    = new Rectangle2D.Double(-width, -height, width, height);
        this.yesBox     = new Rectangle2D.Double(-width, -height, buttonWidth, buttonHeight);
        this.noBox      = new Rectangle2D.Double(-width, -height, buttonWidth, buttonHeight);

        //store the game object & the Graphics2D
        this.game       = game;
        this.g2d        = this.getG2D();

        //load the question image
        this.really     = (BufferedImage)LoadingStuffs.getInstance().getStuff("really");

        //store the sounds
        this.opening    = (Audio)LoadingStuffs.getInstance().getStuff("opening");
        this.closing    = (Audio)LoadingStuffs.getInstance().getStuff("closing");
    }

    /**
     * Draw method
     * @param frametime
     */
    public void draw(long frametime) {
      
        //main box shadow
        this.g2d.setColor(Color.BLACK);
        this.g2d.fillRect((int)mainBox.x + 1, (int)mainBox.y + 1, (int)mainBox.width, (int)mainBox.height);
        
        //draw the main box 
        this.g2d.setColor(new Color(0,66,147));
        this.g2d.fillRect((int)mainBox.x, (int)mainBox.y, (int)mainBox.width, (int)mainBox.height);

        //yes button shadow
        this.g2d.setColor(Color.black);
        this.g2d.fillRect((int)yesBox.x + 1, (int)yesBox.y + 1, (int)yesBox.width, (int)yesBox.height);

        //draw yes buttom
        this.g2d.setColor(Color.red);
        this.g2d.fillRect((int)yesBox.x, (int)yesBox.y, (int)yesBox.width, (int)yesBox.height);

        //no button shadow
        this.g2d.setColor(Color.black);
        this.g2d.fillRect((int)noBox.x + 1, (int)noBox.y + 1, (int)noBox.width, (int)noBox.height);

        //draw the no buttom
        this.g2d.setColor(Color.blue);
        this.g2d.fillRect((int)noBox.x, (int)noBox.y, (int)noBox.width, (int)noBox.height);

        //draw the question image
        this.g2d.drawImage(this.really, this.reallyXPosition, this.reallyYPosition, null);
    }

    /**
     * Play the open menu sound (from outside) 
     */
    public void playOpening() {
        if (this.opening != null) {
            this.opening.play();
        }
    }

    /**
     * Update the exit screen window
     * @param frametime
     */
    public void update(long frametime) {

        //update framecounter
        this.framecounter += frametime;
        
        //calc the box width / height / position
        //Animate
        if (this.mainBoxCurWidth < this.width) {
            if (this.framecounter > 10_000_000) {

                //calc current width/height
                this.mainBoxCurWidth    += animationStep;
                this.mainBoxCurHeight   += halfStep;

                //calc current x/y position
                this.positionX = (short)((this.resolutionW / 2) - (this.mainBoxCurWidth / 2));
                this.positionY = (short)((this.resolutionH / 2) - (this.mainBoxCurHeight / 2));

                //reset
                this.framecounter = 0;
            }
        } else {
            //final main box form
            if (this.mainBoxCurWidth >= this.width || this.mainBoxCurHeight >= this.height) {
                //run once
                if (this.framecounter == frametime) {

                    //define the definitive width/height
                    this.mainBoxCurWidth    = this.width;
                    this.mainBoxCurHeight   = this.height;

                    //define the definitive x/y position
                    this.positionX  = (short)((resolutionW / 2) - (this.width / 2));
                    this.positionY  = (short)((resolutionH / 2) - (this.height / 2));

                    //define yes box button position
                    this.yesBox.x   = this.positionX + 50;
                    this.yesBox.y   = this.positionY + 80;

                    //define no box button position
                    this.noBox.x    = this.positionX + 170;
                    this.noBox.y    = this.positionY + 80;

                    this.reallyXPosition = (short)(this.positionX + ((this.width/2) - (this.really.getWidth()/2)));
                    this.reallyYPosition = (short)(this.positionY + 35);
                }
            }
        }

        //store the x, y, w, h values
        this.mainBox.x      = this.positionX;
        this.mainBox.y      = this.positionY;
        this.mainBox.width  = this.mainBoxCurWidth;
        this.mainBox.height = this.mainBoxCurHeight;
    }

    /**
     * Treat the key movement
     * @param keyCode
     */
    public void move(int keyCode) {

    }

    public int action() {
        return 0;
    }

    //getter
    public Graphics2D getG2D()  { 	return (this.game.getG2D());    }
}