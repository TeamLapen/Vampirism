package de.teamlapen.vampirism.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.blockentity.CoffinBlockEntity;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class CoffinBlock extends VampirismBlockContainer {
    public static final EnumProperty<CoffinPart> PART = EnumProperty.create("part", CoffinPart.class);
    public static final BooleanProperty CLOSED = BooleanProperty.create("closed");
    private static final Table<Direction, Boolean, VoxelShape> shapes;
    private static final Map<Player.BedSleepingProblem, Component> sleepResults = ImmutableMap.of(Player.BedSleepingProblem.NOT_POSSIBLE_NOW, Component.translatable("text.vampirism.coffin.no_sleep"), Player.BedSleepingProblem.TOO_FAR_AWAY, Component.translatable("text.vampirism.coffin.too_far_away"), Player.BedSleepingProblem.OBSTRUCTED, Component.translatable("text.vampirism.coffin.obstructed"));

    public static boolean isOccupied(BlockGetter world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof CoffinBlock && state.getValue(BedBlock.OCCUPIED);
    }

    public static boolean isClosed(BlockGetter world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof CoffinBlock && state.getValue(CLOSED);
    }

    public static boolean isHead(BlockGetter world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof CoffinBlock && state.getValue(PART) == CoffinPart.HEAD;
    }

    private static Direction getDirectionToOther(CoffinPart type, Direction facing) {
        return type == CoffinPart.FOOT ? facing : facing.getOpposite();
    }

    static {
        VoxelShape bottom = Block.box(0,0,0,16,1,16);
        VoxelShape lid = Block.box(0,12,0,16,13,16);

        VoxelShape w = Block.box(0,0,0,1,13,16);
        VoxelShape s = Block.box(0,0,15,16,13,16);
        VoxelShape e = Block.box(15,0,0,16,13,16);
        VoxelShape n = Block.box(0,0,0,16,13,1);

        VoxelShape west = Shapes.or(bottom, n, s, e);
        VoxelShape south = Shapes.or(bottom, w, n, e);
        VoxelShape east = Shapes.or(bottom, w, s, n);
        VoxelShape north = Shapes.or(bottom, w, s, e);

        ImmutableTable.Builder<Direction, Boolean, VoxelShape> shapeBuilder = ImmutableTable.builder();
        shapeBuilder.put(Direction.WEST, false, west);
        shapeBuilder.put(Direction.SOUTH, false, south);
        shapeBuilder.put(Direction.NORTH, false, north);
        shapeBuilder.put(Direction.EAST, false, east);
        shapeBuilder.put(Direction.WEST, true, Shapes.or(west, lid));
        shapeBuilder.put(Direction.SOUTH, true, Shapes.or(south, lid));
        shapeBuilder.put(Direction.NORTH, true, Shapes.or(north, lid));
        shapeBuilder.put(Direction.EAST, true, Shapes.or(east, lid));
        shapes = shapeBuilder.build();
    }

    public CoffinBlock() {
        super(Properties.of(Material.WOOD).strength(0.2f).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(BedBlock.OCCUPIED, Boolean.FALSE).setValue(PART, CoffinPart.FOOT).setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(CLOSED, false));

    }

    @Override
    public Direction getBedDirection(BlockState state, LevelReader world, BlockPos pos) {
        return state.getValue(HORIZONTAL_FACING);
    }

    @Nonnull
    @Override
    public PushReaction getPistonPushReaction(@Nonnull BlockState state) {
        return PushReaction.DESTROY;
    }

    @Nonnull
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction enumfacing = context.getHorizontalDirection();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(enumfacing);
        return context.getLevel().getBlockState(blockpos1).canBeReplaced(context) ? this.defaultBlockState().setValue(HORIZONTAL_FACING, enumfacing) : null;
    }

    @Override
    public boolean isPathfindable(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull PathComputationType type) {
        return false;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        Direction facing = state.getValue(HORIZONTAL_FACING);
        return shapes.get(state.getValue(PART) == CoffinPart.FOOT ? facing:facing.getOpposite(), state.getValue(CLOSED));
    }


    @Override
    public boolean isBed(BlockState state, BlockGetter world, BlockPos pos, Entity player) {
        return !state.getValue(CLOSED) || state.getValue(BedBlock.OCCUPIED);
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new CoffinBlockEntity(pos, state);
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, @Nonnull Player player) {
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
    public void setPlacedBy(@Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity entity, @Nonnull ItemStack itemStack) {
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
    public BlockState updateShape(BlockState stateIn, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor worldIn, @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
        if (facing == getDirectionToOther(stateIn.getValue(PART), stateIn.getValue(HORIZONTAL_FACING))) {
            return facingState.getBlock() == this && facingState.getValue(PART) != stateIn.getValue(PART) ? stateIn.setValue(BedBlock.OCCUPIED, facingState.getValue(BedBlock.OCCUPIED)) : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, Level worldIn, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {

        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack heldItem = player.getItemInHand(hand);
            if (!heldItem.isEmpty()) {
                DyeColor color = heldItem.getItem() instanceof DyeItem ? ((DyeItem) heldItem.getItem()).getDyeColor() : UtilLib.getColorForItem(heldItem.getItem());
                if (color != null) {
                    BlockEntity tile = worldIn.getBlockEntity(pos);
                    BlockEntity other = state.getValue(PART) == CoffinPart.HEAD ? worldIn.getBlockEntity(pos.relative(state.getValue(HORIZONTAL_FACING).getOpposite())) : worldIn.getBlockEntity(pos.relative(state.getValue(HORIZONTAL_FACING)));
                    if (!(tile instanceof CoffinBlockEntity) || !(other instanceof CoffinBlockEntity)) {
                        return InteractionResult.SUCCESS;
                    }
                    ((CoffinBlockEntity) tile).changeColor(color);
                    ((CoffinBlockEntity) other).changeColor(color);
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            if (state.getValue(PART) != CoffinPart.HEAD) {
                pos = pos.relative(state.getValue(HORIZONTAL_FACING));
                state = worldIn.getBlockState(pos);
                if (!state.is(this)) {
                    return InteractionResult.SUCCESS;
                }
            }

            if (player.isShiftKeyDown() && !state.getValue(BedBlock.OCCUPIED)) {
                worldIn.setBlock(pos, state.setValue(CLOSED, !state.getValue(CLOSED)), 3);
                return InteractionResult.SUCCESS;
            } else if (VampirismPlayerAttributes.get(player).vampireLevel == 0) {
                player.displayClientMessage(Component.translatable("text.vampirism.coffin.cant_use"), true);
                return InteractionResult.SUCCESS;
            } else if (state.getValue(BedBlock.OCCUPIED)) {
                player.displayClientMessage(Component.translatable("text.vampirism.coffin.occupied"), true);
                return InteractionResult.SUCCESS;
            } else if (state.getValue(CLOSED)) {
                player.displayClientMessage(Component.translatable("text.vampirism.coffin.closed"), true);
                return InteractionResult.SUCCESS;
            }

            if (!BedBlock.canSetSpawn(worldIn)) {
                worldIn.removeBlock(pos, false);
                BlockPos blockpos = pos.relative(state.getValue(HORIZONTAL_FACING).getOpposite());
                if (worldIn.getBlockState(blockpos).is(this)) {
                    worldIn.removeBlock(blockpos, false);
                }

                worldIn.explode(null, DamageSource.badRespawnPointExplosion(), null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 5.0F, true, Explosion.BlockInteraction.DESTROY);
                return InteractionResult.SUCCESS;
            } else if (state.getValue(BedBlock.OCCUPIED)) {
                player.displayClientMessage(Component.translatable("text.vampirism.coffin.occupied"), true);
                return InteractionResult.SUCCESS;
            } else {
                final BlockPos finalPos = pos;
                player.startSleepInBed(pos).ifLeft(sleepResult1 -> {
                    if (sleepResult1 != null) {
                        player.displayClientMessage(sleepResults.getOrDefault(sleepResult1, sleepResult1.getMessage()), true);
                    }
                }).ifRight(u -> {
                    setCoffinSleepPosition(player, finalPos);
                });
                return InteractionResult.SUCCESS;
            }
        }
    }

    public static void setCoffinSleepPosition(Player player, BlockPos blockPos) {
        player.setPos(blockPos.getX() + 0.5D, blockPos.getY() + 0.1D, blockPos.getZ() + 0.5D);
    }

    @Override
    public void setBedOccupied(BlockState state, Level world, BlockPos pos, LivingEntity sleeper, boolean occupied) {
        super.setBedOccupied(state, world, pos, sleeper, occupied);
        world.setBlock(pos, world.getBlockState(pos).setValue(CLOSED, occupied), 3);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, BedBlock.OCCUPIED, PART, CLOSED);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> blockEntity) {
        return level.isClientSide() && state.getValue(PART) == CoffinPart.HEAD ? createTickerHelper(blockEntity, ModTiles.COFFIN.get(), CoffinBlockEntity::clientTickHead) : null;
    }

    public enum CoffinPart implements StringRepresentable {
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
