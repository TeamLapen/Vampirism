package de.teamlapen.vampirism.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Alchemist's fire which does not spread
 */
public class AlchemicalFireBlock extends VampirismBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 15);

    public AlchemicalFireBlock() {
        super(Properties.of(Material.FIRE, MaterialColor.FIRE).strength(0.0f).lightLevel(s -> 15).sound(SoundType.WOOL).noCollission().randomTicks().noOcclusion().noLootTable());
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(@Nonnull BlockState stateIn, @Nonnull Level worldIn, @Nonnull BlockPos pos, RandomSource rand) {
        if (rand.nextInt(24) == 0) {
            worldIn.playLocalSound((float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F, (float) pos.getZ() + 0.5F, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }


        for (int i = 0; i < 3; ++i) {
            double d0 = (double) pos.getX() + rand.nextDouble();
            double d1 = (double) pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
            double d2 = (double) pos.getZ() + rand.nextDouble();
            ParticleOptions type = i == 0 ? ParticleTypes.LARGE_SMOKE : i == 1 ? ParticleTypes.WITCH : rand.nextInt(10) == 0 ? ParticleTypes.FIREWORK : DustParticleOptions.REDSTONE;
            worldIn.addParticle(type, d0, d1, d2, 0.0D, i == 2 ? 0.1D : 0.0D, 0.0D);
        }
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean isBurning(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canSurvive(@Nonnull BlockState state, LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.below()).isFaceSturdy(worldIn, pos.below(), Direction.UP);
    }

    @Override
    public void entityInside(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, Entity entityIn) {
        if (!entityIn.fireImmune()) {
            entityIn.setRemainingFireTicks(entityIn.getRemainingFireTicks() + 1);
            if (entityIn.getRemainingFireTicks() == 0) {
                entityIn.setSecondsOnFire(8);
            }

            entityIn.hurt(DamageSource.IN_FIRE, 1);
        }

        super.entityInside(state, worldIn, pos, entityIn);
    }

    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
        if (!canSurvive(state, worldIn, pos)) {
            worldIn.removeBlock(pos, isMoving);
        }
    }

    /**
     * Marks the block to burn for an infinite time
     */
    public void setBurningInfinite(Level worldIn, BlockPos pos, BlockState state) {
        worldIn.setBlock(pos, state.setValue(AGE, 15), 4);
    }

    @Override
    public void tick(@Nonnull BlockState state, @Nonnull ServerLevel worldIn, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        if (!this.canSurvive(state, worldIn, pos)) {
            worldIn.removeBlock(pos, this.hasCollision);
        }


        int age = (state.getValue(AGE));


        if (age < 14) {
            state = state.setValue(AGE, age + 1);
            worldIn.setBlock(pos, state, 4);
        } else if (age == 14) {
            worldIn.removeBlock(pos, this.hasCollision);
        }
        worldIn.scheduleTick(pos, this, 30 + random.nextInt(10));
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }


}
