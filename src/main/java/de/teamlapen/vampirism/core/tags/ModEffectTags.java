package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

public class ModEffectTags {
    public static final TagKey<MobEffect> HUNTER_POTION_RESISTANCE = tag("hunter_potion_resistance");
    public static final TagKey<MobEffect> DISABLES_ACTIONS = tag("disables_actions");
    public static final TagKey<MobEffect> DISABLES_ACTIONS_HUNTER = tag("disables_actions/hunter");
    public static final TagKey<MobEffect> DISABLES_ACTIONS_VAMPIRE = tag("disables_actions/vampire");

    private static @NotNull TagKey<MobEffect> tag(@NotNull String name) {
        return TagKey.create(Registries.MOB_EFFECT, VResourceLocation.mc(name));
    }

}
