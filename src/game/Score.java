package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.Scanner;
import util.LoadingStuffs;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Transparency;
import java.awt.Point;

/**
 * Class responsible for score control.
 */
public class Score {
    
    private Game gameRef                = null;
    private BufferedImage scoreBG       = null;
    private BufferedImage hiscoreBG     = null;
    private BufferedImage levelBG       = null;
    private BufferedImage linesBG       = null;
    private Graphics2D g2d              = null;

    //numbers
    private BufferedImage [] numbers_m  = null;
    private BufferedImage [] numbers_b  = null;
    

    private volatile String sHiscore    = null;
    private volatile String sDate       = "";
    private volatile String sScore      = "0000000";
    private volatile String sLevel      = "00";
    private volatile String sLines      = "000";
    
    private volatile int score          = 0;
    private volatile int hiscore        = 0;
    private volatile int level          = 0;
    private volatile int lines          = 0;
    private volatile Date dateHiscore   = null;

    private Point scorePos              = null;
    private Point hiScorePos            = null;
    private Point levelPos              = null;
    private Point linesPos              = null;

    //constants
    public static byte SOFTDROP         = -1;
    public static byte HARDDROP         = 0;
    public static byte SINGLELINE       = 1;
    public static byte DOUBLELINE       = 3;
    public static byte TRIPELINE        = 6;
    public static byte TETRIS           = 10;

    /**
     * Score constructor
     * @param game
     * @param wwm
     * @param whm
     * @param scoreHeight
     */
    public Score(Game game, Point scorePos, Point hiScorePos, Point levelPos, Point linesPos) {
        this.scorePos           = scorePos;
        this.hiScorePos         = hiScorePos;
        this.levelPos           = levelPos;
        this.linesPos           = linesPos;
        this.gameRef            = game;
        this.score              = 0;
        this.hiscore            = 0;
        this.scoreBG            = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(160, 23, Transparency.TRANSLUCENT);
        this.hiscoreBG          = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(160, 23, Transparency.TRANSLUCENT);
        this.levelBG            = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage( 80, 49, Transparency.TRANSLUCENT);
        this.linesBG            = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(120, 49, Transparency.TRANSLUCENT);
        this.numbers_m          = new BufferedImage[10];
        this.numbers_b          = new BufferedImage[10];
        this.numbers_m[0]       = LoadingStuffs.getInstance().getImage("number-0-m");
        this.numbers_m[1]       = LoadingStuffs.getInstance().getImage("number-1-m");
        this.numbers_m[2]       = LoadingStuffs.getInstance().getImage("number-2-m");
        this.numbers_m[3]       = LoadingStuffs.getInstance().getImage("number-3-m");
        this.numbers_m[4]       = LoadingStuffs.getInstance().getImage("number-4-m");
        this.numbers_m[5]       = LoadingStuffs.getInstance().getImage("number-5-m");
        this.numbers_m[6]       = LoadingStuffs.getInstance().getImage("number-6-m");
        this.numbers_m[7]       = LoadingStuffs.getInstance().getImage("number-7-m");
        this.numbers_m[8]       = LoadingStuffs.getInstance().getImage("number-8-m");
        this.numbers_m[9]       = LoadingStuffs.getInstance().getImage("number-9-m");
        this.numbers_b[0]       = LoadingStuffs.getInstance().getImage("number-0-b");
        this.numbers_b[1]       = LoadingStuffs.getInstance().getImage("number-1-b");
        this.numbers_b[2]       = LoadingStuffs.getInstance().getImage("number-2-b");
        this.numbers_b[3]       = LoadingStuffs.getInstance().getImage("number-3-b");
        this.numbers_b[4]       = LoadingStuffs.getInstance().getImage("number-4-b");
        this.numbers_b[5]       = LoadingStuffs.getInstance().getImage("number-5-b");
        this.numbers_b[6]       = LoadingStuffs.getInstance().getImage("number-6-b");
        this.numbers_b[7]       = LoadingStuffs.getInstance().getImage("number-7-b");
        this.numbers_b[8]       = LoadingStuffs.getInstance().getImage("number-8-b");
        this.numbers_b[9]       = LoadingStuffs.getInstance().getImage("number-9-b");

        //load the file containing the hi score
        this.loadHighScore();
    }

