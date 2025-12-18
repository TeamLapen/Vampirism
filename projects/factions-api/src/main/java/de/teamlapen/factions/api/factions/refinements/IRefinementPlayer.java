package de.teamlapen.factions.api.factions.refinements;


import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;

public interface IRefinementPlayer<T extends IFactionPlayer<T> & IRefinementPlayer<T>> extends IFactionPlayer<T> {

    IRefinementHandler<T> getRefinementHandler();
}
