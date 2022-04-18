package game;

import interfaces.GameInterface;

public class Menu {
    
    @SuppressWarnings("unused")
    private volatile long framecounter  = 0L;
    @SuppressWarnings("unused")
    private GameInterface gameRef       = null;

    private volatile boolean goOptions  = false;
    private volatile boolean goGame     = false;
    private volatile boolean goExit     = false;

    /**
     * Constructor
     * @param game
     */
    public Menu(GameInterface game) {
        this.gameRef = game;
    }

    public synchronized void update(long frametime) {

    }
    
    public synchronized void draw(long frametime) {

    }

    public synchronized void firstUpdate(long frametime) {
        
    }

    //getters
    public boolean goOptions() {return (this.goOptions);}
    public boolean goGame() {return (this.goGame);}
    public boolean goExit() {return (this.goExit);}
}
