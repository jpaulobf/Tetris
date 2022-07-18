package game;

import java.awt.image.BufferedImage;
import interfaces.GameInterface;
import util.LoadingStuffs;
import java.awt.Point;

/**
 * Class to animate screen transition
 */
public class ScreenTransition {
    
    private GameInterface gameRef               = null;
    private volatile BufferedImage [] beans     = null;
    private volatile BufferedImage bean         = null;
    private volatile short screenWidth          = 0;
    private volatile short halfSWidth           = 0;
    private volatile short screenHeight         = 0;
    private volatile byte beansCount            = 0;
    private volatile byte center                = 0;
    private volatile Point [] beansPosition     = null;
    private volatile boolean canChange          = false;
    private volatile boolean stopped            = false;
    private volatile boolean render             = true;

    public boolean canChange() {
        return (this.canChange);
    }

    /**
     * Constructor
     * @param game
     */
    public ScreenTransition(GameInterface game) {
        
        this.gameRef        = game;

        this.bean           = LoadingStuffs.getInstance().getImage("bean");

        this.screenWidth    = (short)this.gameRef.getInternalResolutionWidth();
        this.screenHeight   = (short)this.gameRef.getInternalResolutionHeight();
        this.halfSWidth     = (short)(this.screenWidth / 2);

        this.beansCount     = (byte)((this.screenHeight / this.bean.getHeight()) + 1);
        this.beans          = new BufferedImage[this.beansCount];
        this.beansPosition  = new Point[this.beansCount];
        this.center         = (byte)(this.beansCount / 2);

        for (byte i = 0; i < this.beansCount; i++) {
            this.beans[i]            = LoadingStuffs.getInstance().getImage("bean");
            this.beansPosition[i]    = new Point();
            this.beansPosition[i].x  = i * -500;
            this.beansPosition[i].y  = i * this.bean.getHeight();
        }
    }

    /**
     * Update Method
     * @param frametime
     */
    public synchronized void update(long frametime) {
        if (!this.stopped) {
            //calc frog step for each cicle
            double step = 5000 / (1_000_000_000D / (double)frametime);

            for (byte i = 0; i < this.beansCount; i++) {
                this.beansPosition[i].x += step;
                
                if (i == center && this.beansPosition[i].x > this.halfSWidth) { 
                    this.canChange = true;
                }

                if (i == (this.beansCount - 1) && this.beansPosition[i].x > this.screenWidth) {
                    this.stopped = true;
                    this.render = false;
                }
            }
        }
    }

    /**
     * Draw Method
     * @param frametime
     */
    public synchronized void draw(long frametime) {
        if (this.render) {
            for (byte i = 0; i < this.beansCount; i++) {
                this.gameRef.getG2D().drawImage(this.beans[i], this.beansPosition[i].x, this.beansPosition[i].y, null);
            }
        }
    }

    /**
     * Reset method
     */
    public synchronized void reset() {
        this.beansPosition  = null;
        this.beansPosition  = new Point[this.beansCount];
        this.canChange      = false;
        this.render         = true;
        this.stopped        = false;

        for (byte i = 0; i < this.beansCount; i++) {
            this.beansPosition[i]    = new Point();
            this.beansPosition[i].x  = i * -500;
            this.beansPosition[i].y  = i * this.bean.getHeight();
        }
    }

    public void tooglePause() {
        this.stopped = !this.stopped;
    }
}