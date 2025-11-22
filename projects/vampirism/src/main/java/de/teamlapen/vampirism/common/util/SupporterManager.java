package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.settings.Supporter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SupporterManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private Map<ResourceLocation, List<Supporter>> supporters = new HashMap<>();

    /**
     * Returns a randomly picked hunter
     */
    public Supporter getRandomHunter(@NotNull RandomSource rnd) {
        List<Supporter> hunters = this.supporters.getOrDefault(VReference.HUNTER_FACTION_ID, List.of());
        if (!hunters.isEmpty()) {
            return hunters.get(rnd.nextInt(hunters.size()));
        }
        return new Supporter(VReference.HUNTER_FACTION_ID, "none", "none", null, new HashMap<>());
    }

    /**
     * Returns a randomly picked vampire
     */
    public Supporter getRandomVampire(@NotNull RandomSource rnd) {
        List<Supporter> vampires = this.supporters.getOrDefault(VReference.VAMPIRE_FACTION_ID, List.of());
        if (!vampires.isEmpty()) {
            return vampires.get(rnd.nextInt(vampires.size()));
        }
        return new Supporter(VReference.VAMPIRE_FACTION_ID, "none", "none", null, new HashMap<>());
    }

    public void init() {
        VampirismAPI.settings().getSupportersAsync().thenAccept(optional -> {
            if (optional.isPresent()) {
                this.supporters = optional.get().stream().collect(Collectors.groupingBy(Supporter::faction));
                LOGGER.debug("Loaded {} supporter for {} factions", optional.get().size(), this.supporters.size());
            } else {
                LOGGER.warn("Failed to retrieve supporters");
            }
        });
    }

    public Stream<Supporter> getSupporter() {
        return supporters.values().stream().flatMap(List::stream);
    }

}
