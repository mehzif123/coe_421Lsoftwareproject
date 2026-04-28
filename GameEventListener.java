/**
 * GameEventListener.java
 * Observer interface for the Observer design pattern.
 * Any class that wants to be notified of game events
 * (e.g., state changes, room transitions) implements this interface.
 *
 * Design Pattern: Observer (GoF)
 * - Subject: GameController (notifies listeners on state changes)
 * - Observers: HUD, NPC entities, dynamic objects
 */
public interface GameEventListener {
    void onEvent(String eventType, String detail);
}
