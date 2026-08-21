package de.teamlapen.vampirism.api.world.entity.hunter;

import de.teamlapen.faction.api.world.entities.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

/**
 * Advanced vampire hunter
 */
public interface IAdvancedHunter extends IHunterMob, IAdjustableLevel, IVillageCaptureEntity {

    boolean isLookingForHome();

    void setCampArea(AABB box);

}
