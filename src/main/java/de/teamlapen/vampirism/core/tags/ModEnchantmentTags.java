package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public class ModEnchantmentTags {
    public static final TagKey<Enchantment> CROSSBOW_INCOMPATIBLE = tag("crossbow_incompatible");

    private static @NotNull TagKey<Enchantment> tag(@NotNull String name) {
        return TagKey.create(Registries.ENCHANTMENT, VResourceLocation.mod(name));
    }

}
