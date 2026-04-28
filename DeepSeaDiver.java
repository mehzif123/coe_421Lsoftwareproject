import java.util.Map;
import java.util.Scanner;

/**
 * DeepSeaDiver.java
 * Application entry point.
 * Wires all components together and runs the main input loop.
 *
 * HOW TO RUN:
 *   javac -d out src/*.java
 *   java  -cp out DeepSeaDiver
 *
 * OBJECTIVE:
 *   Recover the Abyssal Core from the trench and return to the surface alive.
 *
 * ARCHITECTURE SUMMARY:
 *   - Observer Pattern: GameController (Subject) notifies GuardNPC and
 *     OxygenTimer (Observers) of all significant game events.
 *   - Multi-threading: GuardNPC and OxygenTimer each run on their own
 *     daemon threads, independently of the main input loop.
 *   - Thread safety: volatile fields in GameState, AtomicInteger in
 *     OxygenTimer, synchronized methods in GuardNPC.
 */
public class DeepSeaDiver {

    private final Scanner           scanner = new Scanner(System.in);
    private final Player            player  = new Player();
    private final GameState         state   = new GameState();
    private final Map<String, Room> rooms   = WorldBuilder.build();
    private final CommandParser     parser  = new CommandParser();
    private final GameController    game;

    // Dynamic entities that run on background threads
    private final GuardNPC          guard;
    private final OxygenTimer       oxygen;

    public DeepSeaDiver() {
        player.currentRoom = rooms.get("Surface Platform");
        game   = new GameController(player, state, rooms);
        guard  = new GuardNPC(state);
        oxygen = new OxygenTimer(state, player);

        // Register both entities as observers with GameController (Observer pattern)
        game.addObserver(guard);
        game.addObserver(oxygen);
    }

    public static void main(String[] args) {
        new DeepSeaDiver().run();
    }

    private void run() {
        printIntro();

        // Start background entity threads BEFORE the game loop
        guard.start();
        oxygen.start();

        game.showCurrentRoom();
        game.showOptions();

        while (state.running) {
            System.out.print("\n> ");
            if (!scanner.hasNextLine()) break;

            String raw = scanner.nextLine().trim();

            // Empty input — re-show options as a hint
            if (raw.isEmpty()) {
                game.showOptions();
                continue;
            }

            String cmd = parser.parse(raw);
            game.handleCommand(cmd);
            game.showOptions();
        }

        // Clean shutdown — stop all background threads
        guard.stop();
        oxygen.stop();

        if (state.gameWon) {
            System.out.println("\n\033[1m=== MISSION ACCOMPLISHED ===\033[0m");
        } else if (!state.running) {
            System.out.println("\n=== GAME OVER ===");
        }
    }

    private void printIntro() {
        System.out.println("============================================================");
        System.out.println("                    DEEP SEA DIVER                        ");
        System.out.println("============================================================");
        System.out.println("Recover the Abyssal Core and return alive.");
        System.out.println("WARNING: Your oxygen depletes over time. Work fast.");
        System.out.println("A security drone patrols the Pressure Gate — beware.");
        System.out.println("Type the short commands shown in brackets,");
        System.out.println("or press Enter with no input for a hint.");
        System.out.println("============================================================");
    }
}
