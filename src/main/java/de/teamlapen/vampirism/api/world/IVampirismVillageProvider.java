package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.world.villages.VampirismVillage;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides access to {@link IVampirismVillage} for one world
 */
public interface IVampirismVillageProvider {

    /**
     * @return The nearest village the entity is in or next to.
     */
    @Nullable
    VampirismVillage getNearestVillage(Entity e);

    /**
     * Finds the nearest village, but only the given coordinates are withing it's bounding box plus the given the distance.
     */
    @Nullable
    VampirismVillage getNearestVillage(BlockPos pos, int r);

    /**
     * Gets or create the VillageVampire to the given village. Can be null if no vampire version can exist
     *
     * @param v Should be in the world this collection belongs to
     * @return The Vampirism Village that belongs to the given one. Can be null.
     */
    @Nullable
    VampirismVillage getVampirismVillage(Village v);

    /**
     * FOR INTERNAL USE ONLY
     */
    interface IProviderProvider {
        /**
         * @return The provider that belongs to the given world
         */
        @Nonnull
        IVampirismVillageProvider getProviderForWorld(World world);
    }
}
