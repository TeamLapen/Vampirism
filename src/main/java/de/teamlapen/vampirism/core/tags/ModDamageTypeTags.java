package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.NotNull;

public class ModDamageTypeTags {

    public static final TagKey<DamageType> ENTITY_PHYSICAL = tag("entity_physical");
    public static final TagKey<DamageType> REMAINS_INVULNERABLE = tag("remains_invulnerable");
    public static final TagKey<DamageType> MOTHER_RESISTANT_TO = tag("mother_resistant_to");
    public static final TagKey<DamageType> VAMPIRE_IMMORTAL = tag("vampire_immortal");

    private static @NotNull TagKey<DamageType> tag(@NotNull String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(REFERENCE.MODID, name));
    }
}
