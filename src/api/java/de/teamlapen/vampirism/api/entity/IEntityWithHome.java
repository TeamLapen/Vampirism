package de.teamlapen.vampirism.api.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * Extends {@link CreatureEntity} home system with a way to set more accurate home positions
 * Implemented by VampirismEntity and thereby by most of Vampirism's.
 */
public interface IEntityWithHome {
    @Nullable
    AxisAlignedBB getHome();

    /**
     * Sets the entity's home to the given bounding box.
     *
     * @param home Can be null to unset the home
     */
    void setHome(@Nullable AxisAlignedBB home);

    /**
     * @return The center of the entity's home box or (0/0/0) if none exists
     */
    BlockPos getHomePosition();


    /**
     * Checks if the given position i within the entity's home area
     */
    boolean isWithinHomeDistance(double x, double y, double z);

    /**
     * Checks if the given position i within the entity's home area
     */
    default boolean isWithinHomeDistance(BlockPos pos) {
        return this.isWithinHomeDistance(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Sets the entity's home bounding box to an area that extends r blocks in every direction from pos
     *
     * @param pos
     * @param r
     */
    void setHomeArea(BlockPos pos, int r);
}
