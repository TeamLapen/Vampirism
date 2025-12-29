package de.teamlapen.factions.api.factions.lord;


import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;

public interface ILordPlayerBuilder<T extends IFactionPlayer<T>> {

    /**
     * Sets the maximum lord level for player of this faction<br>
     * If set to 0 the player can not become lord
     *
     * @param highestLordLevel the highest possible lord level for players,  {@code highestLordLevel >= 0}
     * @return the builder
     */
    ILordPlayerBuilder<T> lordLevel(int highestLordLevel);


    /**
     * Sets custom lord titles
     * <br>
     * Only relevant when lord level > 0
     *
     * @param lordTitleFunction an object that provides the title for a lord player
     * @return the builder
     */
    ILordPlayerBuilder<T> lordTitle(ILordTitleProvider lordTitleFunction);

    ILordPlayerEntry build();


}
