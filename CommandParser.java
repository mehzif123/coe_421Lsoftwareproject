import java.util.HashMap;
import java.util.Map;

/**
 * CommandParser.java
 * Normalises raw player input into canonical short-form commands.
 * All synonym mappings live here so adding new aliases never
 * requires touching GameController.
 */
public class CommandParser {

    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        ALIASES.put("down",                  "d");
        ALIASES.put("up",                    "u");
        ALIASES.put("east",                  "e");
        ALIASES.put("west",                  "we");
        ALIASES.put("look",                  "lo");
        ALIASES.put("inventory",             "i");
        ALIASES.put("status",                "st");
        ALIASES.put("open locker",           "op lk");
        ALIASES.put("take suit",             "ta su");
        ALIASES.put("take reinforced suit",  "ta su");
        ALIASES.put("take medkit",           "ta med");
        ALIASES.put("wear suit",             "wr su");
        ALIASES.put("wear reinforced suit",  "wr su");
        ALIASES.put("examine panel",         "ex pa");
        ALIASES.put("solve oxygen",          "so ox");
        ALIASES.put("take core",             "ta co");
        ALIASES.put("win game",              "win");
    }

    /**
     * Collapse whitespace, lowercase, then resolve aliases.
     * If no alias matches, the trimmed input is returned as-is.
     */
    public String parse(String rawInput) {
        String cleaned = rawInput.trim().toLowerCase().replaceAll("\\s+", " ");
        return ALIASES.getOrDefault(cleaned, cleaned);
    }
}
