package de.teamlapen.vampirism.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.client.extensions.BlockExtensions;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

import static net.minecraft.world.level.block.BedBlock.OCCUPIED;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Part of a 2x2 block tent
 * Position property contains the position within the 4 block arrangement
 */
public class TentBlock extends VampirismBlock {
    public static final DirectionProperty FACING = HORIZONTAL_FACING;
    /**
     * Arrangement
     * 23
     * 10
     */
    public static final IntegerProperty POSITION = IntegerProperty.create("position", 0, 3);
    private static final @NotNull Table<Direction, Integer, VoxelShape> shapes;
    private static final @NotNull Map<Player.BedSleepingProblem, Component> sleepResults;
    private static final @NotNull Table<Integer, Direction, Pair<Double, Double>> offsets;

    static {
        VoxelShape NORTH = makeShape();
        VoxelShape EAST = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.NINETY);
        VoxelShape SOUTH = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.HUNDRED_EIGHTY);
        VoxelShape WEST = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);
        VoxelShape BACK = makeShapeBack1();
        VoxelShape BACKMIRROR = makeShapeBack2();
        VoxelShape SOUTHR = Shapes.or(SOUTH, BACK);
        VoxelShape NORTHL = Shapes.or(NORTH, BACKMIRROR);

        ImmutableTable.Builder<Direction, Integer, VoxelShape> shapeBuilder = ImmutableTable.builder();
        shapeBuilder.put(Direction.NORTH, 0, NORTH);
        shapeBuilder.put(Direction.NORTH, 1, NORTH);
        shapeBuilder.put(Direction.NORTH, 2, NORTHL);
        shapeBuilder.put(Direction.NORTH, 3, UtilLib.rotateShape(SOUTHR, UtilLib.RotationAmount.HUNDRED_EIGHTY));
        shapeBuilder.put(Direction.EAST, 0, EAST);
        shapeBuilder.put(Direction.EAST, 1, EAST);
        shapeBuilder.put(Direction.EAST, 2, UtilLib.rotateShape(NORTHL, UtilLib.RotationAmount.NINETY));
        shapeBuilder.put(Direction.EAST, 3, UtilLib.rotateShape(SOUTHR, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY));
        shapeBuilder.put(Direction.SOUTH, 0, SOUTH);
        shapeBuilder.put(Direction.SOUTH, 1, SOUTH);
        shapeBuilder.put(Direction.SOUTH, 2, UtilLib.rotateShape(NORTHL, UtilLib.RotationAmount.HUNDRED_EIGHTY));
        shapeBuilder.put(Direction.SOUTH, 3, SOUTHR);
        shapeBuilder.put(Direction.WEST, 0, WEST);
        shapeBuilder.put(Direction.WEST, 1, WEST);
        shapeBuilder.put(Direction.WEST, 2, UtilLib.rotateShape(NORTHL, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY));
        shapeBuilder.put(Direction.WEST, 3, UtilLib.rotateShape(SOUTHR, UtilLib.RotationAmount.NINETY));
        shapes = shapeBuilder.build();

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

        ImmutableMap.Builder<Player.BedSleepingProblem, Component> sleepBuilder = ImmutableMap.builder();
        sleepBuilder.put(Player.BedSleepingProblem.NOT_POSSIBLE_NOW, Component.translatable("text.vampirism.tent.no_sleep"));
        sleepBuilder.put(Player.BedSleepingProblem.TOO_FAR_AWAY, Component.translatable("text.vampirism.tent.too_far_away"));
        sleepBuilder.put(Player.BedSleepingProblem.OBSTRUCTED, Component.translatable("text.vampirism.tent.obstructed"));
        sleepResults = sleepBuilder.build();
    }

    public static void setTentSleepPosition(@NotNull Player player, @NotNull BlockPos blockPos, int position, Direction facing) {
        player.setPos(blockPos.getX() + offsets.get(position, facing).getFirst(), blockPos.getY() + 0.0625, blockPos.getZ() + offsets.get(position, facing).getSecond());
    }

    private static @NotNull VoxelShape makeShape() {
        return Shapes.or(
                Block.box(0, 0, 0, 16, 1, 16),
                Block.box(0.5, 1, 0, 1.4, 1.45, 16),
                Block.box(0.9, 1.4, 0, 1.8, 1.85, 16),
                Block.box(1.3, 1.8, 0, 2.2, 2.25, 16),
                Block.box(1.7, 2.2, 0, 2.6, 2.65, 16),
                Block.box(2.1, 2.6, 0, 3.0, 3.05, 16),
                Block.box(2.5, 3.0, 0, 3.4, 3.45, 16),
                Block.box(2.9, 3.4, 0, 3.8, 3.85, 16),
                Block.box(3.3, 3.8, 0, 4.2, 4.25, 16),
                Block.box(3.7, 4.2, 0, 4.6, 4.65, 16),
                Block.box(4.1, 4.6, 0, 5.0, 5.05, 16),
                Block.box(4.5, 5.0, 0, 5.4, 5.45, 16),
                Block.box(4.9, 5.4, 0, 5.8, 5.85, 16),
                Block.box(5.3, 5.8, 0, 6.2, 6.25, 16),
                Block.box(5.7, 6.2, 0, 6.6, 6.65, 16),
                Block.box(6.1, 6.6, 0, 7.0, 7.05, 16),
                Block.box(6.5, 7.0, 0, 7.4, 7.45, 16),
                Block.box(6.9, 7.4, 0, 7.8, 7.85, 16),
                Block.box(7.3, 7.8, 0, 8.2, 8.25, 16),
                Block.box(7.7, 8.2, 0, 8.6, 8.65, 16),
                Block.box(8.1, 8.6, 0, 9.0, 9.05, 16),
                Block.box(8.5, 9.0, 0, 9.4, 9.45, 16),
                Block.box(8.9, 9.4, 0, 9.8, 9.85, 16),
                Block.box(9.3, 9.8, 0, 10.2, 10.25, 16),
                Block.box(9.7, 10.2, 0, 10.6, 10.65, 16),
                Block.box(10.1, 10.6, 0, 11.0, 11.05, 16),
                Block.box(10.5, 11.0, 0, 11.4, 11.45, 16),
                Block.box(10.9, 11.4, 0, 11.8, 11.85, 16),
                Block.box(11.3, 11.8, 0, 12.2, 12.25, 16),
                Block.box(11.7, 12.2, 0, 12.6, 12.65, 16),
                Block.box(12.1, 12.6, 0, 13.0, 13.05, 16),
                Block.box(12.5, 13.0, 0, 13.4, 13.45, 16),
                Block.box(12.9, 13.4, 0, 13.8, 13.85, 16),
                Block.box(13.3, 13.8, 0, 14.2, 14.25, 16),
                Block.box(13.7, 14.2, 0, 14.6, 14.65, 16),
                Block.box(14.1, 14.6, 0, 15.0, 15.05, 16),
                Block.box(14.5, 15.0, 0, 15.4, 15.45, 16),
                Block.box(14.9, 15.4, 0, 15.8, 15.85, 16),
                Block.box(15, 15, 0, 16, 16, 16)
        );
    }

    private static @NotNull VoxelShape makeShapeBack2() {
        return Shapes.or(
                Block.box(15, 1, 0, 16, 15.85, 1),
                Block.box(14, 1, 0, 15, 14.65, 1),
                Block.box(13, 1, 0, 14, 13.85, 1),
                Block.box(12, 1, 0, 13, 12.65, 1),
                Block.box(11, 1, 0, 12, 11.85, 1),
                Block.box(10, 1, 0, 11, 10.65, 1),
                Block.box(9, 1, 0, 10, 9.85, 1),
                Block.box(8, 1, 0, 9, 8.65, 1),
                Block.box(7, 1, 0, 8, 7.85, 1),
                Block.box(6, 1, 0, 7, 6.65, 1),
                Block.box(5, 1, 0, 6, 5.85, 1),
                Block.box(4, 1, 0, 5, 4.65, 1),
                Block.box(3, 1, 0, 4, 3.85, 1),
                Block.box(2, 1, 0, 3, 2.65, 1),
                Block.box(1, 1, 0, 2, 1.85, 1));
    }

    private static @NotNull VoxelShape makeShapeBack1() {
        return Shapes.or(
                Block.box(14, 1, 0, 15, 1.85, 1),
                Block.box(13, 1, 0, 14, 2.65, 1),
                Block.box(12, 1, 0, 13, 3.85, 1),
                Block.box(11, 1, 0, 12, 4.65, 1),
                Block.box(10, 1, 0, 11, 5.85, 1),
                Block.box(9, 1, 0, 10, 6.65, 1),
                Block.box(8, 1, 0, 9, 7.85, 1),
                Block.box(7, 1, 0, 8, 8.65, 1),
                Block.box(6, 1, 0, 7, 9.85, 1),
                Block.box(5, 1, 0, 6, 10.65, 1),
                Block.box(4, 1, 0, 5, 11.85, 1),
                Block.box(3, 1, 0, 4, 12.65, 1),
                Block.box(2, 1, 0, 3, 13.85, 1),
                Block.box(1, 1, 0, 2, 14.65, 1),
                Block.box(0, 1, 0, 1, 15.85, 1));
    }

    public TentBlock() {
        super(Properties.of().mapColor(MapColor.WOOL).ignitedByLava().strength(0.6f).sound(SoundType.WOOL).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(POSITION, 0).setValue(OCCUPIED, false));
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(BlockExtensions.TENT);
    }


    @Override
    public boolean canSurvive(@NotNull BlockState blockState, @NotNull LevelReader worldReader, @NotNull BlockPos blockPos) {
        return worldReader.getBlockState(blockPos).isAir();
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return ModItems.ITEM_TENT.get().getDefaultInstance();
    }

    @Override
    public void fallOn(@NotNull Level worldIn, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull Entity entityIn, float fallDistance) {
        super.fallOn(worldIn, state, pos, entityIn, fallDistance * 0.7F);
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter level, BlockPos pos, LivingEntity sleeper) {
        return true;
    }

    @Override
    public @NotNull Direction getBedDirection(@NotNull BlockState state, LevelReader world, BlockPos pos) {
        Direction thisFacing = state.getValue(FACING);
        int thisPos = state.getValue(POSITION);
        if (thisPos != 0) {
            BlockPos main = switch (thisPos) {
                case 1 -> pos.relative(thisFacing.getClockWise());
                case 2 -> pos.relative(thisFacing.getOpposite()).relative(thisFacing.getClockWise());
                case 3 -> pos.relative(thisFacing);
                default -> null;
            };
            if (main != null) {
                state = world.getBlockState(main);
            }
        }
        return switch (state.getValue(POSITION)) {
            case 0, 3 -> state.getValue(HORIZONTAL_FACING).getOpposite();
            default -> state.getValue(HORIZONTAL_FACING);
        };
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter blockReader, @NotNull BlockPos blockPos, @NotNull CollisionContext context) {
        return shapes.get(blockState.getValue(FACING), blockState.getValue(POSITION));
    }


    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        Direction thisFacing = stateIn.getValue(FACING);
        int thisPos = stateIn.getValue(POSITION);
        if (facing == thisFacing.getClockWise() || (thisPos == 0 || thisPos == 2) && facing == thisFacing.getOpposite() || (thisPos == 1 || thisPos == 3) && facing == thisFacing) {
            return facingState.getBlock() instanceof TentBlock ? stateIn.setValue(OCCUPIED, facingState.getValue(OCCUPIED)) : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }


    @Override
    public BlockState playerWillDestroy(@NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        //If in creative mode, also destroy the main block. Otherwise, it will be destroyed due to updateShape and an item will drop
        if (!worldIn.isClientSide && player.isCreative()) {
            Direction thisFacing = state.getValue(FACING);
            int thisPos = state.getValue(POSITION);
            if (thisPos != 0) {
                BlockPos main = switch (thisPos) {
                    case 1 -> pos.relative(thisFacing.getClockWise());
                    case 2 -> pos.relative(thisFacing.getOpposite()).relative(thisFacing.getClockWise());
                    case 3 -> pos.relative(thisFacing);
                    default -> null;
                };
                if (main != null) {
                    BlockState blockstate = worldIn.getBlockState(main);
                    if (blockstate.getBlock() == ModBlocks.TENT_MAIN.get()) {
                        worldIn.setBlock(main, Blocks.AIR.defaultBlockState(), 35);
                        worldIn.levelEvent(player, 2001, main, Block.getId(blockstate));
                    }
                }
            }
        }
        return super.playerWillDestroy(worldIn, pos, state, player);
    }


    @Override
    public void updateEntityAfterFallOn(@NotNull BlockGetter worldIn, @NotNull Entity entityIn) {
        if (entityIn.isShiftKeyDown()) {
            super.updateEntityAfterFallOn(worldIn, entityIn);
        } else {
            Vec3 vec3d = entityIn.getDeltaMovement();
            if (vec3d.y < 0.0D) {
                double d0 = entityIn instanceof LivingEntity ? 1.0D : 0.8D;
                entityIn.setDeltaMovement(vec3d.x, -vec3d.y * (double) 0.33F * d0, vec3d.z);
            }
        }

    }

    @NotNull
    @Override
    public InteractionResult useWithoutItem(@NotNull BlockState blockState, @NotNull Level world, @NotNull final BlockPos pos, @NotNull Player player, @NotNull BlockHitResult rayTraceResult) {
        if (world.isClientSide()) return InteractionResult.SUCCESS;
        if (VampirismPlayerAttributes.get(player).hunterLevel == 0) {
            player.displayClientMessage(Component.translatable("text.vampirism.tent.cant_use"), true);
            return InteractionResult.SUCCESS;
        }

        if (!BedBlock.canSetSpawn(world)) {
            world.removeBlock(pos, false);
            BlockPos blockpos = pos.relative(blockState.getValue(HORIZONTAL_FACING).getOpposite());
            if (world.getBlockState(blockpos).is(this)) {
                world.removeBlock(blockpos, false);
            }

            Vec3 vec3 = pos.getCenter();
            world.explode(null, world.damageSources().badRespawnPointExplosion(vec3), null, vec3, 5.0F, true, Level.ExplosionInteraction.BLOCK);
            return InteractionResult.SUCCESS;
        } else if (blockState.getValue(OCCUPIED)) {
            player.displayClientMessage(Component.translatable("text.vampirism.tent.occupied"), true);
            return InteractionResult.SUCCESS;
        } else {
            BlockState targetState = blockState;
            BlockPos targetPos = pos;
            Direction thisFacing = blockState.getValue(FACING);
            int thisPos = blockState.getValue(POSITION);
            if (thisPos != 0) {
                BlockPos main = switch (thisPos) {
                    case 1 -> pos.relative(thisFacing.getClockWise());
                    case 2 -> pos.relative(thisFacing.getOpposite()).relative(thisFacing.getClockWise());
                    case 3 -> pos.relative(thisFacing);
                    default -> null;
                };
                if (main != null) {
                    targetState = world.getBlockState(main);
                    targetPos = main;
                }
            }
            BlockState finalTargetState = targetState;
            BlockPos finalTargetPos = targetPos;
            player.startSleepInBed(finalTargetPos).ifLeft(sleepResult1 -> {
                if (sleepResult1 != null) {
                    player.displayClientMessage(sleepResults.getOrDefault(sleepResult1, sleepResult1.getMessage()), true);
                }
            }).ifRight(u -> {
                this.setBedOccupied(finalTargetState, world, finalTargetPos, null, true);
                setTentSleepPosition(player, finalTargetPos, finalTargetState.getValue(POSITION), finalTargetState.getValue(HORIZONTAL_FACING));
            });
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(FACING, POSITION, BlockStateProperties.OCCUPIED);
    }

}
