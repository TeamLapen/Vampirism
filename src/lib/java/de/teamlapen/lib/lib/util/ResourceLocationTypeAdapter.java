package de.teamlapen.lib.lib.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public final class ResourceLocationTypeAdapter extends TypeAdapter<ResourceLocation> {

    @Override
    public @NotNull ResourceLocation read(@NotNull JsonReader in) throws IOException {
        return new ResourceLocation(in.nextString());
    }

    @Override
    public void write(@NotNull JsonWriter out, @Nullable ResourceLocation value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.toString());
    }
}
