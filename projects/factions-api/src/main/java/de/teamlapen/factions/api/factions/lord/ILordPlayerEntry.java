package de.teamlapen.factions.api.factions.lord;

/**
 * lord entry for faction builder {@link  ILordPlayerBuilder}
 */
public interface ILordPlayerEntry {

    /**
     * Maximum level a lord can reach
     */
    int maxLevel();

    /**
     * Lord title provider
     */
    ILordTitleProvider lordTitleFunction();
}
