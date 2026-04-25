import java.util.ArrayList;
import java.util.List;

/**
 * Player.java
 * Tracks the player's current location and inventory.
 */
public class Player {
    public Room currentRoom;
    private final List<String> inventory = new ArrayList<>();

    /** Add an item to the player's inventory. */
    public void add(String item) {
        inventory.add(item);
    }

    /** Returns true if the player is carrying the given item. */
    public boolean has(String item) {
        return inventory.contains(item);
    }

    /** Returns a comma-separated inventory string, or "empty". */
    public String inventoryText() {
        return inventory.isEmpty() ? "empty" : String.join(", ", inventory);
    }
}
