package game;

import interfaces.GameInterface;

public class Menu {
    
    @SuppressWarnings("unused")
    private volatile long framecounter  = 0L;
    @SuppressWarnings("unused")
    private GameInterface gameRef       = null;

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

}
