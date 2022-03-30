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

            image = ImageIO.read(new File("images\\background1.png"));
            stuffs.put("background1", image);

            image = ImageIO.read(new File("images\\background2.png"));
            stuffs.put("background2", image);

            image = ImageIO.read(new File("images\\background3.png"));
            stuffs.put("background3", image);

            image = ImageIO.read(new File("images\\background4.png"));
            stuffs.put("background4", image);

            image = ImageIO.read(new File("images\\background5.png"));
            stuffs.put("background5", image);

            image = ImageIO.read(new File("images\\background6.png"));
            stuffs.put("background6", image);

            image = ImageIO.read(new File("images\\background7.png"));
            stuffs.put("background7", image);

            image = ImageIO.read(new File("images\\background8.png"));
            stuffs.put("background8", image);

            image = ImageIO.read(new File("images\\score.png"));
            stuffs.put("score", image);

            image = ImageIO.read(new File("images\\hiscore.png"));
            stuffs.put("hiscore", image);

            image = ImageIO.read(new File("images\\hold_b.png"));
            stuffs.put("hold_b", image);

            image = ImageIO.read(new File("images\\hold_bl.png"));
            stuffs.put("hold_bl", image);

            image = ImageIO.read(new File("images\\hold_bb.png"));
            stuffs.put("hold_bb", image);

            image = ImageIO.read(new File("images\\hold_w.png"));
            stuffs.put("hold_w", image);

            image = ImageIO.read(new File("images\\hold_ww.png"));
            stuffs.put("hold_ww", image);

            image = ImageIO.read(new File("images\\next_b.png"));
            stuffs.put("next_b", image);

            image = ImageIO.read(new File("images\\next_bl.png"));
            stuffs.put("next_bl", image);

            image = ImageIO.read(new File("images\\next_bb.png"));
            stuffs.put("next_bb", image);

            image = ImageIO.read(new File("images\\next_w.png"));
            stuffs.put("next_w", image);

            image = ImageIO.read(new File("images\\next_ww.png"));
            stuffs.put("next_ww", image);

            image = ImageIO.read(new File("images\\circle.png"));
            stuffs.put("circle", image);

            image = ImageIO.read(new File("images\\circle100.png"));
            stuffs.put("circle100", image);

            image = ImageIO.read(new File("images\\lb_level.png"));
            stuffs.put("labelLevel", image);

            image = ImageIO.read(new File("images\\lb_line.png"));
            stuffs.put("labelLine", image);

            Logger.INFO("read all images...", this);

            Audio audio = new Audio("audio\\theme1.wav", 0);
            if (audio != null && audio.isReady()) {
                stuffs.put("theme1", audio);
            }

            audio = new Audio("audio\\theme2.wav", 0);
            if (audio != null && audio.isReady()) {
                stuffs.put("theme2", audio);
            }

            audio = new Audio("audio\\theme3.wav", 0);
            if (audio != null && audio.isReady()) {
                stuffs.put("theme3", audio);
            }

            audio = new Audio("audio\\turn.wav", 0);
            if (audio != null && audio.isReady()) {
                stuffs.put("turn", audio);
            }

            audio = new Audio("audio\\move.wav", 0);
            if (audio != null && audio.isReady()) {
                stuffs.put("move", audio);
            }

            audio = new Audio("audio\\splash2.wav", 0);
            if (audio != null && audio.isReady()) {
                stuffs.put("splash", audio);
            }

            audio = new Audio("audio\\drop.wav", 0);
            if (audio != null && audio.isReady()) {
                stuffs.put("drop", audio);
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