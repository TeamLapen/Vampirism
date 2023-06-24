package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.settings.Supporter;
import net.minecraft.util.RandomSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Handles the download, parsing and access of supporter information.
 * <p>
 * Can check skins here
 * <a href="http://skins.minecraft.net/MinecraftSkins/%s.png">skins.minecraft.net</a>
 */
public class SupporterManager {
    private final static Logger LOGGER = LogManager.getLogger();

    private static Supporter[][] supporters = new Supporter[2][0];


    /**
     * Returns a randomly picked hunter
     */
    public static Supporter getRandomHunter(@NotNull RandomSource rnd) {
        if (supporters[1].length > 0) {
            return supporters[1][rnd.nextInt(supporters[1].length)];
        }
        return new Supporter(REFERENCE.HUNTER_PLAYER_KEY, "none", "none", 0, null);
    }

    /**
     * Returns a randomly picked vampire
     */
    public static Supporter getRandomVampire(@NotNull RandomSource rnd) {
        if (supporters[0].length > 0) {
            return supporters[0][rnd.nextInt(supporters[0].length)];
        }
        return new Supporter(REFERENCE.VAMPIRE_PLAYER_KEY, "none", "none", 0, null);
    }


    private static @NotNull String getDebugString() {
        return "Vampires: " + Arrays.toString(supporters[0]) + " Hunters: " + Arrays.toString(supporters[1]);
    }

    public static void init() {
        VampirismAPI.settings().getSupportersAsync().thenAccept(supporters -> {
            var supporter = new Supporter[2][];
            supporter[0] = supporters.stream().filter(s -> s.faction().equals(REFERENCE.VAMPIRE_PLAYER_KEY)).toArray(Supporter[]::new);
            supporter[1] = supporters.stream().filter(s -> s.faction().equals(REFERENCE.HUNTER_PLAYER_KEY)).toArray(Supporter[]::new);
            SupporterManager.supporters = supporter;
            LOGGER.trace("Supporters {}", getDebugString());
        });
    }
}
