package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CursedBarkBlock extends Block {

    private static final VoxelShape shape = Shapes.empty();
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final DirectionProperty FACING2 = DirectionProperty.create("facing_2", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;


    public CursedBarkBlock() {
        super(BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT, MaterialColor.NONE).noCollission().strength(0.0F).sound(SoundType.VINE));
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(FACING2, Direction.NORTH).setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (Helper.isVampire(entity) || (entity instanceof Player && ((Player) entity).getAbilities().invulnerable)) return;
        Direction mainDirection = state.getValue(FACING);
        Direction secondaryDirection = state.getValue(FACING2);
        BlockPos targetPos = pos.relative(mainDirection);
        if (mainDirection != secondaryDirection) {
            targetPos = targetPos.relative(secondaryDirection);
        }

        Vec3 thrust = new Vec3(targetPos.getX(), targetPos.getY(), targetPos.getZ()).subtract(pos.getX(), pos.getY(), pos.getZ()).normalize().scale(0.04);
        if (!entity.isOnGround()) {
            thrust = thrust.scale(0.3d);
        }
        entity.setDeltaMovement(entity.getDeltaMovement().add(thrust));

        if (!level.isClientSide) {
            if (entity instanceof Player) {
                VampirePlayer.getOpt(((Player) entity)).ifPresent(vampire -> {
                    if (vampire.getRemainingBarkTicks() == 0) {
                        vampire.removeBlood(0.02f);
                        vampire.increaseRemainingBarkTicks(40);
                    }
                });
            } else {
                ExtendedCreature.getSafe(entity).ifPresent(creature -> {
                    if (((ExtendedCreature) creature).getRemainingBarkTicks() == 0) {
                        creature.setBlood(creature.getBlood() - 1);
                        ((ExtendedCreature) creature).increaseRemainingBarkTicks(40);
                    }

                });
            }
        }
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter blockReader, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return shape;
    }


    private boolean canAttachTo(@NotNull BlockGetter blockReader, @NotNull BlockPos pos, @NotNull Direction direction) {
        BlockState blockstate = blockReader.getBlockState(pos);
        return blockstate.isFaceSturdy(blockReader, pos, direction);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader worldReader, @NotNull BlockPos blockPos) {
        Direction mainDirection = state.getValue(FACING);
        Direction secondaryDirection = state.getValue(FACING2);
        BlockPos pos = blockPos.relative(secondaryDirection);
        if (mainDirection != secondaryDirection) {
            pos = pos.relative(mainDirection);
        }
        return this.canAttachTo(worldReader, pos, mainDirection);
    }

    @NotNull
    @Override
    public BlockState updateShape(@NotNull BlockState blockState, @NotNull Direction direction, @NotNull BlockState otherState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos otherPos) {
        boolean facing = blockState.getValue(FACING) == direction;
        if (!facing && blockState.getValue(FACING) != blockState.getValue(FACING2)) {
            pos = pos.relative(blockState.getValue(FACING));
            facing = blockState.getValue(FACING2) == direction;
        }
        if (facing && !blockState.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(blockState, direction, otherState, level, pos, otherPos);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection()).setValue(FACING2, context.getNearestLookingDirection()).setValue(AXIS, Direction.Axis.Y); //TODO usage?
    }

    @Override
    protected void spawnDestroyParticles(@NotNull Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state) {
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        return true;
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(FACING, FACING2, AXIS);
    }
}
