package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CursedBarkBlock extends Block {

    private static final VoxelShape shape =  VoxelShapes.empty();
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final DirectionProperty FACING2 = DirectionProperty.create("facing_2", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;


    public CursedBarkBlock() {
        super(AbstractBlock.Properties.of(Material.REPLACEABLE_PLANT, MaterialColor.NONE).noCollission().strength(0.0F).sound(SoundType.VINE));
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(FACING2, Direction.NORTH).setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    public void entityInside(@Nonnull BlockState state, @Nonnull World level, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        if (Helper.isVampire(entity) || (entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.invulnerable)) return;
        Direction mainDirection = state.getValue(FACING);
        Direction secondaryDirection = state.getValue(FACING2);
        BlockPos targetPos = pos.relative(mainDirection);
        if (mainDirection != secondaryDirection) {
            targetPos = targetPos.relative(secondaryDirection);
        }

        Vector3d thrust = new Vector3d(targetPos.getX(), targetPos.getY(), targetPos.getZ()).subtract(pos.getX(), pos.getY(), pos.getZ()).normalize().scale(0.04);
        if (!entity.isOnGround()) {
            thrust = thrust.scale(0.3);
        }
        entity.setDeltaMovement(entity.getDeltaMovement().add(thrust));

        if (!level.isClientSide) {
            if (entity instanceof PlayerEntity) {
                VampirePlayer.getOpt(((PlayerEntity) entity)).ifPresent(vampire -> {
                    if (vampire.getRemainingBarkTicks() == 0) {
                        vampire.removeBlood(0.02f);
                        vampire.increaseRemainingBarkTicks(40);
                    }
                });
            } else {
                ExtendedCreature.getSafe(entity).ifPresent(creature -> {
                    if (((ExtendedCreature) creature).getRemainingBarkTicks() == 0) {
                        creature.setBlood(creature.getBlood() - 1);
                        ((ExtendedCreature)creature).sync();
                        ((ExtendedCreature) creature).increaseRemainingBarkTicks(40);
                    }

                });
            }
        }
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader blockReader, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return shape;
    }

    private boolean canAttachTo(IBlockReader blockReader, BlockPos pos, Direction direction) {
        BlockState blockstate = blockReader.getBlockState(pos);
        return blockstate.isFaceSturdy(blockReader, pos, direction);
    }

    @Override
    public boolean canSurvive(BlockState state, @Nonnull IWorldReader worldReader, BlockPos blockPos) {
        Direction mainDirection = state.getValue(FACING);
        Direction secondaryDirection = state.getValue(FACING2);
        BlockPos pos =  blockPos.relative(secondaryDirection);
        if (mainDirection != secondaryDirection) {
            pos = pos.relative(mainDirection);
        }
        return this.canAttachTo(worldReader, pos, mainDirection);
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState blockState, @Nonnull Direction direction, @Nonnull BlockState otherState, @Nonnull IWorld level, @Nonnull BlockPos pos, @Nonnull BlockPos otherPos) {
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
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection()).setValue(FACING2, context.getNearestLookingDirection()).setValue(AXIS, Direction.Axis.Y); //TODO usage?
    }

    @Override
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACING2, AXIS);
    }
}
