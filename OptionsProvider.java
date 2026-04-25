import java.util.ArrayList;
import java.util.List;

/**
 * OptionsProvider.java
 * Computes the list of contextual hint options shown to the player
 * after each action. Kept separate from GameController to avoid
 * mixing display logic with game logic.
 */
public class OptionsProvider {

    private final Player    player;
    private final GameState state;

    public OptionsProvider(Player player, GameState state) {
        this.player = player;
        this.state  = state;
    }

    /** Returns the relevant command hints for the current room and game state. */
    public List<String> getOptions() {
        String room = player.currentRoom.name;
        List<String> opts = new ArrayList<>();

        switch (room) {
            case "Surface Platform":
                opts.add("down(d)");
                opts.add("look(lo)");
                opts.add("status(st)");
                break;

            case "Airlock":
                opts.add("down(d)");
                opts.add("up(u)");
                opts.add("west(we)");
                break;

            case "Equipment Locker":
                if (!state.lockerOpen) {
                    opts.add("open locker(op lk)");
                    opts.add("east(e)");
                    opts.add("look(lo)");
                } else if (!player.has("reinforced suit")) {
                    opts.add("take suit(ta su)");
                    opts.add("take medkit(ta med)");
                    opts.add("east(e)");
                } else if (!state.suitWorn) {
                    opts.add("wear suit(wr su)");
                    opts.add("east(e)");
                    opts.add("look(lo)");
                } else {
                    opts.add("east(e)");
                    opts.add("look(lo)");
                    opts.add("status(st)");
                }
                break;

            case "Pressure Gate":
                if (!state.gateUnlocked) {
                    opts.add("examine panel(ex pa)");
                    opts.add("solve oxygen(so oxygen)");
                    opts.add("up(u)");
                } else {
                    opts.add("east(e)");
                    opts.add("up(u)");
                }
                break;

            case "Abyssal Trench":
                if (!state.coreTaken) {
                    opts.add("take core(ta co)");
                    opts.add("west(we)");
                    opts.add("look(lo)");
                } else {
                    opts.add("west(we)");
                    opts.add("inventory(i)");
                    opts.add("status(st)");
                }
                break;

            default:
                opts.add("look(lo)");
                opts.add("status(st)");
        }

        return opts;
    }
}
