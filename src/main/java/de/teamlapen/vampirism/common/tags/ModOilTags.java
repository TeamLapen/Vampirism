package de.teamlapen.vampirism.common.tags;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.tags.TagKey;

public class ModOilTags {
    public static final TagKey<IOil> NON_TREASURE = tag("non_treasure");

    private static TagKey<IOil> tag(String name) {
        return TagKey.create(VampirismRegistries.Keys.OIL, VResourceLocation.mod(name));
    }
}
