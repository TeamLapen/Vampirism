package de.teamlapen.factions.common.factions;

import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IFactionEntity;
import de.teamlapen.factions.api.factions.IFactionRegistry;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.util.SafeCast;
import de.teamlapen.factions.common.core.DefaultFactions;
import de.teamlapen.factions.common.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;


public class FactionRegistry implements IFactionRegistry {
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
        return ModRegistries.FACTIONS.listElements().map(s -> (Holder<IFaction<?>>)s).filter(s -> s.value().getTag(Registries.ENTITY_TYPE).flatMap(BuiltInRegistries.ENTITY_TYPE::get).filter(tag -> entity.getType().is(tag)).isPresent()).findFirst().orElseGet(() -> SafeCast.cast(DefaultFactions.NEUTRAL));
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
