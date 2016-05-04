package de.teamlapen.vampirism.api.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * Extends {@link EntityCreature} home system with a way to set more accurate home positions
 * Implemented by EntityVampirism and thereby by most of Vampirism's.
 */
public interface IEntityWithHome {
    AxisAlignedBB getHome();

    /**
     * Sets the entity's home to the given bounding box
     *
     * @param home
     */
    void setHome(AxisAlignedBB home);

    /**
     * @return The center of the entity's home box or (0/0/0) if none exists
     */
    BlockPos getHomePosition();

    /**
     * Checks if the given position i within the entity's home area
     *
     * @param posX
     * @param posY
     * @param posZ
     * @return
     */
    boolean isWithinHomeDistance(int posX, int posY, int posZ);

    /**
     * Checks if the given position i within the entity's home area
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    boolean isWithinHomeDistance(double x, double y, double z);

    /**
     * Checks if the given position i within the entity's home area
     *
     * @param pos
     * @return
     */
    boolean isWithinHomeDistance(BlockPos pos);

    /**
     * Sets the entity's home bounding box to an area that extends r blocks in every direction from pos
     *
     * @param pos
     * @param r
     */
    void setHomeArea(BlockPos pos, int r);
}
