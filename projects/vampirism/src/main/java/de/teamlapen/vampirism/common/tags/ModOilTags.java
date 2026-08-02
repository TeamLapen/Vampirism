package de.teamlapen.vampirism.common.tags;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.items.oil.IOil;
import net.minecraft.tags.TagKey;

public class ModOilTags {
    public static final TagKey<IOil> NON_TREASURE = tag("non_treasure");
    public static final TagKey<IOil> STRONG = tag("strong");

    private static TagKey<IOil> tag(String name) {
        return TagKey.create(VampirismRegistries.Keys.OIL, VIdentifier.mod(name));
    }
}
