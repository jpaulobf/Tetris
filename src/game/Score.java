package game;

import java.awt.Graphics2D;
import java.awt.image.VolatileImage;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;
import java.awt.Color;
import java.io.File;
import java.util.Scanner;
import util.LoadingStuffs;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class responsible for score control.
 */
public class Score {
    
    private Game gameRef                = null;
    private VolatileImage scoreBG       = null;
    private BufferedImage oneupTile     = null;
    private BufferedImage hiscoreTile   = null;
    private BufferedImage [] numbers    = null;
    private Graphics2D bg2d             = null;
    private volatile String sHiscore    = null;
    private volatile String sDate       = "";
    private volatile String sScore      = "0000000";
    private volatile int score          = 0;
    private volatile int hiscore        = 0;
    private volatile Date dateHiscore   = null;
    private int wwm                     = 0;
    private byte scoreHeight            = 0;
    private final short initialScoreL   = 200;
    private final short initialScoreX   = 350;
    private final short initialHiscoreX = 850;
    private final short initialHiscoreL = 600;
    private final byte initialScoreY    = 10;
    private short currentScoreX         = 0;
    private short currentHiscoreX       = 0;
    private int skipPoint               = 0;

    /**
     * Score constructor
     * @param game
     * @param wwm
     * @param whm
     * @param scoreHeight
     */
    public Score(Game game, int wwm, byte scoreHeight) {
        this.scoreHeight        = scoreHeight;
        this.wwm                = wwm;
        this.gameRef            = game;
        this.score              = 0;
        this.hiscore            = 0;
        this.numbers            = new BufferedImage[10];
        this.scoreBG            = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(wwm, scoreHeight);
        this.bg2d               = (Graphics2D)this.scoreBG.getGraphics();
        this.oneupTile          = (BufferedImage)LoadingStuffs.getInstance().getStuff("oneupTile");
        this.hiscoreTile        = (BufferedImage)LoadingStuffs.getInstance().getStuff("hiscoreTile");
        this.numbers[0]         = (BufferedImage)LoadingStuffs.getInstance().getStuff("number-0");
        this.numbers[1]         = (BufferedImage)LoadingStuffs.getInstance().getStuff("number-1");
        this.numbers[2]         = (BufferedImage)LoadingStuffs.getInstance().getStuff("number-2");
        this.numbers[3]         = (BufferedImage)LoadingStuffs.getInstance().getStuff("number-3");
        this.numbers[4]         = (BufferedImage)LoadingStuffs.getInstance().getStuff("number-4");
        this.numbers[5]         = (BufferedImage)LoadingStuffs.getInstance().getStuff("number-5");
        this.numbers[6]         = (BufferedImage)LoadingStuffs.getInstance().getStuff("number-6");
        this.numbers[7]         = (BufferedImage)LoadingStuffs.getInstance().getStuff("number-7");
        this.numbers[8]         = (BufferedImage)LoadingStuffs.getInstance().getStuff("number-8");
        this.numbers[9]         = (BufferedImage)LoadingStuffs.getInstance().getStuff("number-9");
        this.currentScoreX      = this.initialScoreX;
        this.currentHiscoreX    = this.initialHiscoreX;

        //load the file containing the hi score
        this.loadHighScore();
    }

    /**
     * Draw the BG
     */
    private void drawScoreBG() {
        //clear the backbuffer
        this.bg2d.setBackground(new Color(0, 0, 100));
        this.bg2d.clearRect(0, 0, this.wwm, scoreHeight);
        
        //draw the oneup tile
        this.bg2d.drawImage(this.oneupTile,     this.initialScoreL, this.initialScoreY, (this.initialScoreL + this.oneupTile.getWidth()), this.initialScoreY + this.oneupTile.getHeight(),
                                                0, 0, this.oneupTile.getWidth(), this.oneupTile.getHeight(),
                                                null);

        //draw the hiscore tile
        this.bg2d.drawImage(this.hiscoreTile,   this.initialHiscoreL, this.initialScoreY, (this.initialHiscoreL + this.hiscoreTile.getWidth()), this.initialScoreY + this.hiscoreTile.getHeight(),
                                                0, 0, this.hiscoreTile.getWidth(), this.hiscoreTile.getHeight(),
                                                null);

        //convert the score & hiscore into image
        this.currentScoreX = this.initialScoreX;
        this.currentHiscoreX = this.initialHiscoreX;
        for (int i = 0; i < this.sScore.length(); i++) {
            BufferedImage temp = this.numbers[Byte.parseByte(sScore.charAt(i)+"")];
            this.bg2d.drawImage(temp, this.currentScoreX, this.initialScoreY, this.currentScoreX + temp.getWidth(), this.initialScoreY + temp.getHeight(),
                                        0, 0, temp.getWidth(), temp.getHeight(),
                                        null);
            this.currentScoreX += temp.getWidth();
            temp = this.numbers[Byte.parseByte(this.sHiscore.charAt(i)+"")];
            this.bg2d.drawImage(temp, this.currentHiscoreX, this.initialScoreY, this.currentHiscoreX + temp.getWidth(), this.initialScoreY + temp.getHeight(),
                                        0, 0, temp.getWidth(), temp.getHeight(),
                                        null);
            this.currentHiscoreX += temp.getWidth();
        }
    }

