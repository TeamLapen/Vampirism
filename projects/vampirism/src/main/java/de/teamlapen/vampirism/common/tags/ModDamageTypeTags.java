package de.teamlapen.vampirism.common.tags;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypeTags {
    public static final TagKey<DamageType> ENTITY_PHYSICAL = tag("entity_physical");
    public static final TagKey<DamageType> REMAINS_INVULNERABLE = tag("remains_invulnerable");
    public static final TagKey<DamageType> MOTHER_RESISTANT_TO = tag("mother_resistant_to");
    public static final TagKey<DamageType> VAMPIRE_IMMORTAL = tag("vampire_immortal");

    private static TagKey<DamageType> tag(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, VResourceLocation.mod(name));
    }
}
