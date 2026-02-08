package de.teamlapen.vampirism.misc.sit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * An optional interface for blocks that use the sit entity.
 * Allows customizing sitting behaviour such as sitting direction, player rotation limitations, and stand-up position.
 * If not implemented, default values will be used.
 */
public interface ISittableBlock {

    float DEFAULT_MAX_SIT_ROTATION_ANGLE = 120.0f;

    /**
     * Determines the position where the entity will appear after standing up from this sittable block.
     * Called when the entity stands up.
     * <p>
     * By default, this is usually on top of the block.
     *
     * @param level The level of the sittable block.
     * @param pos The position of the sittable block.
     * @param entity The entity standing up (usually a player).
     * @param facing The direction the block is facing.
     * @return The position where the entity should dismount.
     */
    Vec3 getStandUpLocation(Level level, BlockPos pos, Entity entity, Direction facing);

    /**
     * Returns the rotation angle (in degrees) that the sit entity should face when
     * the entity starts sitting on the block.
     *
     * @param state The current state of the block.
     * @param sitEntity The SitEntity being created.
     * @param player The player starting sitting.
     * @return The yaw rotation angle the sit entity should face.
     */
    float getSitRotation(BlockState state, SitEntity sitEntity, Player player);

    /**
     * Defines how far the sitting entity can turn their head left or right from their sitting direction.
     * Used when the sit entity is created, this method is not used dynamically.
     *
     * @param state The current state of the block.
     * @param sitEntity The SitEntity being created.
     * @param player The player starting sitting.
     * @return The maximum allowed head rotation angle while sitting.
     */
    default float getMaxSitRotationAngle(BlockState state, SitEntity sitEntity, Player player) {
        return DEFAULT_MAX_SIT_ROTATION_ANGLE;
    }

    /**
     * Determines whether the sitting entity’s body and head rotation
     * should be clamped (locked) to the sitting direction.
     * <p>
     * If {@code true}, the player cannot freely rotate their body while sitting,
     * or else the player can rotate both their head and their body.
     *
     * @param state The current state of the block.
     * @param sitEntity The SitEntity being created.
     * @param player The player starting sitting.
     * @return {@code true} if sitting rotation should be locked; {@code false} otherwise.
     */
    default boolean shouldLockSittingPlayerRotation(BlockState state, SitEntity sitEntity, Player player) {
        return true;
    }
}
