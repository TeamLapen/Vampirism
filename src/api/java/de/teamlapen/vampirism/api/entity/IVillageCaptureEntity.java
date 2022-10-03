package de.teamlapen.vampirism.api.entity;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.world.ICaptureAttributes;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;


/**
 * Required for entities that are supposed to attack/defend villages during a capture progress
 */
public interface IVillageCaptureEntity extends IFactionEntity {

    void attackVillage(ICaptureAttributes totem);

    void defendVillage(ICaptureAttributes totem);

    /**
     * @return A (cached) instance of the village the entity is currently in if it is of the same faction or null otherwise
     */
    @Nullable
    ICaptureAttributes getCaptureInfo();

    /**
     * @return The village area that is target of the capture
     */
    @Nullable
    AABB getTargetVillageArea();

    /**
     * if this village is
     */
    boolean isAttackingVillage();

    boolean isDefendingVillage();

    /**
     * Called when the entity is within a village where a capture progress has been stopped.
     * {@link #attackVillage(ICaptureAttributes)} or {@link #defendVillage(ICaptureAttributes)} may not have been called before
     */
    void stopVillageAttackDefense();

}
