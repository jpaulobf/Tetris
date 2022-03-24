package util;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;

/**
 * This class is responsible for load the game stuffs
 */
public class LoadingStuffs {
    
    //private instance of loader
    private static LoadingStuffs instance   = null;
    private int chargeStatus                = 0;

    //Stuffs Map
    private Map<String, Object> stuffs      = new HashMap<String, Object>();

    /**
     * Constructor... load the game stuffs...
     */
    private LoadingStuffs() {
        //load the tiles and sprites
        try {
            
            BufferedImage image = null;
            image = ImageIO.read(new File("images\\splashImage.png"));
            stuffs.put("splashImage", image);

            image = ImageIO.read(new File("images\\background.png"));
            stuffs.put("background", image);

            Logger.INFO("read all images...", this);

            Audio audio = new Audio("audio\\theme.wav", 0);
            if (audio != null && audio.isReady()) {
                stuffs.put("theme", audio);
            }

            audio = new Audio("audio\\turn2.wav", 0);
            if (audio != null && audio.isReady()) {
                stuffs.put("turn", audio);
            }

            audio = new Audio("audio\\move.wav", 0);
            if (audio != null && audio.isReady()) {
                stuffs.put("move", audio);
            }

            Logger.INFO("read all audio...", this);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a transluced volatile image
     * @param image
     * @return
     */
    protected VolatileImage createVImage(BufferedImage image) { 
        VolatileImage vImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(image.getWidth(), image.getHeight(), Transparency.BITMASK);
        Graphics2D bgd2 = (Graphics2D)vImage.getGraphics();
        bgd2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT));
        bgd2.setColor(new java.awt.Color(255,255,255,0));
        bgd2.fillRect(0, 0, image.getWidth(), image.getHeight());
        bgd2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        bgd2.drawImage(image, 0, 0, vImage.getWidth(), vImage.getHeight(), //dest w1, h1, w2, h2
                              0, 0, image.getWidth(), image.getHeight(), //source w1, h1, w2, h2
                              null);
        return (vImage);
    }

    /**
     * Recover the stored object
     * @param objectName
     * @return
     */
    public Object getStuff(String objectName) {
        return (this.stuffs.get(objectName));
    }

    /**
     * Recover the singleton instance  
     * @return
     */
    public static LoadingStuffs getInstance() {
        if (instance == null) {
            instance = new LoadingStuffs();
        }
        return instance;
    }

    /**
     * Returns the charge counter status (0 ... 100%)
     * @return
     */
    public int getChargeStatus() {
        return (this.chargeStatus);
    }
}