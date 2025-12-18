package de.teamlapen.factions.api.factions;

import de.teamlapen.factions.api.FactionsApi;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface IFactionRegistry {

    static IFactionRegistry get() {
        return FactionsApi.services().factionRegistry();
    }

    Holder<? extends IFaction<?>> getFaction(Entity entity);

    Holder<? extends IPlayableFaction<?>> getFaction(Player entity);

    boolean isEntityOfFaction(Entity entity, Holder<? extends IFaction<?>> faction);

}