    /**
     * Load the file highscore
     */
    private synchronized void loadHighScore() {
        //1 - try load hiscore.p file
        //2 - if file not exist, do nothing!
        //3 - else load current hiscore date (yyyy-mm-dd) & score
        //4 - set to the variables
        File hiscorep = new File("files\\hiscore.p");
        if (hiscorep.exists()) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(hiscorep);
                String line, value = "";
                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    line.trim();
                    if (line.length() > 0 && line.charAt(0) != '#') { //ignore comments
                        value = line.split(":")[1].trim();
                        if (value != null && value.length() > 0) {
                            sDate    = value.split(",")[0].trim();
                            sHiscore = value.split(",")[1].trim();
                        }
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        this.hiscore = Integer.parseInt(this.sHiscore);
                        this.sHiscore = String.format("%07d", this.hiscore);
                        this.dateHiscore = formatter.parse(sDate);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error opening 'hiscore.p' file!");
            } finally {
                if (scanner != null) {
                    scanner.close();
                }
            }
        } else {
            this.sScore = String.format("%07d", this.score);
            this.sHiscore = String.format("%07d", this.hiscore);
        }  
    }

    /**
     * Store new highscore to the file.
     */
    public synchronized void storeNewHighScore() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        this.dateHiscore = new Date();
        this.sHiscore = String.valueOf(this.hiscore);
        this.sDate = formatter.format(this.dateHiscore);
        File hiscorep = new File("files\\hiscore.p");

        //if file exists, delete it
        if (hiscorep.exists()) {
            hiscorep.delete();
        }

        //than, create a clean new file
        try {
            hiscorep.createNewFile();    
        } catch (Exception e) {
            System.err.println("Impossible to create 'hiscore.p' file! " + "\n" + e.getMessage());
        }

        //if everything is ok, store the high score
        if (hiscorep.canWrite()) {
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(hiscorep, true));
                writer.append("hiscore:");
                writer.append(sDate);
                writer.append(",");
                writer.append(this.hiscore + "");
                writer.append("\n");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                if (this.sHiscore != null) {
                    this.sHiscore = String.format("%07d", this.hiscore);
                }
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Save the Higher Score
     */
    public void saveHiScore() {
        this.storeNewHighScore();
    }

    /**
     * Update the score
     * @param frametime
     */
    public void update(long frametime) {
        if (this.score > 9_999_999) {
            this.hiscore = this.score;
            this.score = 0;
        } else if (this.score > this.hiscore) {
            this.hiscore = this.score;
            this.sHiscore = String.format("%07d", this.score);
        }
        this.sScore = String.format("%07d", this.score);
    }

    /**
     * Draw the score
     * @param frametime
     */
    public void draw(long frametime) {

        this.drawScoreBG();

        //After HUD rendered, copy to G2D
        this.gameRef.getG2D().drawImage(this.scoreBG, 0, 0, this.wwm, this.scoreHeight,   //dest w1, h1, w2, h2
                                                      0, 0, this.scoreBG.getWidth(), this.scoreBG.getHeight(),  //source w1, h1, w2, h2
                                                      null);
    }

    /**
     * Public method to add points
     * @param type
     */
    public void addScore(byte type) {
        if (this.skipPoint > 0) {
            this.skipPoint--;
        } else {
            //TODO:
        }
    }

    /**
     * When necessary, skip point acumulation
     */
    public void skipPoint() {
        this.skipPoint++;
    }

    /**
     * Reset the skip point controller
     */
    public void resetSkipPoint() {
        this.skipPoint = 0;
    }

    /**
     * Reset method
     */
    public void reset() {
        this.score = 0;
        if (this.sHiscore != null && !"".equals(this.sHiscore)) {
            this.hiscore = Integer.parseInt(this.sHiscore);
        } else {
            this.hiscore = 0;
        }
        this.skipPoint = 0;
    }
}