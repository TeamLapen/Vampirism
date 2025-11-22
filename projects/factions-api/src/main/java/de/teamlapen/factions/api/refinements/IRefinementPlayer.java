package de.teamlapen.factions.api.refinements;


import de.teamlapen.factions.api.entities.player.IFactionPlayer;

public interface IRefinementPlayer<T extends IFactionPlayer<T> & IRefinementPlayer<T>> extends IFactionPlayer<T> {

    IRefinementHandler<T> getRefinementHandler();
}
