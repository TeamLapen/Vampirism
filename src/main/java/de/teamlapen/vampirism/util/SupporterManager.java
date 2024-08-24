package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.settings.Supporter;
import net.minecraft.util.RandomSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Handles the download, parsing and access of supporter information.
 * <p>
 * Can check skins here
 * <a href="http://skins.minecraft.net/MinecraftSkins/%s.png">skins.minecraft.net</a>
 */
public class SupporterManager {
    private final static Logger LOGGER = LogManager.getLogger();

    private static Supporter[] vampire = new Supporter[0];
    private static Supporter[] hunter = new Supporter[0];

    /**
     * Returns a randomly picked hunter
     */
    public static Supporter getRandomHunter(@NotNull RandomSource rnd) {
        if (hunter.length > 0) {
            return hunter[rnd.nextInt(hunter.length)];
        }
        return new Supporter(VReference.HUNTER_FACTION_ID, "none", "none", null, new HashMap<>());
    }

    /**
     * Returns a randomly picked vampire
     */
    public static Supporter getRandomVampire(@NotNull RandomSource rnd) {
        if (vampire.length > 0) {
            return vampire[rnd.nextInt(vampire.length)];
        }
        return new Supporter(VReference.VAMPIRE_FACTION_ID, "none", "none", null, new HashMap<>());
    }

    public static void init() {
        VampirismAPI.settings().getSupportersAsync().thenApply(optional -> {
            if (optional.isPresent()) {
                vampire = optional.get().stream().filter(s -> s.faction().equals(VReference.VAMPIRE_FACTION_ID)).toArray(Supporter[]::new);
                hunter = optional.get().stream().filter(s -> s.faction().equals(VReference.HUNTER_FACTION_ID)).toArray(Supporter[]::new);
                LOGGER.debug("Loaded {} vampire and {} hunter supporter", vampire.length, hunter.length);
                return optional.get();
            } else {
                LOGGER.warn("Failed to retrieve supporters");
                return new ArrayList<Supporter>();
            }
        });
    }

    public static Stream<Supporter> getSupporter() {
        return Stream.concat(Stream.of(hunter), Stream.of(vampire));
    }

}
