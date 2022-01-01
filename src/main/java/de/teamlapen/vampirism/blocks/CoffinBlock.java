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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Block coffin.
 */
public class CoffinBlock extends VampirismBlockContainer {

    public static final String name = "coffin";
    public static final EnumProperty<CoffinPart> PART = EnumProperty.create("part", CoffinPart.class);
    private static final VoxelShape shape = makeShape();
    private static final Map<PlayerEntity.SleepResult, ITextComponent> sleepResults = ImmutableMap.of(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW, new TranslationTextComponent("text.vampirism.coffin.no_sleep"), PlayerEntity.SleepResult.TOO_FAR_AWAY, new TranslationTextComponent("text.vampirism.coffin.too_far_away"), PlayerEntity.SleepResult.OBSTRUCTED, new TranslationTextComponent("text.vampirism.coffin.obstructed"));

    public static boolean isOccupied(IBlockReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof CoffinBlock && state.getValue(BedBlock.OCCUPIED);
    }


    public static boolean isHead(IBlockReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof CoffinBlock && state.getValue(PART) == CoffinPart.HEAD;
    }

    private static Direction getDirectionToOther(CoffinPart type, Direction facing) {
        return type == CoffinPart.FOOT ? facing : facing.getOpposite();
    }

    private static VoxelShape makeShape() {
        return Block.box(0, 0, 0, 16, 13, 16);
    }

    public CoffinBlock() {
        super(name, Properties.of(Material.WOOD).strength(0.2f).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(BedBlock.OCCUPIED, Boolean.FALSE).setValue(PART, CoffinPart.FOOT).setValue(HORIZONTAL_FACING, Direction.NORTH));

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
        return shape;
    }


    @Override
    public boolean isBed(BlockState state, IBlockReader world, BlockPos pos, Entity player) {
        return true;
    }

    @Override
    public TileEntity newBlockEntity(@Nonnull IBlockReader worldIn) {
        return new CoffinTileEntity();
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
            ItemStack heldItem = player.getItemInHand(hand);
            if (!heldItem.isEmpty()) {
                DyeColor color = heldItem.getItem() instanceof DyeItem ? ((DyeItem) heldItem.getItem()).getDyeColor() : UtilLib.getColorForItem(heldItem.getItem());
                if (color != null) {
                    TileEntity tile = worldIn.getBlockEntity(pos);
                    TileEntity other = state.getValue(PART) == CoffinPart.HEAD ? worldIn.getBlockEntity(pos.relative(state.getValue(HORIZONTAL_FACING).getOpposite())) : worldIn.getBlockEntity(pos.relative(state.getValue(HORIZONTAL_FACING)));
                    if (!(tile instanceof CoffinTileEntity) || !(other instanceof CoffinTileEntity)) {
                        return ActionResultType.SUCCESS;
                    }
                    ((CoffinTileEntity) tile).changeColor(color);
                    ((CoffinTileEntity) other).changeColor(color);
                    if (!player.abilities.instabuild) {
                        heldItem.shrink(1);
                    }
                    return ActionResultType.SUCCESS;
                }
            }
            if (state.getValue(PART) != CoffinPart.HEAD) {
                pos = pos.relative(state.getValue(HORIZONTAL_FACING));
                state = worldIn.getBlockState(pos);
                if (!state.is(this)) {
                    return ActionResultType.SUCCESS;
                }
            }

            if (VampirismPlayerAttributes.get(player).vampireLevel == 0) {
                player.displayClientMessage(new TranslationTextComponent("text.vampirism.coffin.cant_use"), true);
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
                    BlockState blockstate = worldIn.getBlockState(finalPos);
                    if (blockstate.getBlock() instanceof CoffinBlock) {
                        worldIn.setBlock(finalPos, blockstate.setValue(BedBlock.OCCUPIED, Boolean.TRUE), 3);
                    }
                });
                return ActionResultType.SUCCESS;
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, BedBlock.OCCUPIED, PART);
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
