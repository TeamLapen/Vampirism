package de.teamlapen.vampirism.api.entity;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;

/**
 * Implemented by all vampire entities
 */
public interface IVampire extends IFactionEntity {
    /**
     * Checks if all requirements are met for the entity to be damaged by the sun, e.g. standing in the sun and not raining.
     * The result is cached for a few ticks unless you use forcerefresh
     * @param forcerefresh
     * @return
     */
    boolean isGettingSundamage(boolean forcerefresh);
    /**
     * Checks if all requirements are met for the entity to be damaged by the sun, e.g. standing in the sun and not raining.
     * The result is cached for a few ticks.
     * Recommend implementation: Just call isGettingSundamage(false)
     * @return
     */
    boolean isGettingSundamge();
}
