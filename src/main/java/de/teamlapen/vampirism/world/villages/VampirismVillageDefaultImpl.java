package de.teamlapen.vampirism.world.villages;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * DO NOT USE.
 * Default implementation for vampirism village
 */
@Deprecated
class VampirismVillageDefaultImpl implements IVampirismVillage {
    @Nullable
    @Override
    public IVampire findNearestVillageAggressor(@Nonnull EntityLivingBase entityCenter) {
        return null;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public BlockPos getCenter() {
        return null;
    }

    @Override
    public Village getVillage() {
        return null;
    }

    @Override
    public void onVillagerBitten(IVampire vampire) {

    }

    @Override
    public void onVillagerBittenToDeath(IVampire vampire) {

    }

    @Override
    public void onVillagerConverted(@Nullable IVampire vampire) {

    }
}
