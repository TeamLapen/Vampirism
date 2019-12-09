package de.teamlapen.vampirism.blocks;

import com.google.common.collect.ImmutableMap;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.tileentity.CoffinTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.common.extensions.IForgeDimension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
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
        return state.getBlock() instanceof CoffinBlock && state.get(BedBlock.OCCUPIED);
    }


    public static boolean isHead(IBlockReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof CoffinBlock && state.get(PART) == CoffinPart.HEAD;
    }

    private static Direction getDirectionToOther(CoffinPart type, Direction facing) {
        return type == CoffinPart.FOOT ? facing : facing.getOpposite();
    }

    private static VoxelShape makeShape() {
        return Block.makeCuboidShape(0, 0, 0, 16, 13, 16);
    }

    public CoffinBlock() {
        super(name, Properties.create(Material.WOOD).hardnessAndResistance(0.2f));
        this.setDefaultState(this.getStateContainer().getBaseState().with(BedBlock.OCCUPIED, Boolean.FALSE).with(PART, CoffinPart.FOOT).with(HORIZONTAL_FACING, Direction.NORTH));

    }

    @Override
    public boolean allowsMovement(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull IBlockReader worldIn) {
        return new CoffinTileEntity();
    }

    @Override
    public Direction getBedDirection(BlockState state, IWorldReader world, BlockPos pos) {
        return state.get(HORIZONTAL_FACING);
    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(BlockState state, @Nonnull LootContext.Builder builder) {
        return state.get(PART) == CoffinPart.FOOT ? Collections.emptyList() : super.getDrops(state, builder);
    }

    @Nonnull
    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return shape;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction enumfacing = context.getPlacementHorizontalFacing();
        BlockPos blockpos = context.getPos();
        BlockPos blockpos1 = blockpos.offset(enumfacing);
        return context.getWorld().getBlockState(blockpos1).isReplaceable(context) ? this.getDefaultState().with(HORIZONTAL_FACING, enumfacing) : null;
    }

    @Override
    public boolean isBed(BlockState state, IBlockReader world, BlockPos pos, Entity player) {
        return true;
    }

    @Override
    public boolean isBedFoot(BlockState state, IWorldReader world, BlockPos pos) {
        return state.get(PART) == CoffinPart.FOOT;
    }

    @Override
    public boolean isNormalCube(BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        return false;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        Direction enumfacing = state.get(HORIZONTAL_FACING);

        if (state.get(PART) == CoffinPart.HEAD) {
            if (worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock() != this) {
                worldIn.removeBlock(pos, isMoving);
            }
        } else if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() != this) {
            worldIn.removeBlock(pos, isMoving);

            if (!worldIn.isRemote()) {
                Block.spawnDrops(state, worldIn, pos);
            }
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {

        if (worldIn.isRemote) {
            return true;
        } else {
            ItemStack heldItem = player.getHeldItem(hand);
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof DyeItem) {
                CoffinTileEntity tile = (CoffinTileEntity) worldIn.getTileEntity(pos);
                TileEntity other = state.get(PART) == CoffinPart.HEAD ? worldIn.getTileEntity(pos.offset(state.get(HORIZONTAL_FACING).getOpposite())) : worldIn.getTileEntity(pos.offset(state.get(HORIZONTAL_FACING)));
                if (!(other instanceof CoffinTileEntity)) {
                    return true;
                }
                tile.changeColor(((DyeItem) heldItem.getItem()).getDyeColor());
                ((CoffinTileEntity) other).changeColor(((DyeItem) heldItem.getItem()).getDyeColor());
                if (!player.abilities.isCreativeMode) {
                    heldItem.shrink(1);
                }
                return true;
            }
            if (state.get(PART) != CoffinPart.HEAD) {
                pos = pos.offset(state.get(HORIZONTAL_FACING));
                state = worldIn.getBlockState(pos);
                if (state.getBlock() != this) {
                    return true;
                }
            }

            if (VampirePlayer.get(player).getLevel() == 0) {
                player.sendStatusMessage(new TranslationTextComponent("text.vampirism.coffin.cant_use"), true);
                return true;
            }

            IForgeDimension.SleepResult sleepResult = worldIn.dimension.canSleepAt(player, pos);
            if (sleepResult != IForgeDimension.SleepResult.BED_EXPLODES) {
                if (sleepResult == IForgeDimension.SleepResult.DENY) return true;
                if (state.get(BedBlock.OCCUPIED)) {
                    player.sendStatusMessage(new TranslationTextComponent("text.vampirism.coffin.occupied"), true);
                    return true;
                } else {
                    final BlockPos finalPos = pos;
                    player.trySleep(pos).ifLeft(sleepResult1 -> {
                        if (sleepResult1 != null) {
                            player.sendStatusMessage(sleepResults.getOrDefault(sleepResult1, sleepResult1.getMessage()), true);
                        }
                    }).ifRight(u -> {
                        BlockState blockstate = worldIn.getBlockState(finalPos);
                        if (blockstate.getBlock() instanceof CoffinBlock) {
                            worldIn.setBlockState(finalPos, blockstate.with(BedBlock.OCCUPIED, Boolean.TRUE), 3);
                        }
                    });
                    return true;
                }
            } else {
                worldIn.removeBlock(pos, false);
                BlockPos blockPos = pos.offset(state.get(HORIZONTAL_FACING).getOpposite());
                if (worldIn.getBlockState(blockPos).getBlock() == this) {
                    worldIn.removeBlock(blockPos, false);
                }
                worldIn.createExplosion(null, DamageSource.netherBedExplosion(), (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
                return true;
            }
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, @Nonnull PlayerEntity player) {
        CoffinPart part = state.get(PART);
        BlockPos blockpos = pos.offset(getDirectionToOther(part, state.get(HORIZONTAL_FACING)));
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (blockstate.getBlock() == this && blockstate.get(PART) != part) {
            worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
            worldIn.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
            if (!worldIn.isRemote && !player.isCreative()) {
                ItemStack itemstack = player.getHeldItemMainhand();
                spawnDrops(state, worldIn, pos, null, player, itemstack);
                spawnDrops(blockstate, worldIn, blockpos, null, player, itemstack);
            }
            player.addStat(Stats.BLOCK_MINED.get(this));
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
        super.onBlockPlacedBy(worldIn, pos, state, entity, itemStack);
        if (!worldIn.isRemote) {
            BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING));
            worldIn.setBlockState(blockpos, state.with(PART, CoffinPart.HEAD), 3);
            worldIn.notifyNeighbors(pos, Blocks.AIR);
            state.updateNeighbors(worldIn, pos, 3);
        }
    }

    @Nonnull
    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing == getDirectionToOther(stateIn.get(PART), stateIn.get(HORIZONTAL_FACING))) {
            return facingState.getBlock() == this && facingState.get(PART) != stateIn.get(PART) ? stateIn.with(BedBlock.OCCUPIED, facingState.get(BedBlock.OCCUPIED)) : Blocks.AIR.getDefaultState();
        } else {
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, BedBlock.OCCUPIED, PART);
    }

    /**
     * Finds the player that is currently sleeping in this coffin
     *
     * @param worldIn
     * @param pos
     * @return
     */
    @Nullable
    private PlayerEntity getPlayerInCoffin(World worldIn, BlockPos pos) {
        for (PlayerEntity entityplayer : worldIn.getPlayers()) {
            if (entityplayer.isSleeping() && entityplayer.getBedLocation(worldIn.getDimension().getType()).equals(pos)) {
                return entityplayer;
            }
        }

        return null;
    }

    public enum CoffinPart implements IStringSerializable {
        HEAD("head"),
        FOOT("foot");

        private final String name;

        CoffinPart(String name) {
            this.name = name;
        }

        @Nonnull
        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }
    }
}
