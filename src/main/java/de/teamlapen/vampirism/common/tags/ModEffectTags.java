package de.teamlapen.vampirism.common.tags;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

public class ModEffectTags {
    public static final TagKey<MobEffect> HUNTER_POTION_RESISTANCE = tag("hunter_potion_resistance");

    private static TagKey<MobEffect> tag(String name) {
        return TagKey.create(Registries.MOB_EFFECT, VResourceLocation.mc(name));
    }
}
