package game;

import java.awt.image.BufferedImage;
import util.LoadingStuffs;
import java.awt.Color;

public class Theme {
 
    private BufferedImage bgimage       = null;
    private boolean filledGrid          = false;
    private Color bgBoardColor          = null;
    private Color lineColor             = null;
    private BufferedImage next         	= null;
	private BufferedImage hold         	= null;
    private BufferedImage circle       	= null;
    private String [] bgThemes          = {"background1", "background2", "background3", "background4", "background5", "background6", "background7", "background8"};
    private String [] nextLabels        = {"next_ww", "next_w", "next_w", "next_bb", "next_b", "next_bb", "next_bb", "next_w"};
    private String [] holdLabes         = {"hold_ww", "hold_w", "hold_w", "hold_bb", "hold_b", "hold_bb", "hold_bb", "hold_w"};
    private boolean [] filledThemes     = {true, false, false, false, false, false, false, false};
    private Color [] boardColors        = {new Color(1f, 1f, 1f, 1f),       new Color(0f, 0f, 0f, 0.4f),    new Color(0f, 0f, 0f, 0.8f),    new Color(0f, 0f, 0f, 0.3f),        new Color(0.1f, 0.1f, 0.1f, 0.3f),      new Color(1, 1, 1, 0.6f),       new Color(1f, 1f, 1f, 0.6f),      new Color(0.3f, 0.5f, 0.9f, 0.4f)};
    private Color [] lineColors         = {new Color(1f, 0.8f, 0.2f, 0.2f), new Color(0f, 0f, 0f, 0.1f),    new Color(1f, 1f, 1f, 0.2f),    new Color(1, 0.8f, 0.2f, 0.4f),     new Color(0f, 0f, 0f, 0.2f),            new Color(0f, 0f, 0f, 0.05f),   new Color(0f, 0f, 0f, 0.05f),     new Color(1f, 1f, 1f, 0.2f)};
    private String[] circles            = {"circle120", "circle", "circle70", "circle100", "circle", "circle", "circle100", "circle70"};

    /**
     * Constructor
     * @param theme
     */
    public Theme(byte theme) {
        this.setTheme(theme);
    }

    public BufferedImage getCircle() {
        return (this.circle);
    }

    /**
     * Define the theme (0 - 7)
     * @param theme
     */
    public void setTheme(byte theme) {
        this.bgimage        = (BufferedImage)LoadingStuffs.getInstance().getStuff(bgThemes[theme]);
        this.filledGrid     = this.filledThemes[theme];
        this.bgBoardColor   = this.boardColors[theme];
        this.lineColor      = this.lineColors[theme];
        this.next           = (BufferedImage)LoadingStuffs.getInstance().getStuff(nextLabels[theme]);
        this.hold           = (BufferedImage)LoadingStuffs.getInstance().getStuff(holdLabes[theme]);
        this.circle         = (BufferedImage)LoadingStuffs.getInstance().getStuff(circles[theme]);
    }

    //getters
    public Color getLineColor() {return (this.lineColor);}
    public Color getBgBoardColor() {return (this.bgBoardColor);}
    public boolean getFilledGrid() {return (this.filledGrid);}
    public BufferedImage getBackgroundImage() {return (this.bgimage);}
    public BufferedImage getNextLabel() {return (this.next);}
    public BufferedImage getHoldLabel() {return (this.hold);}
}