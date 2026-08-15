package de.teamlapen.vampirism.api.world.entity.vampire;

import de.teamlapen.faction.api.world.entities.IEntityLeader;
import de.teamlapen.faction.api.world.entities.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Advanced vampire
 */
public interface IAdvancedVampire extends IVampireMob, IAdjustableLevel, IVillageCaptureEntity, IEntityLeader {

}
