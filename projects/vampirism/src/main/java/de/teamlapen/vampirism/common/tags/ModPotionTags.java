package de.teamlapen.vampirism.common.tags;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.alchemy.Potion;

public class ModPotionTags {
    public static final TagKey<Potion> SERUM_BLOCKED = tag("serum_blocked");

    private static TagKey<Potion> tag(String name) {
        return TagKey.create(Registries.POTION, VIdentifier.mc(name));
    }
}
