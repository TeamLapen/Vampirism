package de.teamlapen.vampirism.config.bloodvalues;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BloodValueReader<T> {
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

    public CompletableFuture<Map<ResourceLocation, BloodValueBuilder>> prepare(IResourceManager manager, Executor executor){
        return CompletableFuture.supplyAsync(() -> loadValues(manager, new HashMap<>()), executor);
    }

    public Map<ResourceLocation, BloodValueBuilder> loadValues(IResourceManager manager, Map<ResourceLocation, BloodValueBuilder> values) {
        for (ResourceLocation resourcePath : manager.listResources(this.directory, (file) -> file.endsWith(".json"))) {
            String s = resourcePath.getPath();
            ResourceLocation resourceName = new ResourceLocation(resourcePath.getNamespace(), s.substring(this.directory.length() +1, s.length() - PATH_SUFFIX_LENGTH));
            try {
                for (IResource resource : manager.getResources(resourcePath)) {
                    try(Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))){
                        JsonObject json = JSONUtils.fromJson(GSON, reader, JsonObject.class);
                        if (json == null){
                            LOGGER.error("Couldn't load {} blood values {} from {} in datapack {} as it is empty or null", this.name, resourceName, resourcePath, resource.getSourceName());
                        } else {
                            values.computeIfAbsent(resourceName, (id) -> new BloodValueBuilder()).addFromJson(json, resource.getSourceName());
                        }
                    } catch (RuntimeException | IOException e){
                        LOGGER.error("Couldn't read {} blood values {} from {} in datapack {}", this.name, resourceName, resourcePath, resource.getSourceName(), e);
                    } finally {
                        IOUtils.closeQuietly(resource);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Couldn't read {} blood values {} from {}", this.name, resourceName, resourcePath, e);
            }
        }
        return values;
    }

    public void load(Map<ResourceLocation, BloodValueBuilder> values) {
        this.valueConsumer.accept(values.entrySet().stream().flatMap(a -> a.getValue().build().entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}
