package de.teamlapen.vampirism.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCandelabraBlock extends VampirismHorizontalBlock {

    public AbstractCandelabraBlock(@NotNull Properties properties, VoxelShape shape) {
        super(properties, shape);
    }

    protected abstract Iterable<Vec3> getParticleOffsets(BlockState pState);

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        this.getParticleOffsets(pState)
                .forEach(
                        p_220695_ -> addParticlesAndSound(
                                pLevel, p_220695_.add((double) pPos.getX(), (double) pPos.getY(), (double) pPos.getZ()), pRandom
                        )
                );
    }

    private static void addParticlesAndSound(Level pLevel, Vec3 pOffset, RandomSource pRandom) {
        float f = pRandom.nextFloat();
        if (f < 0.3F) {
            pLevel.addParticle(ParticleTypes.SMOKE, pOffset.x, pOffset.y, pOffset.z, 0.0, 0.0, 0.0);
            if (f < 0.17F) {
                pLevel.playLocalSound(
                        pOffset.x + 0.5,
                        pOffset.y + 0.5,
                        pOffset.z + 0.5,
                        SoundEvents.CANDLE_AMBIENT,
                        SoundSource.BLOCKS,
                        1.0F + pRandom.nextFloat(),
                        pRandom.nextFloat() * 0.7F + 0.3F,
                        false
                );
            }
        }

        pLevel.addParticle(ParticleTypes.SMALL_FLAME, pOffset.x, pOffset.y, pOffset.z, 0.0, 0.0, 0.0);
    }
}
