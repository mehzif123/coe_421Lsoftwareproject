import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Room.java
 * Represents a single location in the game world.
 * Holds a display name, a description, and a map of
 * directional exits (e.g. "d" -> "Airlock").
 */
public class Room {
    public final String name;
    public final String description;
    public final Map<String, String> exits = new LinkedHashMap<>();

    public Room(String name, String description) {
        this.name        = name;
        this.description = description;
    }
}
