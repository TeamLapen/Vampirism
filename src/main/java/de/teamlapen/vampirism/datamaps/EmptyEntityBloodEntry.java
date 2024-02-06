package de.teamlapen.vampirism.datamaps;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.datamaps.IEntityBloodEntry;
import org.jetbrains.annotations.Nullable;

public class EmptyEntityBloodEntry implements IEntityBloodEntry {
    public static final EmptyEntityBloodEntry INSTANCE = new EmptyEntityBloodEntry();
    public static final Codec<EmptyEntityBloodEntry> CODEC = Codec.unit(INSTANCE);

    @Override
    public int blood() {
        return -1;
    }

    @Override
    public @Nullable IConverterEntry converter() {
        return null;
    }

    @Override
    public boolean canBeConverted() {
        return false;
    }
}
