import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GameController.java
 * Handles all command dispatch and game-logic responses.
 * Movement, item actions, puzzles, and win condition live here.
 * Display helpers are delegated to other classes.
 */
public class GameController {

    private final Player          player;
    private final GameState       state;
    private final Map<String, Room> rooms;
    private final OptionsProvider opts;

    public GameController(Player player, GameState state, Map<String, Room> rooms) {
        this.player = player;
        this.state  = state;
        this.rooms  = rooms;
        this.opts   = new OptionsProvider(player, state);
    }

    //  Top-level dispatch

    public void handleCommand(String cmd) {
        switch (cmd) {
            case "d": case "u": case "e": case "w":
                move(cmd);
                break;
            case "lo":
                showCurrentRoom();
                break;
            case "i":
                System.out.println("Inventory: " + player.inventoryText());
                break;
            case "st":
                printStatus();
                break;
            case "op lk":
                doOpenLocker();
                break;
            case "ta su":
                doTakeSuit();
                break;
            case "ta med":
                doTakeMedkit();
                break;
            case "wr su":
                doWearSuit();
                break;
            case "ex pa":
                doExaminePanel();
                break;
            case "so ox":
                doSolveOxygen();
                break;
            case "ta co":
                doTakeCore();
                break;
            default:
                System.out.println("The current does not understand that command.");
        }
    }

    //  Display helpers

    public void showCurrentRoom() {
        Room r = player.currentRoom;
        System.out.println("\n" + r.name);
        System.out.println(r.description);
        System.out.println("Exits: " + exitText(r));
        describeContext();
        if (player.currentRoom.name.equals("Surface Platform") && state.coreTaken && !state.gameWon) {
            doWin();
        }
    }

    public void showOptions() {
        System.out.println("Best options: " + String.join(" | ", opts.getOptions()));
    }

    private void printStatus() {
        System.out.println("Status: suit="   + (state.suitWorn     ? "worn"    : "not worn")
                         + ", gate="          + (state.gateUnlocked ? "open"    : "locked")
                         + ", core="          + (state.coreTaken    ? "secured" : "not taken"));
    }

    private void describeContext() {
        String name = player.currentRoom.name;
        switch (name) {
            case "Equipment Locker":
                if (!state.lockerOpen) {
                    System.out.println("One locker seems strong enough to survive the pressure. It might open with force.");
                } else if (!player.has("reinforced suit")) {
                    System.out.println("Inside the locker sit a lamp, a reinforced diving suit, and a medkit.");
                }
                break;
            case "Pressure Gate":
                if (!state.gateUnlocked) {
                    System.out.println("The eastern gate is sealed. The console flickers with a failing oxygen puzzle.");
                }
                break;
            case "Abyssal Trench":
                if (!state.coreTaken) {
                    System.out.println("On a basalt pedestal nearby rests the Abyssal Core.");
                }
                break;
            case "Surface Platform":
                if (state.coreTaken) {
                    System.out.println("The cold wind hits your face. The Abyssal Core is finally out of the trench.");
                }
                break;
        }
    }

    private String exitText(Room room) {
        List<String> parts = new ArrayList<>();
        for (String dir : room.exits.keySet()) {
            switch (dir) {
                case "d":  parts.add("down(d)");   break;
                case "u":  parts.add("up(u)");     break;
                case "e":  parts.add("east(e)");   break;
                case "w":  parts.add("west(w)");   break;
            }
        }
        return String.join(", ", parts) + ".";
    }

    //  Movement

    private void move(String dir) {
        String next = player.currentRoom.exits.get(dir);
        if (next == null) {
            System.out.println("You cannot go that way.");
            return;
        }
        if (player.currentRoom.name.equals("Pressure Gate") && dir.equals("e") && !state.gateUnlocked) {
            System.out.println("The pressure gate is still locked.");
            return;
        }
        player.currentRoom = rooms.get(next);
        showCurrentRoom();
    }

    //  Item & puzzle actions
    

    private void doOpenLocker() {
        if (!player.currentRoom.name.equals("Equipment Locker")) {
            System.out.println("There is no locker here.");
            return;
        }
        if (state.lockerOpen) {
            System.out.println("The locker is already open.");
            return;
        }
        state.lockerOpen = true;
        System.out.println("The locker groans open, revealing a lamp, a reinforced diving suit, and a medkit.");
    }

    private void doTakeSuit() {
        if (player.currentRoom.name.equals("Equipment Locker") && state.lockerOpen && !player.has("reinforced suit")) {
            player.add("reinforced suit");
            System.out.println("You take the reinforced diving suit.");
        } else {
            System.out.println("You cannot take that now.");
        }
    }

    private void doTakeMedkit() {
        if (player.currentRoom.name.equals("Equipment Locker") && state.lockerOpen && !player.has("medkit")) {
            player.add("medkit");
            System.out.println("You take the medkit.");
        } else {
            System.out.println("You cannot take that now.");
        }
    }

    private void doWearSuit() {
        if (player.has("reinforced suit")) {
            state.suitWorn = true;
            System.out.println("You lock the reinforced suit into place. The pressure seals hold.");
        } else {
            System.out.println("You do not have the suit.");
        }
    }

    private void doExaminePanel() {
        if (player.currentRoom.name.equals("Pressure Gate")) {
            System.out.println("A cracked prompt blinks: BALANCE MIX / FLOW / RESERVE. The correct short command will solve it.");
        } else {
            System.out.println("There is no panel here.");
        }
    }

    private void doSolveOxygen() {
        if (player.currentRoom.name.equals("Pressure Gate")) {
            state.gateUnlocked = true;
            System.out.println("The panel flashes green. The pressure gate unlocks and slides eastward.");
        } else {
            System.out.println("There is no oxygen puzzle here.");
        }
    }

    private void doTakeCore() {
        if (!player.currentRoom.name.equals("Abyssal Trench") || state.coreTaken) {
            System.out.println("You cannot take that now.");
            return;
        }
        if (!state.suitWorn) {
            System.out.println("The pressure nearly crushes you. You need the reinforced suit first.");
            return;
        }
        state.coreTaken = true;
        player.add("abyssal core");
        System.out.println("You lift the Abyssal Core from its pedestal. The trench seems to recoil around you.");
    }

    private void doWin() {
        if (player.currentRoom.name.equals("Surface Platform") && state.coreTaken) {
            state.gameWon = true;
            state.running = false;
            System.out.println("\n" + "=".repeat(50));
            System.out.println("\033[1m         MISSION ACCOMPLISHED!         \033[0m");
            System.out.println("\033[1m     You have recovered the Abyssal Core!     \033[0m");
            System.out.println("\033[1m         Humanity is saved!         \033[0m");
            System.out.println("=".repeat(50));
        } else {
            System.out.println("You have not completed the mission yet.");
        }
    }
}
