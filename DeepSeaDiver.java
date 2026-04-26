import java.util.Map;
import java.util.Scanner;

/**
 * DeepSeaDiver.java
 * Application entry point.
 * Wires all components together and runs the main input loop.
 *
 *
 * OBJECTIVE:
 *   Recover the Abyssal Core from the trench and return to the surface alive.
 */
public class DeepSeaDiver {

    private final Scanner        scanner = new Scanner(System.in);
    private final Player         player  = new Player();
    private final GameState      state   = new GameState();
    private final Map<String, Room> rooms   = WorldBuilder.build();
    private final CommandParser  parser  = new CommandParser();
    private final GameController game;

    public DeepSeaDiver() {
        player.currentRoom = rooms.get("Surface Platform");
        game = new GameController(player, state, rooms);
    }

    public static void main(String[] args) {
        new DeepSeaDiver().run();
    }

    private void run() {
        printIntro();
        game.showCurrentRoom();

        while (state.running) {
            game.showOptions();
            System.out.print("\n> ");
            if (!scanner.hasNextLine()) break;

            String raw = scanner.nextLine().trim();

            // Empty input : re-show options as a hint
            if (raw.isEmpty()) {
                game.showOptions();
                continue;
            }

            String cmd = parser.parse(raw);
            game.handleCommand(cmd);
        }
    }

    private void printIntro() {
        System.out.println("============================================");
        System.out.println("               DEEP SEA DIVER               ");
        System.out.println("============================================");
        System.out.println("Recover the Abyssal Core and return alive.");
        System.out.println("Type the short commands shown in brackets,");
        System.out.println("or press Enter with no input for a hint.");
        System.out.println("============================================");
    }
}