    /**
     * Update the score
     * @param frametime
     */
    public void update(long frametime) {
        if (this.score > 9_999_999) {
            this.hiscore = 9_999_999;
            this.score = 0;
        } else if (this.score > this.hiscore) {
            this.hiscore = this.score;
        }
        this.sHiscore   = String.format("%07d", this.hiscore);
        this.sScore     = String.format("%07d", this.score);
        this.sLevel     = String.format("%02d", this.level);
        this.sLines     = String.format("%03d", this.lines);
    }

    /**
     * Draw the score
     * @param frametime
     */
    public void draw(long frametime) {

        this.drawScoreBG();

        this.drawHiScoreBG();

        this.drawLevelBG();

        this.drawLinesBG();

        //draw the score
        this.gameRef.getG2D().drawImage(this.scoreBG, this.scorePos.x, this.scorePos.y, (this.scorePos.x + this.scoreBG.getWidth()), (this.scorePos.y + this.scoreBG.getHeight()), //dest w1, h1, w2, h2
                                                      0, 0, this.scoreBG.getWidth(), this.scoreBG.getHeight(),  //source w1, h1, w2, h2
                                                      null);

        //draw the hi-score
        this.gameRef.getG2D().drawImage(this.hiscoreBG, this.hiScorePos.x, this.hiScorePos.y, (this.hiScorePos.x + this.hiscoreBG.getWidth()), (this.hiScorePos.y + this.hiscoreBG.getHeight()), //dest w1, h1, w2, h2
                                                        0, 0, this.hiscoreBG.getWidth(), this.hiscoreBG.getHeight(),  //source w1, h1, w2, h2
                                                        null);

        //draw the level
        this.gameRef.getG2D().drawImage(this.levelBG, this.levelPos.x, this.levelPos.y, (this.levelPos.x + this.levelBG.getWidth()), (this.levelPos.y + this.levelBG.getHeight()), //dest w1, h1, w2, h2
                                                      0, 0, this.levelBG.getWidth(), this.levelBG.getHeight(),  //source w1, h1, w2, h2
                                                      null);

        //draw the lines
        this.gameRef.getG2D().drawImage(this.linesBG, this.linesPos.x, this.linesPos.y, (this.linesPos.x + this.linesBG.getWidth()), (this.linesPos.y + this.linesBG.getHeight()),   //dest w1, h1, w2, h2
                                                      0, 0, this.linesBG.getWidth(), this.linesBG.getHeight(),  //source w1, h1, w2, h2
                                                      null);
    }

    /**
     * Draw the Score Panel
     */
    private void drawScoreBG() {
        //clear the backbuffer
        this.g2d = (Graphics2D)this.scoreBG.getGraphics();
        this.g2d.setComposite(java.awt.AlphaComposite.Clear);
		this.g2d.fillRect(0, 0, this.scoreBG.getWidth(), this.scoreBG.getHeight());
		this.g2d.setComposite(java.awt.AlphaComposite.SrcOver);
        
        int currentPosX = 0;

        //convert the score & hiscore into image
        for (int i = 0; i < this.sScore.length(); i++) {
            BufferedImage temp = this.numbers_m[Byte.parseByte(sScore.charAt(i)+"")];
            this.g2d.drawImage(temp, currentPosX, 0, currentPosX + temp.getWidth(), temp.getHeight(), 
                                     0, 0, temp.getWidth(), temp.getHeight(), null);
            currentPosX += temp.getWidth();
        }
    }

