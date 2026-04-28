/**
 * EntityBehavior.java
 * Interface for any entity (NPC or object) that has dynamic,
 * time-based or context-dependent behavior.
 *
 * Part of the Observer pattern — entities also implement
 * GameEventListener so they can react to game events.
 */
public interface EntityBehavior extends GameEventListener, Runnable {

    /** Called once to start the entity's independent thread. */
    void start();

    /** Called to cleanly stop the entity's thread. */
    void stop();

    /** Returns the entity's name for logging/display. */
    String getName();
}
