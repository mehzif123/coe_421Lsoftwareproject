/**
 * GameState.java
 * Holds all mutable flags that track the player's progress.
 * Centralising these here keeps the other classes clean and
 * makes it easy to add new flags in the future.
 */
public class GameState {
    public boolean lockerOpen   = false;
    public boolean suitWorn     = false;
    public boolean gateUnlocked = false;
    public boolean coreTaken    = false;
    public boolean gameWon      = false;
    public boolean running      = true;
}
