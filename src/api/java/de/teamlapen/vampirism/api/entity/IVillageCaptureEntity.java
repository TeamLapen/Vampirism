package de.teamlapen.vampirism.api.entity;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import net.minecraft.util.math.BlockPos;


/**
 * Required for entities that are supposed to attack/defend villages during a capture progress
 */
public interface IVillageCaptureEntity extends IFactionEntity {
    void attackVillage(BlockPos pos);

    void defendVillage(BlockPos pos);

    void stopVillageAttackDefense();
}
