package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class ModOilTags {
    public static final TagKey<IOil> NON_TREASURE = tag("non_treasure");

    private static @NotNull TagKey<IOil> tag(@NotNull String name) {
        return TagKey.create(VampirismRegistries.Keys.OIL, VResourceLocation.mod(name));
    }
}
