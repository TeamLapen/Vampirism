package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantmentTags {
    public static final TagKey<Enchantment> CROSSBOW_INCOMPATIBLE = tag("crossbow_incompatible");
    public static final TagKey<Enchantment> HUNTER_CROSSBOW_COMPATIBLE = tag("hunter_crossbow_compatible");
    public static final TagKey<Enchantment> SINGLE_HUNTER_CROSSBOW_COMPATIBLE = tag("hunter_crossbow_compatible/single");
    public static final TagKey<Enchantment> DOUBLE_HUNTER_CROSSBOW_COMPATIBLE = tag("hunter_crossbow_compatible/double");
    public static final TagKey<Enchantment> SEMI_AUTOMATIC_HUNTER_CROSSBOW_COMPATIBLE = tag("hunter_crossbow_compatible/semi");

    private static TagKey<Enchantment> tag(String name) {
        return TagKey.create(Registries.ENCHANTMENT, VResourceLocation.mod(name));
    }
}
