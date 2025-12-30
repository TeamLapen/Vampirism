package de.teamlapen.faction.common.tags;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

public class FactionEffectTags {
    public static final TagKey<MobEffect> DISABLES_ACTIONS = tag("disables_actions");

    private static TagKey<MobEffect> tag(String name) {
        return TagKey.create(Registries.MOB_EFFECT, FIdentifier.mod(name));
    }
}
