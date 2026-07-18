package de.teamlapen.vampirism.common.util.wings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IWingsEntity;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WingsManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Codec<List<Wings>> WINGS_LIST_CODEC = Wings.CODEC.listOf();

    private volatile Map<UUID, Wings> wingsMap = Map.of();

    public Optional<Wings> getWings(UUID userId) {
        return Optional.ofNullable(wingsMap.get(userId));
    }

    public Optional<Wings> getWings(Player player) {
        return getWings(player.nameAndId().id());
    }

    public Stream<IWingsEntity.Texture> getAvailableWings(Player player) {
        if (!FMLEnvironment.isProduction()) {
            return Stream.of(IWingsEntity.Texture.values());
        }
        return getWings(player).map(Wings::textures).map(Collection::stream).orElseGet(() -> Stream.of(IWingsEntity.Texture.DEFAULT));
    }

    private CompletableFuture<Map<UUID, Wings>> loadFromFile() {
        return CompletableFuture.supplyAsync(() -> {
            try (InputStream inputStream = VampirismMod.class.getResourceAsStream("/wings.json")) {
                if (inputStream == null) {
                    LOGGER.warn("Wings file not found");
                    return Map.of();
                }
                var string = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                var result = WINGS_LIST_CODEC.decode(JsonOps.INSTANCE, StrictJsonParser.parse(string));
                List<Wings> wingsList = result.getPartialOrThrow().getFirst();
                result.ifError(error -> {
                    LOGGER.warn("Failed to parse wings file: {}", error.message());
                    LOGGER.warn("{}", string);
                });
                return wingsList.stream().collect(Collectors.toUnmodifiableMap(Wings::userId, Function.identity()));
            } catch (Exception e) {
                throw new RuntimeException("Failed to load wings file", e);
            }
        }, Util.nonCriticalIoPool());
    }

    @SubscribeEvent
    public void onSetup(FMLCommonSetupEvent event) {
        loadFromFile().thenAccept(map -> event.enqueueWork(() -> this.wingsMap = map))
                .exceptionally(e -> {
                    LOGGER.warn("Failed to load wings file", e);
                    return null;
                });
    }
}