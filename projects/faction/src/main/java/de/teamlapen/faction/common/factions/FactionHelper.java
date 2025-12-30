package de.teamlapen.faction.common.factions;

import de.teamlapen.faction.api.factions.*;
import de.teamlapen.faction.common.core.DefaultFactions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import java.util.Map;


public class FactionHelper implements IFactionHelper {
    @Override
    public Holder<? extends IFaction<?>> getFaction(Entity entity) {
        if (entity instanceof Player player) {
            return getFaction(player);
        } else if (entity instanceof IFactionEntity factionEntity) {
            return factionEntity.getFaction();
        }
        return getFallbackFaction(entity);
    }

    public Holder<? extends IFaction<?>> getFallbackFaction(Entity entity) {
        return getFallbackFaction(entity.getType());
    }

    public Holder<? extends IFaction<?>> getFallbackFaction(EntityType<?> entity) {
        Map<Holder<? extends IFaction<?>>, TagKey<EntityType<?>>> all = IFactionTags.get().all(Registries.ENTITY_TYPE);
        for (var entry : all.entrySet()) {
            if (entity.is(entry.getValue())) {
                return entry.getKey();
            }
        }
        return DefaultFactions.NEUTRAL;
    }

    @Override
    public Holder<? extends IPlayableFaction<?>> getFaction(Player player) {
        return FactionPlayerHandler.get(player).getFaction();
    }

    @Override
    public boolean isEntityOfFaction(Entity entity, Holder<? extends IFaction<?>> faction) {
        return IFaction.is(getFaction(entity), faction);
    }
}