    /**
     * Draw the Hiscore Panel
     */
    private void drawHiScoreBG() {
        //clear the backbuffer
        this.g2d = (Graphics2D)this.hiscoreBG.getGraphics();
        this.g2d.setComposite(java.awt.AlphaComposite.Clear);
		this.g2d.fillRect(0, 0, this.hiscoreBG.getWidth(), this.hiscoreBG.getHeight());
		this.g2d.setComposite(java.awt.AlphaComposite.SrcOver);
        
        int currentPosX = 0;

        //convert the score & hiscore into image
        for (int i = 0; i < this.sHiscore.length(); i++) {
            BufferedImage temp = this.numbers_m[Byte.parseByte(sHiscore.charAt(i)+"")];
            this.g2d.drawImage(temp, currentPosX, 0, currentPosX + temp.getWidth(), temp.getHeight(), 
                                     0, 0, temp.getWidth(), temp.getHeight(), null);
            currentPosX += temp.getWidth();
        }
    }

    /**
     * Draw the Level Panel
     */
    private void drawLevelBG() {
        //clear the backbuffer
        this.g2d = (Graphics2D)this.levelBG.getGraphics();
        this.g2d.setComposite(java.awt.AlphaComposite.Clear);
		this.g2d.fillRect(0, 0, this.levelBG.getWidth(), this.levelBG.getHeight());
		this.g2d.setComposite(java.awt.AlphaComposite.SrcOver);
        
        int currentPosX = 0;

        //convert the score & hiscore into image
        for (int i = 0; i < this.sLevel.length(); i++) {
            BufferedImage temp = this.numbers_b[Byte.parseByte(sLevel.charAt(i)+"")];
            this.g2d.drawImage(temp, currentPosX, 0, currentPosX + temp.getWidth(), temp.getHeight(), 
                                     0, 0, temp.getWidth(), temp.getHeight(), null);
            currentPosX += temp.getWidth();
        }
    }

    /**
     * Draw the Level Panel
     */
    private void drawLinesBG() {
        //clear the backbuffer
        this.g2d = (Graphics2D)this.linesBG.getGraphics();
        this.g2d.setComposite(java.awt.AlphaComposite.Clear);
		this.g2d.fillRect(0, 0, this.linesBG.getWidth(), this.linesBG.getHeight());
		this.g2d.setComposite(java.awt.AlphaComposite.SrcOver);
        
        int currentPosX = 0;

        //convert the score & hiscore into image
        for (int i = 0; i < this.sLines.length(); i++) {
            BufferedImage temp = this.numbers_b[Byte.parseByte(sLines.charAt(i)+"")];
            this.g2d.drawImage(temp, currentPosX, 0, currentPosX + temp.getWidth(), temp.getHeight(), 
                                     0, 0, temp.getWidth(), temp.getHeight(), null);
            currentPosX += temp.getWidth();
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
     * Public method to add points
     * @param type
     */
    public void addScore(byte type, byte level) {
        if (type == SOFTDROP) {
            this.score += (1 * level);
        } else if (type == HARDDROP) {
            this.score += (2 * level);
        } else if (type == SINGLELINE) {
            this.score += ((level) * 100);
        } else if (type == DOUBLELINE) {
            this.score += ((level) * 300);
        } else if (type == TRIPELINE) {
            this.score += ((level) * 500);
        } else if (type == TETRIS) {
            this.score += ((level) * 500);
        }
    }

    public void setLines(short lines) {
        this.lines = lines;
    }

    public void setCurrentLevel(byte level) {
        this.level = level;
    }

    /**
     * Reset method
     */
    public void reset() {
        this.score = 0;
        this.level = this.gameRef.getCurrentLevel();
        this.lines = 0;
        if (this.sHiscore != null && !"".equals(this.sHiscore)) {
            this.hiscore = Integer.parseInt(this.sHiscore);
        } else {
            this.hiscore = 0;
        }
    }
}