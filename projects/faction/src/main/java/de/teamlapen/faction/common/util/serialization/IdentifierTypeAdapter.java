package de.teamlapen.faction.common.util.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public final class IdentifierTypeAdapter extends TypeAdapter<Identifier> {

    @Override
    public @NotNull Identifier read(@NotNull JsonReader in) throws IOException {
        return Identifier.parse(in.nextString());
    }

    @Override
    public void write(@NotNull JsonWriter out, @Nullable Identifier value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.toString());
    }
}
