package de.teamlapen.vampirism.world;

import de.teamlapen.vampirism.util.DaySleepHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.dimension.Dimension;

/**
 * Added to every world
 */
public class ModWorldEventListener implements IWorldEventListener {

    private final Dimension dimension;

    public ModWorldEventListener(Dimension dimension) {
        this.dimension = dimension;
    }

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data) {

    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {

    }

    @Override
    public void notifyBlockUpdate(IBlockReader worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {

    }

    @Override
    public void notifyLightSet(BlockPos pos) {

    }

    @Override
    public void onEntityAdded(Entity entityIn) {
        if (entityIn instanceof EntityPlayer) DaySleepHelper.updateAllPlayersSleeping(entityIn.getEntityWorld());
    }

    @Override
    public void onEntityRemoved(Entity entityIn) {
        if (entityIn instanceof EntityPlayer) DaySleepHelper.updateAllPlayersSleeping(entityIn.getEntityWorld());
    }

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {

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
    public void addParticle(IParticleData particleData, boolean alwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {

    }

    @Override
    public void addParticle(IParticleData particleData, boolean ignoreRange, boolean minimizeLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {

    }
}
