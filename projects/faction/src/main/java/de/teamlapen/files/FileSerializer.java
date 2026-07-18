package de.teamlapen.files;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

public class FileSerializer<T> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Path path;
    private final Codec<T> codec;
    private final RegistryOps<JsonElement> registryOps;
    private final Supplier<T> defaultValue;

    public FileSerializer(Path path, RegistryAccess registryAccess, Codec<T> codec, Supplier<T> defaultValue) {
        this.path = path;
        this.codec = codec;
        this.registryOps = registryAccess.createSerializationContext(JsonOps.INSTANCE);
        this.defaultValue = defaultValue;
    }

    public void initialize() {
        if (!Files.exists(this.path) && this.path.getParent().toFile().mkdirs()) {
            save(this.defaultValue.get());
        }
    }

    public void save(T base) {
        Optional<JsonElement> jsonElement = codec.encodeStart(this.registryOps, base).resultOrPartial(msg -> LOGGER.warn("Failed to save: {}", msg));
        if (jsonElement.isPresent()) {
            try {
                Files.writeString(this.path, jsonElement.get().toString());
            } catch (Exception e) {
                LOGGER.error("Failed to write file: {}", e.getMessage());
            }
        }
    }

    public T load() {
        if (!Files.exists(this.path)) {
            return this.defaultValue.get();
        }

        try {
            var string  = Files.readString(this.path);
            JsonElement jsonElement = JsonParser.parseString(string);
            var decode = codec.decode(this.registryOps, jsonElement).resultOrPartial(msg -> LOGGER.warn("Failed to load: {}", msg));
            return decode.map(Pair::getFirst).orElseGet(this.defaultValue);
        } catch (IOException e) {
            LOGGER.error("Failed to read file: {}", e.getMessage());
            return this.defaultValue.get();
        }
    }
}
