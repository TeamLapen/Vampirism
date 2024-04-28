package de.teamlapen.vampirism.blocks;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.blockentity.CoffinBlockEntity;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.mixin.accessor.EntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class CoffinBlock extends VampirismBlockContainer {
    public static final MapCodec<CoffinBlock> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    DyeColor.CODEC.fieldOf("color").forGetter(b -> b.color),
                    propertiesCodec()
            ).apply(inst, CoffinBlock::new)
    );
    public static final Map<DyeColor, CoffinBlock> COFFIN_BLOCKS = new HashMap<>();
    public static final String name = "coffin";
    public static final EnumProperty<CoffinPart> PART = EnumProperty.create("part", CoffinPart.class);
    public static final BooleanProperty CLOSED = BooleanProperty.create("closed");
    public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");
    private static final ShapeTable shapes = new ShapeTable();
    private static final Map<Player.BedSleepingProblem, Component> sleepResults = ImmutableMap.of(Player.BedSleepingProblem.NOT_POSSIBLE_NOW, Component.translatable("text.vampirism.coffin.no_sleep"), Player.BedSleepingProblem.TOO_FAR_AWAY, Component.translatable("text.vampirism.coffin.too_far_away"), Player.BedSleepingProblem.OBSTRUCTED, Component.translatable("text.vampirism.coffin.obstructed"));

    public static boolean isOccupied(@NotNull BlockGetter world, @NotNull BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof CoffinBlock && state.getValue(BedBlock.OCCUPIED);
    }

    public static boolean isClosed(@NotNull BlockGetter world, @NotNull BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof CoffinBlock && state.getValue(CLOSED);
    }

    public static boolean isHead(@NotNull BlockGetter world, @NotNull BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof CoffinBlock && state.getValue(PART) == CoffinPart.HEAD;
    }

    private static Direction getDirectionToOther(CoffinPart type, @NotNull Direction facing) {
        return type == CoffinPart.FOOT ? facing : facing.getOpposite();
    }

    private final DyeColor color;

    public CoffinBlock(DyeColor color) {
        this(color, Properties.of().mapColor(MapColor.WOOD).strength(0.2f).noOcclusion().pushReaction(PushReaction.DESTROY).ignitedByLava());
    }

    public CoffinBlock(DyeColor color, Block.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(BedBlock.OCCUPIED, Boolean.FALSE).setValue(PART, CoffinPart.FOOT).setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(CLOSED, false).setValue(VERTICAL, false));
        this.color = color;
        COFFIN_BLOCKS.put(color, this);
    }

    @Override
    public @NotNull Direction getBedDirection(@NotNull BlockState state, LevelReader world, BlockPos pos) {
        return state.getValue(HORIZONTAL_FACING);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Direction enumfacing = context.getHorizontalDirection();
        boolean vertical = context.getClickedFace().getAxis() != Direction.Axis.Y;
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(vertical ? Direction.UP : enumfacing);
        return context.getLevel().getBlockState(blockpos1).canBeReplaced(context) ? this.defaultBlockState().setValue(HORIZONTAL_FACING, enumfacing).setValue(VERTICAL, vertical) : null;
    }

    @Override
    protected boolean isPathfindable(BlockState p_60475_, PathComputationType p_60478_) {
        return false;
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return shapes.getShape(state.getValue(PART), state.getValue(CLOSED), state.getValue(VERTICAL), state.getValue(HORIZONTAL_FACING));
    }

    @Override
    public boolean isBed(@NotNull BlockState state, BlockGetter world, BlockPos pos, Entity player) {
        return !state.getValue(CLOSED) || state.getValue(BedBlock.OCCUPIED);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CoffinBlockEntity(pos, state, this.color);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(@NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        //If in creative mode, also destroy the head block. Otherwise, it will be destroyed due to updateShape and an item will drop
        if (!worldIn.isClientSide && player.isCreative()) {
            CoffinPart part = state.getValue(PART);
            if (part == CoffinPart.FOOT) {
                BlockPos blockpos = getOtherPos(pos, state);
                BlockState blockstate = worldIn.getBlockState(blockpos);
                if (blockstate.getBlock() == this && blockstate.getValue(PART) == CoffinPart.HEAD) {
                    worldIn.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    worldIn.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }

        return super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public void setPlacedBy(@NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack itemStack) {
        super.setPlacedBy(worldIn, pos, state, entity, itemStack);
        if (!worldIn.isClientSide) {
            BlockPos blockpos = getOtherPos(pos, state);
            worldIn.setBlock(blockpos, state.setValue(PART, CoffinPart.HEAD), 3);
            worldIn.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(worldIn, pos, 3);
        }
    }

    @NotNull
    @Override
    public BlockState updateShape(@NotNull BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor worldIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (facing == getDirectionToOther(stateIn.getValue(PART), stateIn.getValue(VERTICAL) ? Direction.UP : stateIn.getValue(HORIZONTAL_FACING))) {
            return facingState.getBlock() == this && facingState.getValue(PART) != stateIn.getValue(PART) ? stateIn.setValue(BedBlock.OCCUPIED, facingState.getValue(BedBlock.OCCUPIED)) : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @NotNull
    @Override
    public InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {

        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            if (state.getValue(PART) != CoffinPart.HEAD) {
                pos = getOtherPos(pos, state);
                state = worldIn.getBlockState(pos);
                if (!state.is(this)) {
                    return InteractionResult.CONSUME;
                }
            }
            player.awardStat(ModStats.INTERACT_WITH_COFFIN.get());
            if (player.isShiftKeyDown() && !state.getValue(BedBlock.OCCUPIED)) {
                worldIn.setBlock(pos, state.setValue(CLOSED, !state.getValue(CLOSED)), 3);
                BlockPos otherPos = getOtherPos(pos, state);
                worldIn.setBlock(otherPos, worldIn.getBlockState(otherPos).setValue(CLOSED, !state.getValue(CLOSED)), 3);
                return InteractionResult.CONSUME;
            } else if (VampirismPlayerAttributes.get(player).vampireLevel == 0) {
                player.displayClientMessage(Component.translatable("text.vampirism.coffin.cant_use"), true);
                return InteractionResult.CONSUME;
            } else if (state.getValue(BedBlock.OCCUPIED)) {
                player.displayClientMessage(Component.translatable("text.vampirism.coffin.occupied"), true);
                return InteractionResult.CONSUME;
            } else if (state.getValue(CLOSED)) {
                player.displayClientMessage(Component.translatable("text.vampirism.coffin.closed"), true);
                return InteractionResult.CONSUME;
            }

            if (!BedBlock.canSetSpawn(worldIn)) {
                worldIn.removeBlock(pos, false);
                BlockPos blockpos = pos.relative(state.getValue(VERTICAL) ? Direction.DOWN : state.getValue(HORIZONTAL_FACING).getOpposite());
                if (worldIn.getBlockState(blockpos).is(this)) {
                    worldIn.removeBlock(blockpos, false);
                }
                Vec3 vec3 = pos.getCenter();
                worldIn.explode(null, worldIn.damageSources().badRespawnPointExplosion(vec3), null, vec3, 5.0F, true, Level.ExplosionInteraction.BLOCK);
                return InteractionResult.CONSUME;
            } else if (state.getValue(BedBlock.OCCUPIED)) {
                player.displayClientMessage(Component.translatable("text.vampirism.coffin.occupied"), true);
                return InteractionResult.CONSUME;
            } else {
                final BlockPos finalPos = pos;
                BlockState finalState = state;
                player.startSleepInBed(pos).ifLeft(sleepResult1 -> {
                    if (sleepResult1 != null) {
                        player.displayClientMessage(sleepResults.getOrDefault(sleepResult1, sleepResult1.getMessage()), true);
                    }
                }).ifRight(u -> setCoffinSleepPosition(player, finalPos, finalState));
                return InteractionResult.CONSUME;
            }
        }
    }

    public static void setCoffinSleepPosition(@NotNull Player player, @NotNull BlockPos blockPos, @NotNull BlockState state) {
        if (state.getValue(VERTICAL)) {
            player.setPose(Pose.STANDING);
            double x;
            double z;
            switch (state.getValue(HORIZONTAL_FACING)) {
                case NORTH -> {
                    x = 0.5;
                    z = 0.3;
                    player.yBodyRot = player.yHeadRot = 0;
                }
                case EAST -> {
                    x = 0.7;
                    z = 0.5;
                    player.yBodyRot = player.yHeadRot = 90;
                }
                case SOUTH -> {
                    x = 0.5;
                    z = 0.7;
                    player.yBodyRot = player.yHeadRot = 180;
                }
                case WEST -> {
                    x = 0.3;
                    z = 0.5;
                    player.yBodyRot = player.yHeadRot = 270;
                }
                default -> {
                    return;
                }
            }
            player.setPos(blockPos.getX() + x, blockPos.getY() - 1, blockPos.getZ() + z);
            player.setBoundingBox(new AABB(blockPos.getX() + x - 0.2, blockPos.getY() - 0.8, blockPos.getZ() + z - 0.2, blockPos.getX() + x + 0.2, blockPos.getY() + 0.4, blockPos.getZ() + z + 0.2));
        } else {
            player.setPos(blockPos.getX() + 0.5D, blockPos.getY() + 0.2D, blockPos.getZ() + 0.5D);
            player.setBoundingBox(((EntityAccessor) player).getDimensions().makeBoundingBox(blockPos.getX() + 0.5D, blockPos.getY() + 0.2D, blockPos.getZ() + 0.5D).deflate(0.3));
        }
    }

    public @NotNull BlockPos getOtherPos(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (state.getValue(VERTICAL)) {
            if (state.getValue(PART) == CoffinPart.FOOT) {
                return pos.above();
            } else {
                return pos.below();
            }
        } else {
            if (state.getValue(PART) == CoffinPart.FOOT) {
                return pos.relative(state.getValue(HORIZONTAL_FACING));
            } else {
                return pos.relative(state.getValue(HORIZONTAL_FACING).getOpposite());
            }
        }
    }

    @Override
    public void setBedOccupied(BlockState state, @NotNull Level world, @NotNull BlockPos pos, LivingEntity sleeper, boolean occupied) {
        super.setBedOccupied(state, world, pos, sleeper, occupied);
        world.setBlock(pos, world.getBlockState(pos).setValue(CLOSED, occupied), 3);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, BedBlock.OCCUPIED, PART, CLOSED, VERTICAL);
    }

    public DyeColor getColor() {
        return color;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntity) {
        return state.getValue(PART) == CoffinPart.HEAD ? createTickerHelper(blockEntity, ModTiles.COFFIN.get(), CoffinBlockEntity::clientTickHead) : null;
    }

    public enum CoffinPart implements StringRepresentable {
        HEAD("head"),
        FOOT("foot");

        private final String name;

        CoffinPart(String name) {
            this.name = name;
        }

        @NotNull
        public String getSerializedName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }
    }

    public static class ShapeTable {

        private final VoxelShape[][][] @NotNull [] shapes;

        public ShapeTable() {
            this.shapes = buildShapes();
        }

        public VoxelShape getShape(CoffinBlock.@NotNull CoffinPart part, boolean closed, boolean vertical, @NotNull Direction facing) {
            return shapes[part.ordinal()][closed ? 1 : 0][vertical ? 1 : 0][facing.get2DDataValue()];
        }

        private VoxelShape[][][] @NotNull [] buildShapes() {
            VoxelShape shape = Shapes.empty();
            shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.0625, 2), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.90625, 0.0625, 0.046875, 0.96875, 0.1875, 1.96875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.875, 0.1875, 1.375, 0.9375, 0.375, 1.875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.875, 0.1875, 0.75, 0.9375, 0.375, 1.25), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.875, 0.1875, 0.125, 0.9375, 0.375, 0.625), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.25, 0.1875, 1.875, 0.75, 0.375, 1.9375), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.0625, 0.1875, 0.125, 0.125, 0.375, 0.625), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.0625, 0.1875, 0.75, 0.125, 0.375, 1.25), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.0625, 0.1875, 1.375, 0.125, 0.375, 1.875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.25, 0.1875, 0.0625, 0.75, 0.375, 0.125), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.03125, 0.0625, 0.046875, 0.09375, 0.1875, 1.96875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.09375, 0.0625, 1.921875, 0.90625, 0.1875, 1.96875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.09375, 0.0625, 0.046875, 0.90625, 0.1875, 0.09375), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.90625, 0.1875, 1.875, 0.96875, 0.375, 1.96875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.90625, 0.1875, 1.25, 0.96875, 0.375, 1.375), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.90625, 0.1875, 0.625, 0.96875, 0.375, 0.75), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.90625, 0.1875, 0.046875, 0.96875, 0.375, 0.125), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.03125, 0.1875, 0.046875, 0.09375, 0.375, 0.125), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.03125, 0.1875, 0.625, 0.09375, 0.375, 0.75), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.03125, 0.1875, 1.25, 0.09375, 0.375, 1.375), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.03125, 0.1875, 1.875, 0.09375, 0.375, 1.96875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.09375, 0.1875, 1.921875, 0.25, 0.375, 1.96875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.75, 0.1875, 1.921875, 0.90625, 0.375, 1.96875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.75, 0.1875, 0.046875, 0.90625, 0.375, 0.09375), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.09375, 0.1875, 0.046875, 0.25, 0.375, 0.09375), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.09375, 0.375, 0.046875, 0.90625, 0.5, 0.09375), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.03125, 0.375, 0.046875, 0.09375, 0.5, 1.96875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.09375, 0.375, 1.921875, 0.90625, 0.5, 1.96875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.90625, 0.375, 0.046875, 0.96875, 0.5, 1.96875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.90625, 0.5, 0, 1, 0.5625, 2), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0, 0.5, 0, 0.09375, 0.5625, 2), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.09375, 0.5, 1.921875, 0.90625, 0.5625, 2), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.09375, 0.5, 0, 0.90625, 0.5625, 0.09375), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.09375, 0.0625, 0.09375, 0.140625, 0.546875, 1.921875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.859375, 0.0625, 0.09375, 0.90625, 0.546875, 1.921875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.140625, 0.0625, 1.859375, 0.859375, 0.546875, 1.921875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.140625, 0.0625, 0.09375, 0.859375, 0.546875, 0.15625), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.1875, 0.125, 0.1875, 0.8125, 0.3125, 0.5), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.203125, 0.203125, 0.4375, 0.796875, 0.34375, 0.5625), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.203125, 0.203125, 0.171875, 0.796875, 0.328125, 0.296875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.9375, 0.09375, 0.25, 1, 0.15625, 0.5), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.9375, 0.09375, 0.875, 1, 0.15625, 1.125), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.9375, 0.09375, 1.5, 1, 0.15625, 1.75), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0, 0.09375, 1.5, 0.0625, 0.15625, 1.75), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0, 0.09375, 0.875, 0.0625, 0.15625, 1.125), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0, 0.09375, 0.25, 0.0625, 0.15625, 0.5), BooleanOp.OR);

            VoxelShape lidShape = Shapes.empty();
            lidShape = Shapes.join(lidShape, Shapes.box(0, 0.5625, 0, 0.09375, 0.625, 2), BooleanOp.OR);
            lidShape = Shapes.join(lidShape, Shapes.box(0.09375, 0.5625, 0, 0.90625, 0.625, 0.078125), BooleanOp.OR);
            lidShape = Shapes.join(lidShape, Shapes.box(0.90625, 0.5625, 0, 1, 0.625, 2), BooleanOp.OR);
            lidShape = Shapes.join(lidShape, Shapes.box(0.09375, 0.5625, 1.90625, 0.90625, 0.625, 2), BooleanOp.OR);
            lidShape = Shapes.join(lidShape, Shapes.box(0.09375, 0.625, 0.0625, 0.90625, 0.6875, 1.921875), BooleanOp.OR);

            VoxelShape head = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 1, 1), BooleanOp.AND);
            VoxelShape foot = Shapes.join(shape, Shapes.box(0, 0, 1, 1, 1, 2), BooleanOp.AND).move(0, 0, -1);
            VoxelShape lidHead = Shapes.join(lidShape, Shapes.box(0, 0, 0, 1, 1, 1), BooleanOp.AND);
            VoxelShape lidFoot = Shapes.join(lidShape, Shapes.box(0, 0, 1, 1, 1, 2), BooleanOp.AND).move(0, 0, -1);

            VoxelShape[][][][] shapes = new VoxelShape[2][][][];
            shapes[CoffinPart.HEAD.ordinal()] = buildShapePart(head, lidHead);
            shapes[CoffinPart.FOOT.ordinal()] = buildShapePart(foot, lidFoot);
            return shapes;
        }

        private VoxelShape[][] @NotNull [] buildShapePart(VoxelShape shape, @NotNull VoxelShape shapeLid) {
            VoxelShape[][][] shapes = new VoxelShape[2][][];
            shapes[0] = buildShapeClosed(shape, shapeLid, false);
            shapes[1] = buildShapeClosed(shape, shapeLid, true);
            return shapes;
        }

        private VoxelShape[] @NotNull [] buildShapeClosed(VoxelShape shape, @NotNull VoxelShape shapeLid, boolean closed) {
            if (closed) {
                shape = Shapes.or(shape, shapeLid);
            }
            VoxelShape[][] shapes = new VoxelShape[2][];
            shapes[0] = buildShapeVertical(shape, false);
            shapes[1] = buildShapeVertical(shape, true);
            return shapes;
        }

        private VoxelShape @NotNull [] buildShapeVertical(VoxelShape shape, boolean vertical) {
            if (vertical) {
                shape = UtilLib.rollShape(shape, Direction.NORTH);
            }
            VoxelShape[] shapes = new VoxelShape[4];
            VoxelShape finalShape = shape;
            Direction.Plane.HORIZONTAL.stream().forEach(dir -> shapes[dir.get2DDataValue()] = buildShapeDirectional(finalShape, dir));
            return shapes;
        }

        private VoxelShape buildShapeDirectional(@NotNull VoxelShape shape, @NotNull Direction direction) {
            return switch (direction) {
                case NORTH -> shape;
                case EAST -> UtilLib.rotateShape(shape, UtilLib.RotationAmount.NINETY);
                case SOUTH -> UtilLib.rotateShape(shape, UtilLib.RotationAmount.HUNDRED_EIGHTY);
                case WEST -> UtilLib.rotateShape(shape, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);
                default -> throw new IllegalArgumentException("Wrong direction argument");
            };
        }
    }
}
