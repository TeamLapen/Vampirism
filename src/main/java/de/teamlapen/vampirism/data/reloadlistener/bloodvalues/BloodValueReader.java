package de.teamlapen.vampirism.data.reloadlistener.bloodvalues;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BloodValueReader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final int PATH_SUFFIX_LENGTH = ".json".length();
    private final Consumer<Map<ResourceLocation, Float>> valueConsumer;
    private final String directory;
    private final String name;

    public BloodValueReader(Consumer<Map<ResourceLocation, Float>> valueConsumer, String directory, String name) {
        this.valueConsumer = valueConsumer;
        this.directory = directory;
        this.name = name;
    }

    public @NotNull CompletableFuture<Map<String, BloodValueBuilder>> prepare(@NotNull ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> load(manager), executor);
    }

    public @NotNull Map<String, BloodValueBuilder> load(@NotNull ResourceManager manager) {
        Map<String, BloodValueBuilder> values = new HashMap<>();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : manager.listResourceStacks(this.directory, (file) -> file.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            String s = resourcelocation.getPath();
            ResourceLocation resourceName = new ResourceLocation(resourcelocation.getNamespace(), s.substring(this.directory.length() + 1, s.length() - PATH_SUFFIX_LENGTH));
            for (Resource resource : entry.getValue()) {
                try (Reader reader = resource.openAsReader()) {
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    BloodValueFile file = BloodValueFile.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, jsonElement)).getOrThrow(false, LOGGER::error);
                    values.computeIfAbsent(resourceName.getPath(), (id) -> new BloodValueBuilder()).addFromFile(new BloodValueBuilder.BuilderEntries(file.values().stream().map(a -> new BloodValueBuilder.Proxy(a, resource.sourcePackId())).toList(), file.replace()));
                } catch (Exception e) {
                    LOGGER.error("Couldn't read {} blood values {} from {}", this.name, resourceName, resource.sourcePackId(), e);
                }
            }
        }
        return values;
    }

    public void load(@NotNull Map<String, BloodValueBuilder> values) {
        this.valueConsumer.accept(values.entrySet().stream().flatMap(a -> a.getValue().build().entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}
