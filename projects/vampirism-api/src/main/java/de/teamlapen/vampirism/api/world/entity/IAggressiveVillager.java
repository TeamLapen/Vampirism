package de.teamlapen.vampirism.api.world.entity;

import de.teamlapen.faction.api.world.entities.ICaptureStrengthProvider;
import de.teamlapen.faction.api.world.entities.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.world.entity.hunter.IHunterMob;

/**
 * Interface for villagers that are (can be) aggressive and hunt vampires.
 * E.g. used by MCA integration to allow MCAVillagers to be aggressive villagers
 * <p>
 * May only be implemented by subclasses of EntityVillager
 * <p>
 * Should replace itself to a calm entity after the capture has been stopped
 */
public interface IAggressiveVillager extends IHunterMob, IVillageCaptureEntity, ICaptureStrengthProvider {

    @Override
    default float getCaptureStrength() {
        return 0.7f;
    }
}
