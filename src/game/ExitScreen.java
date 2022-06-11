package game;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import interfaces.GameInterface;
import util.LoadingStuffs;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;

/**
 * Exitscreen class
 */
public class ExitScreen {

    private short positionX                     = 0;
    private short positionY                     = 0;
    private short width                         = 300;
    private short height                        = 150;
    private short buttonWidth                   = 80;
    private short buttonHeight                  = 32;
    private int resolutionH                     = 0;
    private int resolutionW                     = 0;
    private GameInterface game                  = null;
    private Rectangle2D.Double mainBox          = null;
    private Rectangle2D.Double yesBox           = null;
    private Rectangle2D.Double noBox            = null;
    private short mainBoxMinWidth               = 0;
    private short mainBoxMinHeight              = 0;
    private short mainBoxCurWidth               = this.mainBoxMinWidth;
    private short mainBoxCurHeight              = this.mainBoxMinHeight;
    private short centerX                       = 0;
    private short centerY                       = 0;
    private volatile long framecounter          = 0;
    private final byte step                     = 20;
    private final byte halfStep                 = step/2;
    private Graphics2D g2d                      = null;
    private BufferedImage really  				= null;

    /**
     * Constructor
     * @param g2d
     * @param resolutionW
     * @param resolutionH
     */
    public ExitScreen(GameInterface game, int resolutionW, int resolutionH) {

        this.resolutionW = resolutionW;
        this.resolutionH = resolutionH;

        this.mainBox    = new Rectangle2D.Double(-width, -height, width, height);
        this.yesBox     = new Rectangle2D.Double(-width, -height, buttonWidth, buttonHeight);
        this.noBox      = new Rectangle2D.Double(-width, -height, buttonWidth, buttonHeight);

        this.game       = game;
        this.g2d        = this.getG2D();

        this.really     = (BufferedImage)LoadingStuffs.getInstance().getStuff("really");
    }

    public void draw(long frametime) {
      
        //button shadow
        this.g2d.setColor(Color.BLACK);
        this.g2d.fillRect((int)mainBox.x + 1, (int)mainBox.y + 1, (int)mainBox.width, (int)mainBox.height);
        
        this.g2d.setColor(new Color(0,66,147));
        this.g2d.fillRect((int)mainBox.x, (int)mainBox.y, (int)mainBox.width, (int)mainBox.height);

        //button shadow
        this.g2d.setColor(Color.black);
        this.g2d.fillRect((int)yesBox.x + 1, (int)yesBox.y + 1, (int)yesBox.width, (int)yesBox.height);

        this.g2d.setColor(Color.red);
        this.g2d.fillRect((int)yesBox.x, (int)yesBox.y, (int)yesBox.width, (int)yesBox.height);

        //button shadow
        this.g2d.setColor(Color.black);
        this.g2d.fillRect((int)noBox.x + 1, (int)noBox.y + 1, (int)noBox.width, (int)noBox.height);

        this.g2d.setColor(Color.blue);
        this.g2d.fillRect((int)noBox.x, (int)noBox.y, (int)noBox.width, (int)noBox.height);

        this.g2d.drawImage(this.really, ((int)yesBox.x + 10), ((int)mainBox.y + 30), null);
    }

    /**
     * Update the exit screen window
     * @param frametime
     */
    public void update(long frametime) {
        
        //update framecounter
        this.framecounter += frametime;
        
        //calc the box width / height / position
        if (this.mainBoxCurWidth < this.width) {
            if (this.framecounter > 10_000_000) {

                //calc current width/height
                this.mainBoxCurWidth    += step;
                this.mainBoxCurHeight   += halfStep;

                //calc current x/y position
                this.positionX = (short)((this.resolutionW / 2) - (this.mainBoxCurWidth / 2));
                this.positionY = (short)((this.resolutionH / 2) - (this.mainBoxCurHeight / 2));

                //reset
                this.framecounter = 0;
            }
        } else {
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
                    this.noBox.x    = this.positionX + 150;
                    this.noBox.y    = this.positionY + 80;
                }
            }
        }

        this.mainBox.x      = this.positionX;
        this.mainBox.y      = this.positionY;
        this.mainBox.width  = this.mainBoxCurWidth;
        this.mainBox.height = this.mainBoxCurHeight;
    }

    public void move(int keyCode) {

    }

    public int action() {
        return 0;
    }

    public Graphics2D getG2D()  { 	return (this.game.getG2D());    }
}