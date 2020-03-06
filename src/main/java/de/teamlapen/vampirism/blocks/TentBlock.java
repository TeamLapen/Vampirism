package de.teamlapen.vampirism.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.player.VampirismPlayer;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraftforge.common.extensions.IForgeDimension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Part of a 2x2 block tent
 * Position property contains the position within the 4 block arrangement
 */
public class TentBlock extends VampirismBlock {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final IntegerProperty POSITION = IntegerProperty.create("position", 0, 3);
    private static final String name = "tent";
    private static final VoxelShape NORTH = makeShape();
    private static final VoxelShape EAST = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.NINETY);
    private static final VoxelShape SOUTH = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.HUNDRED_EIGHTY);
    private static final VoxelShape WEST = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);

    private static final Map<PlayerEntity.SleepResult, ITextComponent> sleepResults;
    private static final Table<Integer, Direction, Pair<Double, Double>> offsets;

    public TentBlock() {
        this(name);
    }

    static {
        ImmutableTable.Builder<Integer, Direction, Pair<Double, Double>> offsetsBuilder = ImmutableTable.builder();
        offsetsBuilder.put(0, Direction.NORTH, Pair.of(1.0, 1.6));
        offsetsBuilder.put(0, Direction.EAST, Pair.of(-0.6, 1.0));
        offsetsBuilder.put(0, Direction.SOUTH, Pair.of(0.0, -0.6));
        offsetsBuilder.put(0, Direction.WEST, Pair.of(1.6, 0.0));
        offsetsBuilder.put(1, Direction.NORTH, Pair.of(1.0, -0.6));
        offsetsBuilder.put(1, Direction.EAST, Pair.of(1.6, 1.0));
        offsetsBuilder.put(1, Direction.SOUTH, Pair.of(0.0, 1.6));
        offsetsBuilder.put(1, Direction.WEST, Pair.of(-0.6, 0.0));
        offsetsBuilder.put(2, Direction.NORTH, Pair.of(1.0, 0.4));
        offsetsBuilder.put(2, Direction.EAST, Pair.of(0.6, 1.0));
        offsetsBuilder.put(2, Direction.SOUTH, Pair.of(0.0, 0.6));
        offsetsBuilder.put(2, Direction.WEST, Pair.of(0.4, 0.0));
        offsetsBuilder.put(3, Direction.NORTH, Pair.of(1.0, 0.6));
        offsetsBuilder.put(3, Direction.EAST, Pair.of(0.4, 1.0));
        offsetsBuilder.put(3, Direction.SOUTH, Pair.of(0.0, 0.4));
        offsetsBuilder.put(3, Direction.WEST, Pair.of(0.6, 0.0));
        offsets = offsetsBuilder.build();

        ImmutableMap.Builder<PlayerEntity.SleepResult, ITextComponent> sleepBuilder = ImmutableMap.builder();
        sleepBuilder.put(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW, new TranslationTextComponent("text.vampirism.tent.no_sleep"));
        sleepBuilder.put(PlayerEntity.SleepResult.TOO_FAR_AWAY, new TranslationTextComponent("text.vampirism.tent.too_far_away"));
        sleepBuilder.put(PlayerEntity.SleepResult.OBSTRUCTED, new TranslationTextComponent("text.vampirism.tent.obstructed"));
        sleepResults = sleepBuilder.build();
    }

    public static void setTentSleepPosition(PlayerEntity player, BlockPos blockPos, int position, Direction facing) {
        player.setPosition(blockPos.getX() + offsets.get(position, facing).getFirst(), blockPos.getY() + 0.0625, blockPos.getZ() + offsets.get(position, facing).getSecond());
    }

    protected TentBlock(String name) {
        super(name, Properties.create(Material.WOOL).hardnessAndResistance(0.6f).sound(SoundType.CLOTH));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(POSITION, 0).with(BedBlock.OCCUPIED, false));
    }

    @Override
    public Direction getBedDirection(BlockState state, IWorldReader world, BlockPos pos) {
        switch (state.get(POSITION)) {
            case 0:
            case 3:
                return state.get(HORIZONTAL_FACING).getOpposite();
            default:
                return state.get(HORIZONTAL_FACING);
        }
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState blockState, IBlockReader blockReader, BlockPos blockPos, ISelectionContext context) {
        switch (blockState.get(FACING)) {
            case NORTH:
                return NORTH;
            case EAST:
                return EAST;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
        }
        return NORTH;
    }

    @Override
    public boolean isBed(BlockState state, IBlockReader world, BlockPos pos, @Nullable Entity player) {
        return true;
    }

    @Override
    public boolean isNormalCube(BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        return false;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState blockState, World world, final BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (world.isRemote()) return ActionResultType.SUCCESS;
        if (HunterPlayer.getOpt(playerEntity).map(VampirismPlayer::getLevel).orElse(0) == 0) {
            playerEntity.sendStatusMessage(new TranslationTextComponent("text.vampirism.tent.cant_use"), true);
            return ActionResultType.SUCCESS;
        }
        IForgeDimension.SleepResult sleepResult = world.getDimension().canSleepAt(playerEntity, blockPos);
        if (sleepResult != IForgeDimension.SleepResult.BED_EXPLODES) {
            if (sleepResult == IForgeDimension.SleepResult.DENY) return ActionResultType.SUCCESS;
            if (blockState.get(BedBlock.OCCUPIED)) {
                playerEntity.sendStatusMessage(new TranslationTextComponent("text.vampirism.tent.occupied"), true);
                return ActionResultType.SUCCESS;
            } else {
                playerEntity.trySleep(blockPos).ifLeft(sleepResult1 -> {
                    if (sleepResult1 != null) {
                        playerEntity.sendStatusMessage(sleepResults.getOrDefault(sleepResult1, sleepResult1.getMessage()), true);
                    }
                }).ifRight(u -> {
                    setOccupied(world, blockState, blockPos, true);
                    setTentSleepPosition(playerEntity, blockPos, playerEntity.world.getBlockState(blockPos).get(POSITION), playerEntity.world.getBlockState(blockPos).get(HORIZONTAL_FACING));
                });
                return ActionResultType.SUCCESS;
            }
        } else {
            world.removeBlock(blockPos, false);
            BlockPos blockPos1 = blockPos.offset(blockState.get(HORIZONTAL_FACING).getOpposite());
            if (world.getBlockState(blockPos1).getBlock() == this) {
                world.removeBlock(blockPos1, false);
            }
            world.createExplosion(null, DamageSource.netherBedExplosion(), (double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
            return ActionResultType.SUCCESS;
        }
    }

    @Override
    public void onReplaced(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        super.onReplaced(state, world, pos, newState, isMoving);
        if (newState.getBlock() != state.getBlock()) {
            BlockPos main = pos;
            Direction dir = state.get(FACING);
            int p = state.get(POSITION);
            if (p == 0) {
                dir = dir.getOpposite();
            } else if (p == 1) {
                main = pos.offset(dir.rotateY());
            } else if (p == 2) {
                main = pos.offset(dir.rotateY()).offset(dir.getOpposite());
            } else if (p == 3) {
                main = pos.offset(dir);
                dir = dir.getOpposite();
            }
            BlockPos cur = main;
            if (cur != pos) world.destroyBlock(cur, true);
            cur = main.offset(dir);
            if (cur != pos) world.destroyBlock(cur, true);
            cur = main.offset(dir.rotateYCCW());
            if (cur != pos) world.destroyBlock(cur, true);
            cur = main.offset(dir).offset(dir.rotateYCCW());
            if (cur != pos) world.destroyBlock(cur, true);
        }

    }

    @Override
    public void setBedOccupied(BlockState state, IWorldReader world, BlockPos pos, LivingEntity sleeper, boolean occupied) {
        if (world instanceof IWorldWriter) {
            setOccupied((IWorldWriter & IWorldReader) world, state, pos, occupied);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, POSITION, BlockStateProperties.OCCUPIED);
    }

    private <T extends IWorldWriter & IWorldReader> void replaceWithOccupied(T world, BlockPos pos, boolean occupied) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof TentBlock) {
            world.setBlockState(pos, world.getBlockState(pos).with(BlockStateProperties.OCCUPIED, occupied), 2);
        }
    }

    private static VoxelShape makeShape() {
        return VoxelShapes.or(
                Block.makeCuboidShape(0, 0, 0, 16, 1, 16),
                Block.makeCuboidShape(0.5, 1, 0, 1.4, 1.45, 16),
                Block.makeCuboidShape(0.9, 1.4, 0, 1.8, 1.85, 16),
                Block.makeCuboidShape(1.3, 1.8, 0, 2.2, 2.25, 16),
                Block.makeCuboidShape(1.7, 2.2, 0, 2.6, 2.65, 16),
                Block.makeCuboidShape(2.1, 2.6, 0, 3.0, 3.05, 16),
                Block.makeCuboidShape(2.5, 3.0, 0, 3.4, 3.45, 16),
                Block.makeCuboidShape(2.9, 3.4, 0, 3.8, 3.85, 16),
                Block.makeCuboidShape(3.3, 3.8, 0, 4.2, 4.25, 16),
                Block.makeCuboidShape(3.7, 4.2, 0, 4.6, 4.65, 16),
                Block.makeCuboidShape(4.1, 4.6, 0, 5.0, 5.05, 16),
                Block.makeCuboidShape(4.5, 5.0, 0, 5.4, 5.45, 16),
                Block.makeCuboidShape(4.9, 5.4, 0, 5.8, 5.85, 16),
                Block.makeCuboidShape(5.3, 5.8, 0, 6.2, 6.25, 16),
                Block.makeCuboidShape(5.7, 6.2, 0, 6.6, 6.65, 16),
                Block.makeCuboidShape(6.1, 6.6, 0, 7.0, 7.05, 16),
                Block.makeCuboidShape(6.5, 7.0, 0, 7.4, 7.45, 16),
                Block.makeCuboidShape(6.9, 7.4, 0, 7.8, 7.85, 16),
                Block.makeCuboidShape(7.3, 7.8, 0, 8.2, 8.25, 16),
                Block.makeCuboidShape(7.7, 8.2, 0, 8.6, 8.65, 16),
                Block.makeCuboidShape(8.1, 8.6, 0, 9.0, 9.05, 16),
                Block.makeCuboidShape(8.5, 9.0, 0, 9.4, 9.45, 16),
                Block.makeCuboidShape(8.9, 9.4, 0, 9.8, 9.85, 16),
                Block.makeCuboidShape(9.3, 9.8, 0, 10.2, 10.25, 16),
                Block.makeCuboidShape(9.7, 10.2, 0, 10.6, 10.65, 16),
                Block.makeCuboidShape(10.1, 10.6, 0, 11.0, 11.05, 16),
                Block.makeCuboidShape(10.5, 11.0, 0, 11.4, 11.45, 16),
                Block.makeCuboidShape(10.9, 11.4, 0, 11.8, 11.85, 16),
                Block.makeCuboidShape(11.3, 11.8, 0, 12.2, 12.25, 16),
                Block.makeCuboidShape(11.7, 12.2, 0, 12.6, 12.65, 16),
                Block.makeCuboidShape(12.1, 12.6, 0, 13.0, 13.05, 16),
                Block.makeCuboidShape(12.5, 13.0, 0, 13.4, 13.45, 16),
                Block.makeCuboidShape(12.9, 13.4, 0, 13.8, 13.85, 16),
                Block.makeCuboidShape(13.3, 13.8, 0, 14.2, 14.25, 16),
                Block.makeCuboidShape(13.7, 14.2, 0, 14.6, 14.65, 16),
                Block.makeCuboidShape(14.1, 14.6, 0, 15.0, 15.05, 16),
                Block.makeCuboidShape(14.5, 15.0, 0, 15.4, 15.45, 16),
                Block.makeCuboidShape(14.9, 15.4, 0, 15.8, 15.85, 16),
                Block.makeCuboidShape(15, 15, 0, 16, 16, 16)
        );
    }

    private <T extends IWorldWriter & IWorldReader> void setOccupied(T world, BlockState state, BlockPos pos, boolean occupied) {
        BlockPos main = pos;
        Direction dir = state.get(FACING);
        int p = state.get(POSITION);
        if (p == 0) {
            dir = dir.getOpposite();
        } else if (p == 1) {
            main = pos.offset(dir.rotateY());
        } else if (p == 2) {
            main = pos.offset(dir.rotateY()).offset(dir.getOpposite());
        } else if (p == 3) {
            main = pos.offset(dir);
            dir = dir.getOpposite();
        }
        BlockPos cur = main;
        replaceWithOccupied(world, cur, occupied);
        cur = main.offset(dir);
        replaceWithOccupied(world, cur, occupied);
        cur = main.offset(dir.rotateYCCW());
        replaceWithOccupied(world, cur, occupied);
        cur = main.offset(dir).offset(dir.rotateYCCW());
        replaceWithOccupied(world, cur, occupied);
    }
}
