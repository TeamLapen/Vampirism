package de.teamlapen.faction.api;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;

public class FactionTagKeys {
    /**
     * Entity types that are targeted by entities of the faction regardless of the target's faction.
     *
     * @see de.teamlapen.faction.api.factions.IFactionPredicate.Builder#defaultTargets()
     * @see de.teamlapen.faction.api.event.AddFactionTagEvent.Builder#defaultTargets(net.minecraft.tags.TagKey)
     */
    public static final ResourceKey<EntityType<?>> DEFAULT_TARGETS = ResourceKey.create(Registries.ENTITY_TYPE, FIdentifier.mod("default_targets"));
    public static final ResourceKey<MobEffect> ACTION_DISABLES = ResourceKey.create(Registries.MOB_EFFECT, FIdentifier.mod("action_disable"));
}
