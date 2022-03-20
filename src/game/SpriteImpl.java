package game;

import java.awt.geom.Rectangle2D;
import game.interfaces.Directions;
import game.interfaces.Sprite;

/*
    WTCD: This abstract class store the common characteristics and methods of a sprite
*/
public abstract class SpriteImpl implements Sprite, Directions {

    //variable member values 
    protected volatile byte type                = 0;
    protected volatile byte direction           = LEFT;
    protected volatile short velocity           = 0;
    protected volatile double positionX         = 0;
    protected volatile double positionY         = 0;
    protected volatile double destPositionX     = 0;
    protected volatile double destPositionY     = 0;
    protected volatile double calculatedStep    = 0D;
    protected volatile short width              = 0;
    protected volatile byte height              = 0;
    protected volatile byte offsetTop           = 0;
    protected volatile byte offsetLeft          = 0;
    protected volatile int ogPositionX          = 0;
    protected Rectangle2D rectangle             = null;
    protected volatile int scenarioOffsetX      = 0;
    protected volatile int scenarioOffsetY      = 0;
    
    /**
     * Accessor Method
     * @return
     */
    public short getWidth()                     {   return (this.width);            }
    public byte getDirection()                  {   return (this.direction);        }
    public double getPositionX()                {   return (this.positionX);        }
    public double getCalculatedStep()           {   return (this.calculatedStep);   }
    public void setScenarioOffsetX(int offsetX) {   this.scenarioOffsetX = offsetX; }
    public void setScenarioOffsetY(int offsetY) {   this.scenarioOffsetY = offsetY; }
    public int getType()                        {   return (this.type);             }
    
    /**
     * Abstract methods
     */
    public abstract void draw(long frametime);
    public abstract void update(long frametime);

    /**
     * WTMD: Return a rectangle with the current XY position
     */
    public Rectangle2D calcMyRect() {
        this.rectangle = new Rectangle2D.Double(this.positionX, this.positionY, this.width, this.height);
        return (this.rectangle);
    }

    /**
     * Verify if this sprite is coliding with other
     * @param sprite
     * @return
     */
    public boolean isColliding(Sprite sprite) {
        return (calcMyRect().intersects(sprite.calcMyRect()));
    }

    /**
     * Verbose sprite colision detection
     * @param sprite
     * @param verbose
     * @return
     */
    public boolean isColliding(SpriteImpl sprite, boolean verbose) {

        if (verbose) {
            System.out.println("my rect: ");
            System.out.println(this.positionX);
            System.out.println(this.positionY);
            System.out.println(this.width);
            System.out.println(this.height);
            System.out.println("sprite rect: ");
            System.out.println(sprite.positionX);
            System.out.println(sprite.positionY);
            System.out.println(sprite.width);
            System.out.println(sprite.height);
        }

        return (isColliding(sprite));
    }

    /**
     * Verbose sprite colision detection
     * @param sprite
     * @param additionalPositionX
     * @param additionalPositionY
     * @return is colliding
     */
    public boolean isColliding(Sprite sprite, double additionalPositionX, double additionalPositionY) {
        return ((new Rectangle2D.Double(this.positionX + additionalPositionX, this.positionY + additionalPositionY, this.width, this.height)).intersects(sprite.calcMyRect()));
    }
}