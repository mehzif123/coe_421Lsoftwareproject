import java.util.concurrent.atomic.AtomicBoolean;

/**
 * GuardNPC.java
 * An automated NPC character — a damaged deep-sea maintenance drone
 * that patrols the Pressure Gate.
 *
 * DESIGN PATTERN — Observer:
 *   Implements EntityBehavior (which extends GameEventListener).
 *   Registered with GameController as an observer.
 *   Reacts to "ROOM_ENTER" events so it can change mood when
 *   the player enters its room.
 *
 * MULTI-THREADING:
 *   Runs in its own thread. Periodically prints status messages
 *   that simulate independent behavior evolving over time.
 *   Shared state (mood, location) is accessed via synchronized
 *   methods to prevent data races with the main game thread.
 *
 * DYNAMIC BEHAVIOR:
 *   - Mood shifts from NEUTRAL → HOSTILE as time passes.
 *   - Becomes CALM if the gate is unlocked (goal achieved).
 *   - Prints context-specific warnings depending on scene.
 */
public class GuardNPC implements EntityBehavior {

    public enum Mood { NEUTRAL, HOSTILE, CALM }

    private final GameState      state;
    private final AtomicBoolean  running  = new AtomicBoolean(false);
    private Thread               thread;

    // Shared mutable state — always accessed via synchronized methods
    private Mood    mood            = Mood.NEUTRAL;
    private boolean playerNearby    = false;
    private int     tickCount       = 0;

    // How many ticks (each ~4 s) before the guard turns hostile
    private static final int HOSTILE_THRESHOLD = 3;

    public GuardNPC(GameState state) {
        this.state = state;
    }

    // ------------------------------------------------------------------ //
    //  EntityBehavior / Runnable
    // ------------------------------------------------------------------ //

    @Override
    public void start() {
        running.set(true);
        thread = new Thread(this, "GuardNPC-Thread");
        thread.setDaemon(true);   // dies when main thread exits
        thread.start();
    }

    @Override
    public void stop() {
        running.set(false);
        if (thread != null) thread.interrupt();
    }

    @Override
    public String getName() { return "Security Drone"; }

    /**
     * Main loop — runs independently on its own thread.
     * Sleeps between ticks; updates mood over time.
     */
    @Override
    public void run() {
        while (running.get() && !state.gameWon) {
            try {
                Thread.sleep(4000);   // tick every 4 seconds
            } catch (InterruptedException e) {
                break;
            }

            tick();
        }
    }

    /**
     * Synchronized tick — updates mood and prints a message.
     * Synchronized so the main thread cannot read mood mid-update.
     */
    private synchronized void tick() {
        if (state.gameWon) return;

        tickCount++;

        // Mood update logic
        if (state.gateUnlocked) {
            mood = Mood.CALM;
        } else if (tickCount >= HOSTILE_THRESHOLD) {
            mood = Mood.HOSTILE;
        }

        if (!playerNearby) return;   // only narrate if player is in the room

        // Print a message on the game thread's stdout (safe: println is synchronized internally)
        switch (mood) {
            case NEUTRAL:
                System.out.println("\n[Drone] The security drone sweeps its sensor beam across the gate. It hasn't noticed you yet.");
                break;
            case HOSTILE:
                System.out.println("\n[Drone] !! The drone's optical sensor locks onto you. It emits a sharp warning tone. Solve the panel — now!");
                break;
            case CALM:
                System.out.println("\n[Drone] The drone powers down its weapons. Gate access confirmed.");
                break;
        }
        System.out.print("\n> ");   // re-show prompt after async message
    }

    // ------------------------------------------------------------------ //
    //  Observer — GameEventListener
    // ------------------------------------------------------------------ //

    /**
     * Called by GameController (the Subject) whenever a notable event occurs.
     * Synchronized to safely update playerNearby from the main thread
     * while the NPC thread may be reading it.
     */
    @Override
    public synchronized void onEvent(String eventType, String detail) {
        if ("ROOM_ENTER".equals(eventType)) {
            playerNearby = "Pressure Gate".equals(detail);

            // Immediate reaction when player enters the gate room
            if (playerNearby) {
                System.out.println("[Drone] A battered maintenance drone hovers near the gate, its red sensor light blinking.");
            } else if ("Abyssal Trench".equals(detail) && mood == Mood.HOSTILE) {
                System.out.println("[Drone] (The drone's warning tone fades as you move away from the gate.)");
            }
        }
        if ("GATE_UNLOCKED".equals(eventType)) {
            mood = Mood.CALM;
        }
    }

    /** Thread-safe mood reader for other classes. */
    public synchronized Mood getMood() { return mood; }
}
