package de.teamlapen.vampirism.api.world.entity.hunter;

import de.teamlapen.faction.api.world.entities.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.faction.api.world.entities.ICustomizationHolder;
import net.minecraft.world.phys.AABB;

/**
 * Interface for basic hunter mob
 * Do not implement
 */
public interface IBasicHunter extends IHunterMob, IAdjustableLevel, IVillageCaptureEntity, ICustomizationHolder {
    int TYPES = 126;

    /**
     * @return A randomly selected but permanent integer between 0 and {@link IBasicHunter#TYPES} or -1 if not selected yet.
     */
    @Override
    int getEntityTextureType();

    boolean isLookingForHome();

    void makeNormalHunter();

    void makeVillageHunter(AABB box);

}
