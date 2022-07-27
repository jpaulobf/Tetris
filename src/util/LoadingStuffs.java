package util;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
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
    private Map<String, BufferedImage> images   = new HashMap<String, BufferedImage>();
    private Map<String, Audio> audios           = new HashMap<String, Audio>();

    /**
     * Get each audio added.
     * @return
     */
    public List<Audio> getAudioList() {
        return (new ArrayList<Audio>(this.audios.values()));
    }

    /**
     * Constructor... load the game stuffs...
     */
    private LoadingStuffs() {
        //load the tiles and sprites
        try {
            
            BufferedImage image = null;
            image = ImageIO.read(new File("images\\splashImage.png"));
            images.put("splashImage", image);

            image = ImageIO.read(new File("images\\background1.png"));
            images.put("background1", image);

            image = ImageIO.read(new File("images\\background2.png"));
            images.put("background2", image);

            image = ImageIO.read(new File("images\\background3.png"));
            images.put("background3", image);

            image = ImageIO.read(new File("images\\background4.png"));
            images.put("background4", image);

            image = ImageIO.read(new File("images\\background5.png"));
            images.put("background5", image);

            image = ImageIO.read(new File("images\\background6.png"));
            images.put("background6", image);

            image = ImageIO.read(new File("images\\background7.png"));
            images.put("background7", image);

            image = ImageIO.read(new File("images\\background8.png"));
            images.put("background8", image);

            image = ImageIO.read(new File("images\\score.png"));
            images.put("score", image);

            image = ImageIO.read(new File("images\\hiscore.png"));
            images.put("hiscore", image);

            image = ImageIO.read(new File("images\\hold_b.png"));
            images.put("hold_b", image);

            image = ImageIO.read(new File("images\\hold_bl.png"));
            images.put("hold_bl", image);

            image = ImageIO.read(new File("images\\hold_bb.png"));
            images.put("hold_bb", image);

            image = ImageIO.read(new File("images\\hold_w.png"));
            images.put("hold_w", image);

            image = ImageIO.read(new File("images\\hold_ww.png"));
            images.put("hold_ww", image);

            image = ImageIO.read(new File("images\\next_b.png"));
            images.put("next_b", image);

            image = ImageIO.read(new File("images\\next_bl.png"));
            images.put("next_bl", image);

            image = ImageIO.read(new File("images\\next_bb.png"));
            images.put("next_bb", image);

            image = ImageIO.read(new File("images\\next_w.png"));
            images.put("next_w", image);

            image = ImageIO.read(new File("images\\next_ww.png"));
            images.put("next_ww", image);

            image = ImageIO.read(new File("images\\circle.png"));
            images.put("circle", image);

            image = ImageIO.read(new File("images\\circle70.png"));
            images.put("circle70", image);
            
            image = ImageIO.read(new File("images\\circle100.png"));
            images.put("circle100", image);

            image = ImageIO.read(new File("images\\circle120.png"));
            images.put("circle120", image);

            image = ImageIO.read(new File("images\\lb_level.png"));
            images.put("labelLevel", image);

            image = ImageIO.read(new File("images\\lb_line.png"));
            images.put("labelLine", image);

            image = ImageIO.read(new File("images\\n0m.png"));
            images.put("number-0-m", image);

            image = ImageIO.read(new File("images\\n1m.png"));
            images.put("number-1-m", image);

            image = ImageIO.read(new File("images\\n2m.png"));
            images.put("number-2-m", image);

            image = ImageIO.read(new File("images\\n3m.png"));
            images.put("number-3-m", image);

            image = ImageIO.read(new File("images\\n4m.png"));
            images.put("number-4-m", image);

            image = ImageIO.read(new File("images\\n5m.png"));
            images.put("number-5-m", image);

            image = ImageIO.read(new File("images\\n6m.png"));
            images.put("number-6-m", image);

            image = ImageIO.read(new File("images\\n7m.png"));
            images.put("number-7-m", image);

            image = ImageIO.read(new File("images\\n8m.png"));
            images.put("number-8-m", image);

            image = ImageIO.read(new File("images\\n9m.png"));
            images.put("number-9-m", image);

            image = ImageIO.read(new File("images\\n0b.png"));
            images.put("number-0-b", image);

            image = ImageIO.read(new File("images\\n1b.png"));
            images.put("number-1-b", image);

            image = ImageIO.read(new File("images\\n2b.png"));
            images.put("number-2-b", image);

            image = ImageIO.read(new File("images\\n3b.png"));
            images.put("number-3-b", image);

            image = ImageIO.read(new File("images\\n4b.png"));
            images.put("number-4-b", image);

            image = ImageIO.read(new File("images\\n5b.png"));
            images.put("number-5-b", image);

            image = ImageIO.read(new File("images\\n6b.png"));
            images.put("number-6-b", image);

            image = ImageIO.read(new File("images\\n7b.png"));
            images.put("number-7-b", image);

            image = ImageIO.read(new File("images\\n8b.png"));
            images.put("number-8-b", image);

            image = ImageIO.read(new File("images\\n9b.png"));
            images.put("number-9-b", image);

            image = ImageIO.read(new File("images\\bean.png"));
            images.put("bean", image);

            image = ImageIO.read(new File("images\\tlogo.png"));
            images.put("tlogo", image);

            image = ImageIO.read(new File("images\\selector.png"));
            images.put("selector", image);

            image = ImageIO.read(new File("images\\lexit.png"));
            images.put("lexit", image);

            image = ImageIO.read(new File("images\\loptions.png"));
            images.put("loptions", image);

            image = ImageIO.read(new File("images\\lplaygame.png"));
            images.put("lplaygame", image);

            image = ImageIO.read(new File("images\\start_selected.png"));
            images.put("starSelected", image);

            image = ImageIO.read(new File("images\\start_unselected.png"));
            images.put("starUnselected", image);

            image = ImageIO.read(new File("images\\really.png"));
            images.put("really", image);

            image = ImageIO.read(new File("images\\yes.png"));
            images.put("lb-yes", image);

            image = ImageIO.read(new File("images\\no.png"));
            images.put("lb-no", image);

            image = ImageIO.read(new File("images\\options_logo.png"));
            images.put("options-logo", image);

            image = ImageIO.read(new File("images\\lb_play_music.png"));
            images.put("label-play-music", image);

            image = ImageIO.read(new File("images\\toogle_off.png"));
            images.put("toogle-off", image);

            image = ImageIO.read(new File("images\\toogle_on.png"));
            images.put("toogle-on", image);

            image = ImageIO.read(new File("images\\lb_play_sfx.png"));
            images.put("label-play-sfx", image);

            image = ImageIO.read(new File("images\\lb_music_volume.png"));
            images.put("label-music-vol", image);

            image = ImageIO.read(new File("images\\lb_sfx_volume.png"));
            images.put("label-sfx-vol", image);

            image = ImageIO.read(new File("images\\lb_exit_option.png"));
            images.put("label-exit-option", image);

            image = ImageIO.read(new File("images\\lb_ghost_piece.png"));
            images.put("label-ghost-piece", image);

            image = ImageIO.read(new File("images\\lb_hold_piece.png"));
            images.put("label-hold-piece", image);

            image = ImageIO.read(new File("images\\v1_on.png"));
            images.put("v1-on", image);

            image = ImageIO.read(new File("images\\v2_on.png"));
            images.put("v2-on", image);

            image = ImageIO.read(new File("images\\v3_on.png"));
            images.put("v3-on", image);

            image = ImageIO.read(new File("images\\v4_on.png"));
            images.put("v4-on", image);

            image = ImageIO.read(new File("images\\v5_on.png"));
            images.put("v5-on", image);

            image = ImageIO.read(new File("images\\v6_on.png"));
            images.put("v6-on", image);

            image = ImageIO.read(new File("images\\v1_off.png"));
            images.put("v1-off", image);

            image = ImageIO.read(new File("images\\v2_off.png"));
            images.put("v2-off", image);

            image = ImageIO.read(new File("images\\v3_off.png"));
            images.put("v3-off", image);

            image = ImageIO.read(new File("images\\v4_off.png"));
            images.put("v4-off", image);

            image = ImageIO.read(new File("images\\v5_off.png"));
            images.put("v5-off", image);

            image = ImageIO.read(new File("images\\v6_off.png"));
            images.put("v6-off", image);

            image = ImageIO.read(new File("images\\lb_how_many_next.png"));
            images.put("label-how-many-next", image);

            Logger.INFO("read all images...", this);

            Audio audio = new Audio("audio\\theme1.wav", 0, Audio.MUSIC);
            if (audio != null && audio.isReady()) {
                audios.put("theme1", audio);
            }

            audio = new Audio("audio\\theme2.wav", 0, Audio.MUSIC);
            if (audio != null && audio.isReady()) {
                audios.put("theme2", audio);
            }

            audio = new Audio("audio\\theme3.wav", 0, Audio.MUSIC);
            if (audio != null && audio.isReady()) {
                audios.put("theme3", audio);
            }

            //turn
            audio = new Audio("audio\\turnb.wav", 0, Audio.SFX);
            if (audio != null && audio.isReady()) {
                audios.put("turn", audio);
            }

            audio = new Audio("audio\\move.wav", 0, Audio.SFX);
            if (audio != null && audio.isReady()) {
                audios.put("move", audio);
            }

            audio = new Audio("audio\\splash2.wav", 0, Audio.SFX);
            if (audio != null && audio.isReady()) {
                audios.put("splash", audio);
            }

            audio = new Audio("audio\\drop.wav", 0, Audio.SFX);
            if (audio != null && audio.isReady()) {
                audios.put("drop", audio);
            }

            audio = new Audio("audio\\hold.wav", 0, Audio.SFX);
            if (audio != null && audio.isReady()) {
                audios.put("hold", audio);
            }

            audio = new Audio("audio\\tetris.wav", 0, Audio.SFX);
            if (audio != null && audio.isReady()) {
                audios.put("tetris", audio);
            }

            audio = new Audio("audio\\intro.wav", 0, Audio.MUSIC);
            if (audio != null && audio.isReady()) {
                audios.put("intro", audio);
            }

            audio = new Audio("audio\\menuitem.wav", 0, Audio.SFX);
            if (audio != null && audio.isReady()) {
                audios.put("menuitem", audio);
            }

            audio = new Audio("audio\\star.wav", 0, Audio.SFX);
            if (audio != null && audio.isReady()) {
                audios.put("star", audio);
            }

            audio = new Audio("audio\\opening.wav", 0, Audio.SFX);
            if (audio != null && audio.isReady()) {
                audios.put("opening", audio);
            }

            audio = new Audio("audio\\closing.wav", 0, Audio.SFX);
            if (audio != null && audio.isReady()) {
                audios.put("closing", audio);
            }

            audio = new Audio("audio\\start.wav", 0, Audio.SFX);
            if (audio != null && audio.isReady()) {
                audios.put("start", audio);
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
     * Return the stored image
     * @param imageName
     * @return
     */
    public BufferedImage getImage(String imageName) {
        return (this.images.get(imageName));
    }

    /**
     * Return the stored audio
     * @param audioName
     * @return
     */
    public Audio getAudio(String audioName) {
        return (this.audios.get(audioName));
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