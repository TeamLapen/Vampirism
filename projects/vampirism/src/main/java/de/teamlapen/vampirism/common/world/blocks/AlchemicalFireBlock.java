package de.teamlapen.vampirism.common.world.blocks;

import de.teamlapen.vampirism.common.util.DamageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Alchemist's fire which does not spread
 */
public class AlchemicalFireBlock extends Block {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 15);
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;

    public AlchemicalFireBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(24) == 0) {
            level.playLocalSound((float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F, (float) pos.getZ() + 0.5F, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
        }

        for (int i = 0; i < 3; ++i) {
            double d0 = (double) pos.getX() + random.nextDouble();
            double d1 = (double) pos.getY() + random.nextDouble() * 0.5D + 0.5D;
            double d2 = (double) pos.getZ() + random.nextDouble();
            ParticleOptions type = i == 0 ? ParticleTypes.LARGE_SMOKE : i == 1 ? ParticleTypes.WITCH : random.nextInt(10) == 0 ? ParticleTypes.FIREWORK : DustParticleOptions.REDSTONE;
            level.addParticle(type, d0, d1, d2, 0.0D, i == 2 ? 0.1D : 0.0D, 0.0D);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean isBurning(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean intersects) {
        if (!entity.fireImmune()) {
            entity.igniteForSeconds(entity.getRemainingFireTicks() + 1);
            if (entity.getRemainingFireTicks() == 0) {
                entity.igniteForSeconds(8);
            }

            if (level instanceof ServerLevel serverLevel) {
                DamageHandler.hurtVanilla(serverLevel, entity, DamageSources::inFire, 1);
            }
        }

        super.entityInside(state, level, pos, entity, applier, intersects);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
        if (!canSurvive(state, level, pos)) {
            level.removeBlock(pos, false);
        }
    }

    /**
     * Marks the block to burn for an infinite time
     */
    public void setBurningInfinite(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(AGE, 15), 4);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!this.canSurvive(state, level, pos)) {
            level.removeBlock(pos, this.hasCollision);
        }

        int age = (state.getValue(AGE));

        if (age < 14) {
            state = state.setValue(AGE, age + 1);
            level.setBlock(pos, state, 4);
        } else if (age == 14) {
            level.removeBlock(pos, this.hasCollision);
        }
        level.scheduleTick(pos, this, 30 + random.nextInt(10));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE);
        builder.add(NORTH, EAST, SOUTH, WEST, UP);
    }
}
