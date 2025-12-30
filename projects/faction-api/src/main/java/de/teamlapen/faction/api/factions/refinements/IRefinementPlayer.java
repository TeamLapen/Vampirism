package de.teamlapen.faction.api.factions.refinements;


import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;

public interface IRefinementPlayer<T extends IFactionPlayer<T> & IRefinementPlayer<T>> extends IFactionPlayer<T> {

    static <T extends IRefinementPlayer<T>> Optional<T> get(Player player) {
        return FactionsApi.factionPlayerHandler(player).getCurrentRefinementPlayer();
    }

    IRefinementHandler<T> getRefinementHandler();
}
