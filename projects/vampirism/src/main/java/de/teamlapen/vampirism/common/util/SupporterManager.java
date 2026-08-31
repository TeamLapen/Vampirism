package de.teamlapen.vampirism.common.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.JsonOps;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.common.util.supporter.Supporter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SupporterManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private Map<Identifier, List<Supporter>> supporters = new HashMap<>();
    private Map<String, Supporter> supportersByPlayer = Map.of();

    /**
     * Returns a randomly picked hunter
     */
    public Supporter getRandomHunter(@NotNull RandomSource rnd) {
        List<Supporter> hunters = this.supporters.getOrDefault(VReference.HUNTER_FACTION_ID, List.of());
        if (!hunters.isEmpty()) {
            return hunters.get(rnd.nextInt(hunters.size()));
        }
        return Supporter.FALLBACK;
    }

    /**
     * Returns a randomly picked vampire
     */
    public Supporter getRandomVampire(@NotNull RandomSource rnd) {
        List<Supporter> vampires = this.supporters.getOrDefault(VReference.VAMPIRE_FACTION_ID, List.of());
        if (!vampires.isEmpty()) {
            return vampires.get(rnd.nextInt(vampires.size()));
        }
        return Supporter.FALLBACK;
    }

    /**
     * Load supporter when we have access to the registries
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLoadLevel(LevelEvent.Load event) {
        loadSupporter(event.getLevel().registryAccess());
    }

    public void loadSupporter(RegistryAccess registryAccess) {
        InputStream inputStream = VampirismMod.class.getResourceAsStream("/supporters.json");
        if (inputStream != null) {
            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
            try (jsonReader) {
                var ops = RegistryOps.create(JsonOps.INSTANCE, registryAccess);
                List<Supporter> loadedSupporters = Supporter.CODEC.codec().listOf().parse(ops, JsonParser.parseReader(jsonReader)).getPartialOrThrow();
                Map<String, Supporter> supportersByPlayer = new HashMap<>();
                for (Supporter supporter : loadedSupporters) {
                    if (!supporter.player().isBlank() && supportersByPlayer.putIfAbsent(supporter.player(), supporter) != null) {
                        throw new IllegalStateException("Duplicate supporter player name " + supporter.player());
                    }
                }
                this.supporters = loadedSupporters.stream().collect(Collectors.groupingBy(Supporter::faction));
                this.supportersByPlayer = Map.copyOf(supportersByPlayer);
            } catch (JsonSyntaxException | IOException ex) {
                LOGGER.error("Failed to retrieve supporter from file", ex);
            }
        }
    }

    public Stream<Supporter> getSupporter() {
        return supporters.values().stream().flatMap(List::stream);
    }

    public Optional<Supporter> getSupporter(String player) {
        return Optional.ofNullable(this.supportersByPlayer.get(player));
    }

}
