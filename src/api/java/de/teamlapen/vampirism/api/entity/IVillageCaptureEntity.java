package de.teamlapen.vampirism.api.entity;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.world.IVillageAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Optional;


/**
 * Required for entities that are supposed to attack/defend villages during a capture progress
 */
public interface IVillageCaptureEntity extends IFactionEntity {
    default void attackVillage(BlockPos totem){
        setAttacking(true);
        setTotemPos(totem);
        attack(totem);
    }

    default void defendVillage(BlockPos totem){
        setAttacking(false);
        setTotemPos(totem);
        defend(totem);
    }

    default boolean isAttackingVillage(){
        return getAttacking() && getVillageAttributes().isPresent();
    }

    default boolean isDefendingVillage(){
        return !getAttacking() && getVillageAttributes().isPresent();

    }

    default void attack(BlockPos totem){
    }

    default void defend(BlockPos totem){
    }

    boolean getAttacking();

    void setAttacking(boolean attack);

    void setTotemPos(BlockPos pos);

    /**
     * Called when the entity is within a village whre a capture progress has been stopped.
     * {@link #attackVillage(BlockPos)} or {@link #defendVillage(BlockPos)} may not have been called before
     */
    default void stopVillageAttackDefense(){
        setTotemPos(null);
        getRepresentingEntity().setCustomName(null);
    }

    /**
     * @return A (cached) instance of the village the entity is currently in if it is of the same faction or null otherwise
     */
    @Nonnull
    Optional<? extends IVillageAttributes> getVillageAttributes();

}
