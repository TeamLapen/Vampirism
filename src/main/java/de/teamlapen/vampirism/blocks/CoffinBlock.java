package de.teamlapen.vampirism.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.tileentity.CoffinTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Block coffin.
 */
public class CoffinBlock extends VampirismBlockContainer {

    public static final Map<DyeColor, CoffinBlock> COFFIN_BLOCKS = new HashMap<>();
    public static final String name = "coffin";
    public static final EnumProperty<CoffinPart> PART = EnumProperty.create("part", CoffinPart.class);
    public static final BooleanProperty CLOSED = BooleanProperty.create("closed");
    private static final Table<Direction, Boolean, Pair<VoxelShape,VoxelShape>> shapes;
    private static final Map<PlayerEntity.SleepResult, ITextComponent> sleepResults = ImmutableMap.of(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW, new TranslationTextComponent("text.vampirism.coffin.no_sleep"), PlayerEntity.SleepResult.TOO_FAR_AWAY, new TranslationTextComponent("text.vampirism.coffin.too_far_away"), PlayerEntity.SleepResult.OBSTRUCTED, new TranslationTextComponent("text.vampirism.coffin.obstructed"));

    public static boolean isOccupied(IBlockReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof CoffinBlock && state.getValue(BedBlock.OCCUPIED);
    }

    public static boolean isClosed(IBlockReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof CoffinBlock && state.getValue(CLOSED);
    }

    public static boolean isHead(IBlockReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof CoffinBlock && state.getValue(PART) == CoffinPart.HEAD;
    }

    private static Direction getDirectionToOther(CoffinPart type, Direction facing) {
        return type == CoffinPart.FOOT ? facing : facing.getOpposite();
    }

