package de.teamlapen.vampirism.api.entity;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.world.IVillageAttributes;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;


/**
 * Required for entities that are supposed to attack/defend villages during a capture progress
 */
public interface IVillageCaptureEntity extends IFactionEntity {
    void attackVillage(IVillageAttributes totem);

    void defendVillage(IVillageAttributes totem);

    /**
     * @return The village area that is target of the capture
     */
    @Nullable
    AxisAlignedBB getTargetVillageArea();

    boolean isAttackingVillage();

    boolean isDefendingVillage();

    /**
     * Called when the entity is within a village whre a capture progress has been stopped.
     * {@link #attackVillage(IVillageAttributes)} or {@link #defendVillage(IVillageAttributes)} may not have been called before
     */
    void stopVillageAttackDefense();

    /**
     * @return A (cached) instance of the village the entity is currently in if it is of the same faction or null otherwise
     */
    @Nullable
    IVillageAttributes getVillageAttributes();

}
