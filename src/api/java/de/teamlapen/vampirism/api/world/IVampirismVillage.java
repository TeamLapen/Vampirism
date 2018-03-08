package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interfaces for Vampirism counterpart to vanilla Village class
 */
public interface IVampirismVillage {
    /**
     * Finds the nearest aggressor to the given entity
     */
    @Nullable
    IVampire findNearestVillageAggressor(@Nonnull EntityLivingBase entityCenter);

    AxisAlignedBB getBoundingBox();

    BlockPos getCenter();

    Village getVillage();


    /**
     * Call this if a villager in this village has been bitten
     *
     * @param vampire The biter
     */
    void onVillagerBitten(IVampire vampire);

    /**
     * Call this if a villager in this village has been killed by a bite
     *
     * @param vampire The biter
     */
    void onVillagerBittenToDeath(IVampire vampire);

    /**
     * Call this if a villager in this village is converted by a vampire
     *
     * @param vampire The biter or null if unknown
     */
    void onVillagerConverted(@Nullable IVampire vampire);

    /**
     * @return If the village is overtaken by vampires
     */
    boolean isOvertaken();

    /**
     * Set if the village is overtaken by vampires
     */
    void setIsOvertaken(boolean overtaken);
}
