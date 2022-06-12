package de.teamlapen.vampirism.blocks;

import com.google.common.collect.ImmutableMap;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.tileentity.CoffinTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;

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
    public static final EnumProperty<CoffinPart> PART = EnumProperty.create("part", CoffinPart.class);
    public static final BooleanProperty CLOSED = BooleanProperty.create("closed");
    public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");
    private static ShapeTable shapes = new ShapeTable();
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

    private final DyeColor color;

    public CoffinBlock(DyeColor color) {
        super(Properties.of(Material.WOOD).strength(0.2f).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(BedBlock.OCCUPIED, Boolean.FALSE).setValue(PART, CoffinPart.FOOT).setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(CLOSED, false).setValue(VERTICAL, false));
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
        boolean vertical = context.getClickedFace().getAxis() != Direction.Axis.Y;
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(vertical ? Direction.UP : enumfacing);
        return context.getLevel().getBlockState(blockpos1).canBeReplaced(context) ? this.defaultBlockState().setValue(HORIZONTAL_FACING, enumfacing).setValue(VERTICAL, vertical) : null;
    }

    @Override
    public boolean isPathfindable(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, PathType type) {
        return false;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shapes.getShape(state.getValue(PART), state.getValue(CLOSED), state.getValue(VERTICAL), state.getValue(HORIZONTAL_FACING));
    }


    @Override
    public boolean isBed(BlockState state, IBlockReader world, BlockPos pos, Entity player) {
        return !state.getValue(CLOSED) || state.getValue(BedBlock.OCCUPIED);
    }

    @Override
    public TileEntity newBlockEntity(@Nonnull IBlockReader worldIn) {
        return new CoffinTileEntity(color);
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, @Nonnull PlayerEntity player) {
        //If in creative mode, also destroy the head block. Otherwise, it will be destroyed due to updateShape and an item will drop
        if (!worldIn.isClientSide && player.isCreative()) {
            CoffinPart part = state.getValue(PART);
            if(part == CoffinPart.FOOT) {
                BlockPos blockpos = getOtherPos(pos, state);
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
            BlockPos blockpos = getOtherPos(pos, state);
            worldIn.setBlock(blockpos, state.setValue(PART, CoffinPart.HEAD), 3);
            worldIn.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(worldIn, pos, 3);
        }
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing == getDirectionToOther(stateIn.getValue(PART), stateIn.getValue(VERTICAL) ? Direction.UP:stateIn.getValue(HORIZONTAL_FACING))) {
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
                pos = getOtherPos(pos, state);
                state = worldIn.getBlockState(pos);
                if (!state.is(this)) {
                    return ActionResultType.CONSUME;
                }
            }

            if (player.isShiftKeyDown() && !state.getValue(BedBlock.OCCUPIED)) {
                worldIn.setBlock(pos, state.setValue(CLOSED, !state.getValue(CLOSED)), 3);
                BlockPos otherPos = getOtherPos(pos, state);
                worldIn.setBlock(otherPos, worldIn.getBlockState(otherPos).setValue(CLOSED, !state.getValue(CLOSED)),3);
                return ActionResultType.CONSUME;
            } else if (VampirismPlayerAttributes.get(player).vampireLevel == 0) {
                player.displayClientMessage(new TranslationTextComponent("text.vampirism.coffin.cant_use"), true);
                return ActionResultType.CONSUME;
            } else if (state.getValue(BedBlock.OCCUPIED)) {
                player.displayClientMessage(new TranslationTextComponent("text.vampirism.coffin.occupied"), true);
                return ActionResultType.CONSUME;
            } else if (state.getValue(CLOSED)) {
                player.displayClientMessage(new TranslationTextComponent("text.vampirism.coffin.closed"), true);
                return ActionResultType.CONSUME;
            }

            if (!BedBlock.canSetSpawn(worldIn)) {
                worldIn.removeBlock(pos, false);
                BlockPos blockpos = pos.relative(state.getValue(VERTICAL)?Direction.DOWN:state.getValue(HORIZONTAL_FACING).getOpposite());
                if (worldIn.getBlockState(blockpos).is(this)) {
                    worldIn.removeBlock(blockpos, false);
                }

                worldIn.explode(null, DamageSource.badRespawnPointExplosion(), null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
                return ActionResultType.CONSUME;
            } else if (state.getValue(BedBlock.OCCUPIED)) {
                player.displayClientMessage(new TranslationTextComponent("text.vampirism.coffin.occupied"), true);
                return ActionResultType.CONSUME;
            } else {
                final BlockPos finalPos = pos;
                BlockState finalState = state;
                player.startSleepInBed(pos).ifLeft(sleepResult1 -> {
                    if (sleepResult1 != null) {
                        player.displayClientMessage(sleepResults.getOrDefault(sleepResult1, sleepResult1.getMessage()), true);
                    }
                }).ifRight(u -> {
                    setCoffinSleepPosition(player, finalPos, finalState);
                });
                return ActionResultType.CONSUME;
            }
        }
    }

    public static void setCoffinSleepPosition(PlayerEntity player, BlockPos blockPos, BlockState state) {
        if (state.getValue(VERTICAL)) {
            player.setPose(Pose.STANDING);
            double x;
            double z;
            switch (state.getValue(HORIZONTAL_FACING)){
                case NORTH:
                    x = 0.5;
                    z = 0.3;
                    player.yBodyRot = player.yHeadRot = 0;
                    break;
                case EAST:
                    x = 0.7;
                    z = 0.5;
                    player.yBodyRot = player.yHeadRot = 90;
                    break;
                case SOUTH:
                    x= 0.5;
                    z=0.7;
                    player.yBodyRot = player.yHeadRot = 180;
                    break;
                case WEST:
                    x=0.3;
                    z=0.5;
                    player.yBodyRot = player.yHeadRot = 270;
                    break;
                default:
                    return;
            }
            player.setPos(blockPos.getX() + x, blockPos.getY()-1, blockPos.getZ()+z);
            player.setBoundingBox(new AxisAlignedBB(blockPos.getX()+x-0.2, blockPos.getY()-0.8, blockPos.getZ()+z-0.2, blockPos.getX()+x+0.2, blockPos.getY()+0.4 , blockPos.getZ()+z+0.2));
        } else {
            player.setPos(blockPos.getX() + 0.5D, blockPos.getY() + 0.2D, blockPos.getZ() + 0.5D);
            player.setBoundingBox(player.dimensions.makeBoundingBox(blockPos.getX() + 0.5D, blockPos.getY() + 0.2D, blockPos.getZ() + 0.5D).deflate(0.3));
        }
    }

    public BlockPos getOtherPos(BlockPos pos, BlockState state) {
        if (state.getValue(VERTICAL)) {
            if (state.getValue(PART) == CoffinPart.FOOT) {
                return pos.above();
            } else {
                return pos.below();
            }
        } else {
            if (state.getValue(PART) == CoffinPart.FOOT) {
                return pos.relative(state.getValue(HorizontalBlock.FACING));
            } else {
                return pos.relative(state.getValue(HorizontalBlock.FACING).getOpposite());
            }
        }
    }

    @Override
    public void setBedOccupied(BlockState state, World world, BlockPos pos, LivingEntity sleeper, boolean occupied) {
        super.setBedOccupied(state, world, pos, sleeper, occupied);
        world.setBlock(pos, world.getBlockState(pos).setValue(CLOSED, occupied), 3);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, BedBlock.OCCUPIED, PART, CLOSED, VERTICAL);
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

    public static class ShapeTable  {

        private final VoxelShape[][][][] shapes;
        public ShapeTable() {
            this.shapes = buildShapes();
        }

        public VoxelShape getShape(CoffinBlock.CoffinPart part, boolean closed, boolean vertical, Direction facing) {
            return shapes[part.ordinal()][closed?1:0][vertical?1:0][facing.get2DDataValue()];
        }

        private VoxelShape[][][][] buildShapes() {
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
            lidShape = VoxelShapes.join(lidShape, VoxelShapes.box(0.09375, 0.625, 0.0625, 0.90625, 0.6875, 1.921875), IBooleanFunction.OR);

            VoxelShape head = VoxelShapes.join(shape, VoxelShapes.box(0,0,0,1,1,1), IBooleanFunction.AND);
            VoxelShape foot = VoxelShapes.join(shape, VoxelShapes.box(0,0,1,1,1,2), IBooleanFunction.AND).move(0,0,-1);
            VoxelShape lidHead = VoxelShapes.join(lidShape, VoxelShapes.box(0,0,0,1,1,1), IBooleanFunction.AND);
            VoxelShape lidFoot = VoxelShapes.join(lidShape, VoxelShapes.box(0,0,1,1,1,2), IBooleanFunction.AND).move(0,0,-1);

            VoxelShape[][][][] shapes = new VoxelShape[2][][][];
            shapes[CoffinPart.HEAD.ordinal()] = buildShapePart(head, lidHead);
            shapes[CoffinPart.FOOT.ordinal()] = buildShapePart(foot, lidFoot);
            return shapes;
        }
        private VoxelShape[][][] buildShapePart(VoxelShape shape, VoxelShape shapeLid) {
            VoxelShape[][][] shapes = new VoxelShape[2][][];
            shapes[0] = buildShapeClosed(shape, shapeLid, false);
            shapes[1] = buildShapeClosed(shape, shapeLid, true);
            return shapes;
        }
        private VoxelShape[][] buildShapeClosed(VoxelShape shape, VoxelShape shapeLid, boolean closed) {
            if (closed) {
                shape = VoxelShapes.or(shape, shapeLid);
            }
            VoxelShape[][] shapes = new VoxelShape[2][];
            shapes[0] = buildShapeVertical(shape,false);
            shapes[1] = buildShapeVertical(shape, true);
            return shapes;
        }
        private VoxelShape[] buildShapeVertical(VoxelShape shape, boolean vertical) {
            if (vertical) {
                shape = UtilLib.rollShape(shape, Direction.NORTH);
            }
            VoxelShape[] shapes = new VoxelShape[4];
            VoxelShape finalShape = shape;
            Direction.Plane.HORIZONTAL.stream().forEach(dir -> {
                shapes[dir.get2DDataValue()] = buildShapeDirectional(finalShape, dir);
            });
            return shapes;
        }
        private VoxelShape buildShapeDirectional(VoxelShape shape, Direction direction){
            switch (direction){
                case NORTH:
                    return shape;
                case EAST:
                    return UtilLib.rotateShape(shape, UtilLib.RotationAmount.NINETY);
                case SOUTH:
                    return UtilLib.rotateShape(shape, UtilLib.RotationAmount.HUNDRED_EIGHTY);
                case WEST:
                    return UtilLib.rotateShape(shape, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);
                default:
                    throw new IllegalArgumentException("Wrong direction argument");
            }
        }
    }
}
