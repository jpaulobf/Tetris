package game;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import interfaces.GameInterface;

/**
 * Exitscreen class
 */
public class ExitScreen {

    private short positionX                     = 0;
    private short positionY                     = 0;
    private short width                         = 300;
    private short height                        = 150;
    private short buttonWidth                   = 50;
    private short buttonHeight                  = 20;
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

    /**
     * Constructor
     * @param g2d
     * @param resolutionW
     * @param resolutionH
     */
    public ExitScreen(GameInterface game, int resolutionW, int resolutionH) {

        this.resolutionW = resolutionW;
        this.resolutionH = resolutionH;



        this.mainBox = new Rectangle2D.Double(this.positionX, this.positionY, width, height);
        this.yesBox = new Rectangle2D.Double(this.positionX, this.positionY, buttonWidth, buttonHeight);
        this.noBox = new Rectangle2D.Double(this.positionX, this.positionY, buttonWidth, buttonHeight);

        this.game = game;
    }

    public void draw(long frametime) {

        //TODO: set color
        this.getG2D().fillRect((int)mainBox.x, (int)mainBox.y, (int)mainBox.width, (int)mainBox.height);
        
        //TODO: look for color
        this.getG2D().fillRect((int)yesBox.x, (int)yesBox.y, (int)yesBox.width, (int)yesBox.height);

        //TODO: look for color
        this.getG2D().fillRect((int)noBox.x, (int)noBox.y, (int)noBox.width, (int)noBox.height);
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
            if (this.mainBoxCurWidth > this.width || this.mainBoxCurHeight > this.height) {

                //run once
                if (this.framecounter == frametime) {
                    //define the definitive width/height
                    this.mainBoxCurWidth    = this.width;
                    this.mainBoxCurHeight   = this.height;

                    //define the definitive x/y position
                    this.positionX          = (short)((resolutionW / 2) - (this.width / 2));
                    this.positionY          = (short)((resolutionH / 2) - (this.height / 2));
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