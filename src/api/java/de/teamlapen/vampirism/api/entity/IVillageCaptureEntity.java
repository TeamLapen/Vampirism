package de.teamlapen.vampirism.api.entity;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;


/**
 * Required for entities that are supposed to attack/defend villages during a capture progress
 */
public interface IVillageCaptureEntity extends IFactionEntity {
    void attackVillage(AxisAlignedBB area);

    void defendVillage(AxisAlignedBB area);

    /**
     * @return The village area that is target of the capture
     */
    @Nullable
    AxisAlignedBB getTargetVillageArea();

    boolean isAttackingVillage();

    /**
     * Called when the entity is within a village whre a capture progress has been stopped.
     * {@link #attackVillage(AxisAlignedBB)} or {@link #defendVillage(AxisAlignedBB)} may not have been called before
     */
    void stopVillageAttackDefense();

//    /**
//     * @return A (cached) instance of the village the entity is currently in if it is of the same faction or null otherwise
//     */
//    @Nullable
//    IVampirismVillage getCurrentFriendlyVillage();

}
