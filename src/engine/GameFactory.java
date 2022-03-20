package engine;

import interfaces.GameInterface;
import game.Game;

/**
 * Game factory
 */
public class GameFactory {
    public static GameInterface getGameInstance() {
        return (new Game());
    }
}
