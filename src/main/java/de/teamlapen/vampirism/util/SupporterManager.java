package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.VampirismAttachments;
import de.teamlapen.vampirism.api.settings.Supporter;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.util.RandomSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;

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
        return new Supporter(VReference.HUNTER_FACTION_ID, "none", "none", null, new HashMap<>());
    }

    /**
     * Returns a randomly picked vampire
     */
    public static Supporter getRandomVampire(@NotNull RandomSource rnd) {
        if (supporters[0].length > 0) {
            return supporters[0][rnd.nextInt(supporters[0].length)];
        }
        return new Supporter(VReference.VAMPIRE_FACTION_ID, "none", "none",  null, new HashMap<>());
    }


    private static @NotNull String getDebugString() {
        return "Vampires: " + Arrays.toString(supporters[0]) + " Hunters: " + Arrays.toString(supporters[1]);
    }

    public static void init() {
        VampirismAPI.settings().getSupportersAsync().thenAccept(optional -> {
            optional.ifPresentOrElse(supporters -> {
                var supporter = new Supporter[2][];
                supporter[0] = supporters.stream().filter(s -> s.faction().equals(VReference.VAMPIRE_FACTION_ID)).toArray(Supporter[]::new);
                supporter[1] = supporters.stream().filter(s -> s.faction().equals(VReference.HUNTER_FACTION_ID)).toArray(Supporter[]::new);
                SupporterManager.supporters = supporter;
                LOGGER.trace("Supporters {}", getDebugString());
            }, () -> LOGGER.warn("Failed to retrieve supporters"));
        });
    }
}
