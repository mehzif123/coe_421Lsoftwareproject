import java.util.HashMap;
import java.util.Map;

/**
 * WorldBuilder.java
 * Responsible solely for constructing all Room objects,
 * wiring their exits together, and returning the complete map.
 * Follows the Single Responsibility Principle — world layout
 * lives here, not scattered through GameController.
 */
public class WorldBuilder {

    /**
     * Build and return all rooms keyed by room name.
     * The caller receives the complete, fully-connected world map.
     */
    public static Map<String, Room> build() {
        Room surface = new Room("Surface Platform",
                "The launch platform rocks above the black water. A steel cage elevator waits below.");
        Room airlock = new Room("Airlock",
                "A flooded entry chamber hums around you. Dim lights pulse along the floor.");
        Room locker  = new Room("Equipment Locker",
                "A narrow side room lined with rusted lockers. One large locker still looks usable.");
        Room gate    = new Room("Pressure Gate",
                "A reinforced circular gate blocks the eastern tunnel. A corroded console waits beside it.");
        Room trench  = new Room("Abyssal Trench",
                "The ocean floor drops away into a vast trench. Thermal vents hiss nearby.");

        // Wire up exits
        surface.exits.put("d",  "Airlock");

        airlock.exits.put("u",  "Surface Platform");
        airlock.exits.put("d",  "Pressure Gate");
        airlock.exits.put("we", "Equipment Locker");

        locker.exits.put("e",   "Airlock");

        gate.exits.put("u",     "Airlock");
        gate.exits.put("e",     "Abyssal Trench");

        trench.exits.put("we",  "Pressure Gate");

        Map<String, Room> rooms = new HashMap<>();
        rooms.put(surface.name, surface);
        rooms.put(airlock.name, airlock);
        rooms.put(locker.name,  locker);
        rooms.put(gate.name,    gate);
        rooms.put(trench.name,  trench);

        return rooms;
    }
}
