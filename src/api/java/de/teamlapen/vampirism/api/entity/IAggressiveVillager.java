package de.teamlapen.vampirism.api.entity;

import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import net.minecraft.entity.Entity;

/**
 * Interface for villagers that are (can be) agressive and hunt vampires.
 * E.g. used by MCA integration to allow MCAVillagers to be agressive villagers
 * <p>
 * May only be implemented by subclasses of EntityVillager
 */
public interface IAggressiveVillager extends IHunterMob {

    /**
     * Calm down this villager.
     *
     * @return A new calmed-down entity
     */
    Entity makeCalm();


}
