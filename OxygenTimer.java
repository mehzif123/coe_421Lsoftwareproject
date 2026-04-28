import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * OxygenTimer.java
 * A dynamic game object that represents the player's oxygen supply.
 * Depletes over time independently on its own background thread.
 *
 * MULTI-THREADING:
 *   Runs on a dedicated thread — completely independent of the main
 *   game loop thread. Uses AtomicInteger for the oxygen level so that
 *   reads from the main thread and decrements from this thread are both
 *   atomic and visible without explicit synchronization blocks.
 *
 * DYNAMIC BEHAVIOR:
 *   - Oxygen ticks down every 15 seconds.
 *   - Warns the player at 50% and 25%.
 *   - Kills the game (game over) at 0% if the core has not been taken.
 *   - Pauses depletion while the player is on the Surface Platform
 *     (above water — contextual behavior).
 */
public class OxygenTimer implements EntityBehavior {

    private static final int MAX_OXYGEN  = 100;
    private static final int TICK_AMOUNT = 10;   // lost per tick
    private static final int TICK_MS     = 15_000;

    private final GameState      state;
    private final Player         player;
    private final AtomicBoolean  running  = new AtomicBoolean(false);
    private final AtomicInteger  oxygen   = new AtomicInteger(MAX_OXYGEN);
    private Thread               thread;

    public OxygenTimer(GameState state, Player player) {
        this.state  = state;
        this.player = player;
    }

    // ------------------------------------------------------------------ //
    //  EntityBehavior / Runnable
    // ------------------------------------------------------------------ //

    @Override
    public void start() {
        running.set(true);
        thread = new Thread(this, "OxygenTimer-Thread");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void stop() {
        running.set(false);
        if (thread != null) thread.interrupt();
    }

    @Override
    public String getName() { return "Oxygen Supply"; }

    @Override
    public void run() {
        while (running.get() && !state.gameWon) {
            try {
                Thread.sleep(TICK_MS);
            } catch (InterruptedException e) {
                break;
            }

            // Oxygen does not deplete on the surface
            if ("Surface Platform".equals(player.currentRoom.name)) continue;

            int current = oxygen.addAndGet(-TICK_AMOUNT);

            if (current <= 0 && !state.coreTaken) {
                // Game over — set flag on shared state (volatile field)
                state.running = false;
                System.out.println("\n[O2] Your oxygen is depleted. Darkness closes in... Game over.");
                System.out.print("\n> ");
                break;
            } else if (current == 50) {
                System.out.println("\n[O2] Warning: oxygen at 50%. Conserve your air.");
                System.out.print("\n> ");
            } else if (current == 30) {
                System.out.println("\n[O2] Critical: oxygen at 30%! Find the core and surface quickly!");
                System.out.print("\n> ");
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  Observer — GameEventListener
    // ------------------------------------------------------------------ //

    /**
     * Refills oxygen slightly when the player returns to the airlock
     * (air pockets). Demonstrates contextual object behavior.
     */
    @Override
    public void onEvent(String eventType, String detail) {
        if ("ROOM_ENTER".equals(eventType) && "Airlock".equals(detail)) {
            int refill = Math.min(MAX_OXYGEN, oxygen.addAndGet(5));
            oxygen.set(refill);
            if (refill < MAX_OXYGEN) {
                System.out.println("[O2] Air pocket in the airlock — oxygen + 5%. Current: " + refill + "%");
            }
        }
    }

    /** Returns current oxygen level — thread-safe via AtomicInteger. */
    public int getOxygen() { return oxygen.get(); }
}