    static {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.join(shape, VoxelShapes.box(0, 0, 0, 1, 0.0625, 2), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.90625, 0.0625, 0.046875, 0.96875, 0.1875, 1.96875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.875, 0.1875, 1.375, 0.9375, 0.375, 1.875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.875, 0.1875, 0.75, 0.9375, 0.375, 1.25), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.875, 0.1875, 0.125, 0.9375, 0.375, 0.625), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.25, 0.1875, 1.875, 0.75, 0.375, 1.9375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.0625, 0.1875, 0.125, 0.125, 0.375, 0.625), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.0625, 0.1875, 0.75, 0.125, 0.375, 1.25), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.0625, 0.1875, 1.375, 0.125, 0.375, 1.875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.25, 0.1875, 0.0625, 0.75, 0.375, 0.125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.03125, 0.0625, 0.046875, 0.09375, 0.1875, 1.96875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.09375, 0.0625, 1.921875, 0.90625, 0.1875, 1.96875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.09375, 0.0625, 0.046875, 0.90625, 0.1875, 0.09375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.90625, 0.1875, 1.875, 0.96875, 0.375, 1.96875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.90625, 0.1875, 1.25, 0.96875, 0.375, 1.375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.90625, 0.1875, 0.625, 0.96875, 0.375, 0.75), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.90625, 0.1875, 0.046875, 0.96875, 0.375, 0.125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.03125, 0.1875, 0.046875, 0.09375, 0.375, 0.125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.03125, 0.1875, 0.625, 0.09375, 0.375, 0.75), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.03125, 0.1875, 1.25, 0.09375, 0.375, 1.375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.03125, 0.1875, 1.875, 0.09375, 0.375, 1.96875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.09375, 0.1875, 1.921875, 0.25, 0.375, 1.96875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.75, 0.1875, 1.921875, 0.90625, 0.375, 1.96875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.75, 0.1875, 0.046875, 0.90625, 0.375, 0.09375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.09375, 0.1875, 0.046875, 0.25, 0.375, 0.09375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.09375, 0.375, 0.046875, 0.90625, 0.5, 0.09375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.03125, 0.375, 0.046875, 0.09375, 0.5, 1.96875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.09375, 0.375, 1.921875, 0.90625, 0.5, 1.96875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.90625, 0.375, 0.046875, 0.96875, 0.5, 1.96875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.90625, 0.5, 0, 1, 0.5625, 2), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0, 0.5, 0, 0.09375, 0.5625, 2), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.09375, 0.5, 1.921875, 0.90625, 0.5625, 2), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.09375, 0.5, 0, 0.90625, 0.5625, 0.09375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.09375, 0.0625, 0.09375, 0.140625, 0.546875, 1.921875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.859375, 0.0625, 0.09375, 0.90625, 0.546875, 1.921875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.140625, 0.0625, 1.859375, 0.859375, 0.546875, 1.921875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.140625, 0.0625, 0.09375, 0.859375, 0.546875, 0.15625), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.1875, 0.125, 0.1875, 0.8125, 0.3125, 0.5), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.203125, 0.203125, 0.4375, 0.796875, 0.34375, 0.5625), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.203125, 0.203125, 0.171875, 0.796875, 0.328125, 0.296875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.9375, 0.09375, 0.25, 1, 0.15625, 0.5), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.9375, 0.09375, 0.875, 1, 0.15625, 1.125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.9375, 0.09375, 1.5, 1, 0.15625, 1.75), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0, 0.09375, 1.5, 0.0625, 0.15625, 1.75), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0, 0.09375, 0.875, 0.0625, 0.15625, 1.125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0, 0.09375, 0.25, 0.0625, 0.15625, 0.5), IBooleanFunction.OR);

        VoxelShape lidShape = VoxelShapes.empty();
        lidShape = VoxelShapes.join(lidShape, VoxelShapes.box(0, 0.5625, 0, 0.09375, 0.625, 2), IBooleanFunction.OR);
        lidShape = VoxelShapes.join(lidShape, VoxelShapes.box(0.09375, 0.5625, 0, 0.90625, 0.625, 0.078125), IBooleanFunction.OR);
        lidShape = VoxelShapes.join(lidShape, VoxelShapes.box(0.90625, 0.5625, 0, 1, 0.625, 2), IBooleanFunction.OR);
        lidShape = VoxelShapes.join(lidShape, VoxelShapes.box(0.09375, 0.5625, 1.90625, 0.90625, 0.625, 2), IBooleanFunction.OR);
        lidShape = VoxelShapes.join(lidShape, VoxelShapes.box(0.09375, 0.625, 0.078125, 0.90625, 0.6875, 1.9375), IBooleanFunction.OR);

        VoxelShape top = VoxelShapes.join(shape, VoxelShapes.box(0,0,0,1,1,1), IBooleanFunction.AND);
        VoxelShape bottom = VoxelShapes.join(shape, VoxelShapes.box(0,0,1,1,1,2), IBooleanFunction.AND);
        VoxelShape lidTop = VoxelShapes.join(lidShape, VoxelShapes.box(0,0,0,1,1,1), IBooleanFunction.AND);
        VoxelShape lidBottom = VoxelShapes.join(lidShape, VoxelShapes.box(0,0,1,1,1,2), IBooleanFunction.AND);

        bottom = bottom.move(0,0,-1);
        lidBottom = lidBottom.move(0,0,-1);

        ImmutableTable.Builder<Direction, Boolean, Pair<VoxelShape,VoxelShape>> shapeBuilder = ImmutableTable.builder();
        shapeBuilder.put(Direction.WEST, false, Pair.of(UtilLib.rotateShape(top, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY), UtilLib.rotateShape(bottom, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY)));
        shapeBuilder.put(Direction.SOUTH, false, Pair.of(UtilLib.rotateShape(top, UtilLib.RotationAmount.HUNDRED_EIGHTY),UtilLib.rotateShape(bottom, UtilLib.RotationAmount.HUNDRED_EIGHTY)));
        //noinspection SuspiciousNameCombination
        shapeBuilder.put(Direction.NORTH, false, Pair.of(top, bottom));
        shapeBuilder.put(Direction.EAST, false, Pair.of(UtilLib.rotateShape(top, UtilLib.RotationAmount.NINETY),UtilLib.rotateShape(bottom, UtilLib.RotationAmount.NINETY)));
        shapeBuilder.put(Direction.WEST, true, Pair.of(UtilLib.rotateShape(VoxelShapes.or(top, lidTop), UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY),UtilLib.rotateShape(VoxelShapes.or(top, lidBottom), UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY)));
        shapeBuilder.put(Direction.SOUTH, true, Pair.of(UtilLib.rotateShape(VoxelShapes.or(top, lidTop), UtilLib.RotationAmount.HUNDRED_EIGHTY),UtilLib.rotateShape(VoxelShapes.or(top, lidBottom), UtilLib.RotationAmount.HUNDRED_EIGHTY)));
        shapeBuilder.put(Direction.NORTH, true, Pair.of(VoxelShapes.or(top, lidTop),VoxelShapes.or(bottom, lidBottom)));
        shapeBuilder.put(Direction.EAST, true, Pair.of(UtilLib.rotateShape(VoxelShapes.or(top, lidTop), UtilLib.RotationAmount.NINETY),UtilLib.rotateShape(VoxelShapes.or(top, lidBottom), UtilLib.RotationAmount.NINETY)));
        shapes = shapeBuilder.build();
    }

    private final DyeColor color;

    public CoffinBlock(DyeColor color) {
        super(name + "_" + color.getName(), Properties.of(Material.WOOD).strength(0.2f).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(BedBlock.OCCUPIED, Boolean.FALSE).setValue(PART, CoffinPart.FOOT).setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(CLOSED, false));
        this.color = color;
        COFFIN_BLOCKS.put(color, this);
    }

    @Override
    public Direction getBedDirection(BlockState state, IWorldReader world, BlockPos pos) {
        return state.getValue(HORIZONTAL_FACING);
    }

    @Nonnull
    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction enumfacing = context.getHorizontalDirection();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(enumfacing);
        return context.getLevel().getBlockState(blockpos1).canBeReplaced(context) ? this.defaultBlockState().setValue(HORIZONTAL_FACING, enumfacing) : null;
    }

    @Override
    public boolean isPathfindable(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, PathType type) {
        return false;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction facing = state.getValue(HORIZONTAL_FACING);
        Pair<VoxelShape,VoxelShape> shape = shapes.get(facing, state.getValue(CLOSED));
        return state.getValue(PART) == CoffinPart.FOOT ? shape.getRight() : shape.getLeft();
    }


    @Override
    public boolean isBed(BlockState state, IBlockReader world, BlockPos pos, Entity player) {
        return !state.getValue(CLOSED) || state.getValue(BedBlock.OCCUPIED);
    }

    @Override
    public TileEntity newBlockEntity(@Nonnull IBlockReader worldIn) {
        return new CoffinTileEntity(false, color);
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, @Nonnull PlayerEntity player) {
        //If in creative mode, also destroy the head block. Otherwise, it will be destroyed due to updateShape and an item will drop
        if (!worldIn.isClientSide && player.isCreative()) {
            CoffinPart part = state.getValue(PART);
            if(part == CoffinPart.FOOT){
                BlockPos blockpos = pos.relative(getDirectionToOther(part, state.getValue(HORIZONTAL_FACING)));
                BlockState blockstate = worldIn.getBlockState(blockpos);
                if (blockstate.getBlock() == this && blockstate.getValue(PART) == CoffinPart.HEAD) {
                    worldIn.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    worldIn.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }

        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
        super.setPlacedBy(worldIn, pos, state, entity, itemStack);
        if (!worldIn.isClientSide) {
            BlockPos blockpos = pos.relative(state.getValue(HORIZONTAL_FACING));
            worldIn.setBlock(blockpos, state.setValue(PART, CoffinPart.HEAD), 3);
            worldIn.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(worldIn, pos, 3);
        }
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing == getDirectionToOther(stateIn.getValue(PART), stateIn.getValue(HORIZONTAL_FACING))) {
            return facingState.getBlock() == this && facingState.getValue(PART) != stateIn.getValue(PART) ? stateIn.setValue(BedBlock.OCCUPIED, facingState.getValue(BedBlock.OCCUPIED)) : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {

        if (worldIn.isClientSide) {
            return ActionResultType.SUCCESS;
        } else {
            if (state.getValue(PART) != CoffinPart.HEAD) {
                pos = pos.relative(state.getValue(HORIZONTAL_FACING));
                state = worldIn.getBlockState(pos);
                if (!state.is(this)) {
                    return ActionResultType.SUCCESS;
                }
            }

            if (player.isShiftKeyDown() && !state.getValue(BedBlock.OCCUPIED)) {
                worldIn.setBlock(pos, state.setValue(CLOSED, !state.getValue(CLOSED)), 3);
                return ActionResultType.SUCCESS;
            } else if (VampirismPlayerAttributes.get(player).vampireLevel == 0) {
                player.displayClientMessage(new TranslationTextComponent("text.vampirism.coffin.cant_use"), true);
                return ActionResultType.SUCCESS;
            } else if (state.getValue(BedBlock.OCCUPIED)) {
                player.displayClientMessage(new TranslationTextComponent("text.vampirism.coffin.occupied"), true);
                return ActionResultType.SUCCESS;
            } else if (state.getValue(CLOSED)) {
                player.displayClientMessage(new TranslationTextComponent("text.vampirism.coffin.closed"), true);
                return ActionResultType.SUCCESS;
            }

            if (!BedBlock.canSetSpawn(worldIn)) {
                worldIn.removeBlock(pos, false);
                BlockPos blockpos = pos.relative(state.getValue(HORIZONTAL_FACING).getOpposite());
                if (worldIn.getBlockState(blockpos).is(this)) {
                    worldIn.removeBlock(blockpos, false);
                }

                worldIn.explode(null, DamageSource.badRespawnPointExplosion(), null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
                return ActionResultType.SUCCESS;
            } else if (state.getValue(BedBlock.OCCUPIED)) {
                player.displayClientMessage(new TranslationTextComponent("text.vampirism.coffin.occupied"), true);
                return ActionResultType.SUCCESS;
            } else {
                final BlockPos finalPos = pos;
                player.startSleepInBed(pos).ifLeft(sleepResult1 -> {
                    if (sleepResult1 != null) {
                        player.displayClientMessage(sleepResults.getOrDefault(sleepResult1, sleepResult1.getMessage()), true);
                    }
                }).ifRight(u -> {
                    setCoffinSleepPosition(player, finalPos);
                });
                return ActionResultType.SUCCESS;
            }
        }
    }

    public static void setCoffinSleepPosition(PlayerEntity player, BlockPos blockPos) {
        player.setPos(blockPos.getX() + 0.5D, blockPos.getY() + 0.2D, blockPos.getZ() + 0.5D);
        player.setBoundingBox(player.dimensions.makeBoundingBox(blockPos.getX() + 0.5D, blockPos.getY() + 0.2D, blockPos.getZ() + 0.5D).deflate(0.3));
    }

    @Override
    public void setBedOccupied(BlockState state, World world, BlockPos pos, LivingEntity sleeper, boolean occupied) {
        super.setBedOccupied(state, world, pos, sleeper, occupied);
        world.setBlock(pos, world.getBlockState(pos).setValue(CLOSED, occupied), 3);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, BedBlock.OCCUPIED, PART, CLOSED);
    }

    public DyeColor getColor() {
        return color;
    }

    public enum CoffinPart implements IStringSerializable {
        HEAD("head"),
        FOOT("foot");

        private final String name;

        CoffinPart(String name) {
            this.name = name;
        }

        @Nonnull
        public String getSerializedName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }
    }
}
