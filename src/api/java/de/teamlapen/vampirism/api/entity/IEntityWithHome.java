package de.teamlapen.vampirism.api.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Extends {@link net.minecraft.world.entity.PathfinderMob} home system with a way to set more accurate home positions
 * Implemented by VampirismEntity and thereby by most of Vampirism's.
 */
public interface IEntityWithHome {
    @Nullable
    AABB getHome();

    /**
     * Sets the entity's home to the given bounding box.
     *
     * @param home Can be null to unset the home
     */
    void setHome(@Nullable AABB home);

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
    default boolean isWithinHomeDistance(@NotNull BlockPos pos) {
        return this.isWithinHomeDistance(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Sets the entity's home bounding box to an area that extends r blocks in every direction from pos
     */
    void setHomeArea(BlockPos pos, int r);
}
