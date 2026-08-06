package de.teamlapen.faction.client.config.preferences;

import com.mojang.serialization.Codec;
import de.teamlapen.files.FileSerializer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.Unmodifiable;

import java.nio.file.Path;
import java.util.function.Supplier;

public abstract class PreferenceValue<T> {

    private final FileSerializer<T> serializer;
    private T value;

    public PreferenceValue(Identifier key, Codec<T> codec, RegistryAccess registryAccess, Supplier<T> defaultValue) {
        var path = FMLPaths.CONFIGDIR.get().resolve(key.getNamespace()).resolve(key.getPath() + ".json");
        this.serializer = new FileSerializer<>(path, registryAccess, codec, defaultValue);
        this.serializer.initialize();
        this.value = this.serializer.load();
        if (checkValues(this.value, registryAccess)) {
            save();
        }
    }

    protected boolean checkValues(T value, RegistryAccess registryAccess) {
        return false;
    }

    protected T getValue(){
        return this.value;
    }

    protected void setValue(@Unmodifiable T value) {
        this.value = value;
    }

    protected void load() {
        this.value = this.serializer.load();
    }

    protected void save() {
        this.serializer.save(this.value);
    }

}
