package de.teamlapen.vampirism.world;

import de.teamlapen.vampirism.util.DaySleepHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

/**
 * Added to every world
 */
public class ModWorldEventListener implements IWorldEventListener {

    private final int dimension;

    public ModWorldEventListener(int dimension) {
        this.dimension = dimension;
    }

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data) {

    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {

    }

    @Override
    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {

    }

    @Override
    public void notifyLightSet(BlockPos pos) {

    }

    @Override
    public void onEntityAdded(Entity entityIn) {
        if (entityIn instanceof EntityPlayer) DaySleepHelper.updateAllPlayersSleeping(entityIn.worldObj);
    }

    @Override
    public void onEntityRemoved(Entity entityIn) {
        if (entityIn instanceof EntityPlayer) DaySleepHelper.updateAllPlayersSleeping(entityIn.worldObj);
    }

    @Override
    public void playAuxSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int data) {

    }

    @Override
    public void playRecord(SoundEvent soundIn, BlockPos pos) {

    }

    @Override
    public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {

    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {

    }

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... parameters) {

    }
}
