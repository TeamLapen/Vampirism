package de.teamlapen.vampirism.api.world.entity.hunter;

import de.teamlapen.faction.api.world.entities.IVillageCaptureEntity;
import de.teamlapen.library.api.difficulty.IAdjustableLevel;
import net.minecraft.world.phys.AABB;

/**
 * Advanced vampire hunter
 */
public interface IAdvancedHunter extends IHunterMob, IAdjustableLevel, IVillageCaptureEntity {

    boolean isLookingForHome();

    void setCampArea(AABB box);

}
