package game;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

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
    private Graphics2D g2d                      = null;
    private Rectangle2D.Double mainBox          = null;
    private Rectangle2D.Double yesBox           = null;
    private Rectangle2D.Double noBox            = null;
    private short mainBoxMinWidth               = 0;
    private short mainBoxMinHeight              = 0;
    private short mainBoxCurWidth               = this.mainBoxMinWidth;
    private short mainBoxCurHeight              = this.mainBoxMinHeight;
    private volatile long framecounter          = 0;
    private final byte step                     = 10;
    private final byte halfStep                 = step/2;

    /**
     * Constructor
     * @param g2d
     * @param resolutionW
     * @param resolutionH
     */
    public ExitScreen(Graphics2D g2d, int resolutionW, int resolutionH) {

        this.resolutionW = resolutionW;
        this.resolutionH = resolutionH;

        this.positionX = (short)((resolutionW / 2) - (this.width / 2));
        this.positionY = (short)((resolutionH / 2) - (this.height / 2));

        this.mainBox = new Rectangle2D.Double(this.positionX, this.positionY, width, height);
        this.yesBox = new Rectangle2D.Double(this.positionX, this.positionY, buttonWidth, buttonHeight);
        this.noBox = new Rectangle2D.Double(this.positionX, this.positionY, buttonWidth, buttonHeight);


        this.g2d = g2d;
    }

    public void draw(long frametime) {
       



    }

    /**
     * Update the exit screen window
     * @param frametime
     */
    public void update(long frametime) {
        
        this.framecounter += frametime;
        
        if (this.mainBoxCurWidth < this.width) {
            if (this.framecounter > 100_000_000) {
                
                this.mainBoxCurWidth += step;
                this.mainBoxCurHeight += halfStep;

                this.framecounter = 0;
            }
        } else {
            if (this.mainBoxCurWidth > this.width || this.mainBoxCurHeight > this.height) {
                this.mainBoxCurWidth = this.width;
                this.mainBoxCurHeight = this.height;
            }
        }
    }

    public void move(int keyCode) {

    }

    public int action() {
        return 0;
    }
}