package game;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Exitscreen class
 */
public class ExitScreen {

    private int positionX                       = 0;
    private int positionY                       = 0;
    private int width                           = 300;
    private int height                          = 150;
    private int buttonWidth                     = 50;
    private int buttonHeight                    = 20;
    private int resolutionH                     = 0;
    private int resolutionW                     = 0;
    private Graphics2D g2d                      = null;
    private Rectangle2D.Double mainBox          = null;
    private Rectangle2D.Double yesBox           = null;
    private Rectangle2D.Double noBox            = null;

    /**
     * Constructor
     * @param g2d
     * @param resolutionW
     * @param resolutionH
     */
    public ExitScreen(Graphics2D g2d, int resolutionW, int resolutionH) {

        this.resolutionW = resolutionW;
        this.resolutionH = resolutionH;

        this.positionX = (int)((resolutionW / 2) - (this.width / 2));
        this.positionY = (int)((resolutionH / 2) - (this.height / 2));

        this.mainBox = new Rectangle2D.Double(this.positionX, this.positionY, width, height);
        this.yesBox = new Rectangle2D.Double(this.positionX, this.positionY, buttonWidth, buttonHeight);
        this.noBox = new Rectangle2D.Double(this.positionX, this.positionY, buttonWidth, buttonHeight);


        this.g2d = g2d;
    }

    public void draw(long frametime) {
       //TODO
    }

    public void update(long frametime) {
        //TODO
    }

    public void move(int keyCode) {

    }

    public int action() {
        return 0;
    }
}