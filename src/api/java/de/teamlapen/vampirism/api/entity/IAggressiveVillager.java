package de.teamlapen.vampirism.api.entity;

import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;

/**
 * Interface for villagers that are (can be) agressive and hunt vampires.
 * E.g. used by MCA integration to allow MCAVillagers to be agressive villagers
 * <p>
 * May only be implemented by subclasses of EntityVillager
 *
 * Should replace itself to a calm entity after the capture has been stopped
 */
public interface IAggressiveVillager extends IHunterMob, IVillageCaptureEntity {



}
