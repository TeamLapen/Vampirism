package de.teamlapen.faction.api;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;

public class FactionTagKeys {
    public static final ResourceKey<MobEffect> ACTION_DISABLES = ResourceKey.create(Registries.MOB_EFFECT, FIdentifier.mod("action_disable"));
}
