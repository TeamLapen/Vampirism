package de.teamlapen.vampirism.api.entity.factions;

/**
 * Should be implemented (through the subclasses) by any faction entity
 */
public interface IFactionEntity {
    /**
     * Return the faction this entity belongs to
     *
     * @return
     */
    Faction getFaction();
}
