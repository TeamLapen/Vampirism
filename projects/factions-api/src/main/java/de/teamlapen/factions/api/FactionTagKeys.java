package de.teamlapen.factions.api;

import de.teamlapen.factions.api.util.FResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;

public class FactionTagKeys {
    public static final ResourceKey<MobEffect> ACTION_DISABLES = ResourceKey.create(Registries.MOB_EFFECT, FResourceLocation.mod("action_disable"));
}
