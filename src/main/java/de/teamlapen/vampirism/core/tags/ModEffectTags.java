package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

public class ModEffectTags {
    public static final TagKey<MobEffect> HUNTER_POTION_RESISTANCE = tag("hunter_potion_resistance");

    private static @NotNull TagKey<MobEffect> tag(@NotNull String name) {
        return TagKey.create(Registries.MOB_EFFECT, new ResourceLocation(REFERENCE.MODID, name));
    }

}
