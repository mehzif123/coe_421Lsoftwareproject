/**
 * GameState.java
 * Holds all mutable flags that track the player's progress.
 * Centralising these here keeps the other classes clean and
 * makes it easy to add new flags in the future.
 *
 * THREAD SAFETY:
 *   Fields read or written by background threads (OxygenTimer,
 *   GuardNPC) are declared volatile so changes are immediately
 *   visible across threads without requiring full synchronization.
 *   Fields accessed only from the main game thread are plain booleans.
 */
public class GameState {
    public boolean  lockerOpen   = false;
    public boolean  suitWorn     = false;
    public boolean  gateUnlocked = false;
    public boolean  coreTaken    = false;

    // Read by OxygenTimer thread, written by main thread → volatile
    public volatile boolean gameWon  = false;
    public volatile boolean running  = true;
}
