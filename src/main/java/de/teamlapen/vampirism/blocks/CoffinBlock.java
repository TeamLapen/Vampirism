package de.teamlapen.vampirism.blocks;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
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
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.common.extensions.IForgeDimension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Block coffin.
 */
public class CoffinBlock extends VampirismBlockContainer {

    public static final String name = "coffin";
    public static final EnumProperty<CoffinPart> PART = EnumProperty.create("part", CoffinPart.class);
    public static final BooleanProperty OCCUPIED = BooleanProperty.create("occupied");
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private static final VoxelShape shape = makeShape();

    public static boolean isOccupied(IBlockReader world, BlockPos pos) {
        return world.getBlockState(pos).get(OCCUPIED);
    }

    public static void setCoffinOccupied(World world, BlockPos pos, boolean value) {
        BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.with(OCCUPIED, value), 4);
    }

    public static boolean isHead(IBlockReader world, BlockPos pos) {
        return world.getBlockState(pos).get(PART) == CoffinPart.HEAD;
    }

    private static Direction getDirectionToOther(CoffinPart type, Direction facing) {
        return type == CoffinPart.FOOT ? facing : facing.getOpposite();
    }

    private static VoxelShape makeShape() {
        return Block.makeCuboidShape(0, 0, 0, 16, 13, 16);
    }

    public CoffinBlock() {
        super(name, Properties.create(Material.WOOD).hardnessAndResistance(0.2f));
        this.setDefaultState(this.getStateContainer().getBaseState().with(OCCUPIED, Boolean.FALSE).with(PART, CoffinPart.FOOT).with(FACING, Direction.NORTH));

    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new CoffinTileEntity();
    }

    @Override
    public Direction getBedDirection(BlockState state, IWorldReader world, BlockPos pos) {
        return state.get(FACING);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return state.get(PART) == CoffinPart.FOOT ? Collections.emptyList() : super.getDrops(state, builder);
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

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
        return context.getWorld().getBlockState(blockpos1).isReplaceable(context) ? this.getDefaultState().with(FACING, enumfacing) : null;
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
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        Direction enumfacing = state.get(FACING);

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
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {

        if (world.isRemote) {
            return true;
        } else {
            ItemStack heldItem = player.getHeldItem(hand);
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof DyeItem) {
                CoffinTileEntity tile = (CoffinTileEntity) world.getTileEntity(pos);
                TileEntity other = state.get(PART) == CoffinPart.HEAD ? world.getTileEntity(pos.offset(state.get(FACING).getOpposite())) : world.getTileEntity(pos.offset(state.get(FACING)));
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
                pos = pos.offset(state.get(FACING));
                state = world.getBlockState(pos);
                if (state.getBlock() != this) {
                    return true;
                }
            }

            if (VampirePlayer.get(player).getLevel() == 0) {
                player.sendMessage(new TranslationTextComponent("text.vampirism.coffin.cant_use"));
                return true;
            }

            IForgeDimension.SleepResult sleepResult = world.dimension.canSleepAt(player, pos);
            if (sleepResult != IForgeDimension.SleepResult.BED_EXPLODES) {
                if (sleepResult == IForgeDimension.SleepResult.DENY) return true;
                if (state.get(OCCUPIED)) {
                    PlayerEntity entityplayer = this.getPlayerInCoffin(world, pos);

                    if (entityplayer != null) {
                        player.sendStatusMessage(new TranslationTextComponent("text.vampirism.coffin.occupied"), true);
                        return true;
                    }

                    state = state.with(OCCUPIED, Boolean.FALSE);
                    world.setBlockState(pos, state, 4);
                }

                IVampirePlayer vampire = VReference.VAMPIRE_FACTION.getPlayerCapability(player);

                Either<PlayerEntity.SleepResult, Unit> entityplayer$enumstatus = vampire.trySleep(pos);

                if (entityplayer$enumstatus.right().isPresent()) {
                    state = state.with(OCCUPIED, Boolean.TRUE);
                    world.setBlockState(pos, state, 4);
                    return true;
                } else {
                    entityplayer$enumstatus.left().ifPresent((result) -> {
                        if (result == PlayerEntity.SleepResult.NOT_POSSIBLE_NOW) {
                            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.coffin.no_sleep"), true);
                        } else if (result == PlayerEntity.SleepResult.NOT_SAFE) {
                            player.sendStatusMessage(new TranslationTextComponent("block.minecraft.bed.not_safe"), true);
                        }
                    });
                    return true;
                }
            } else {
                player.sendMessage(new TranslationTextComponent("text.vampirism.coffin.wrong_dimension"));
                return true;
            }
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        CoffinPart part = state.get(PART);
        BlockPos blockpos = pos.offset(getDirectionToOther(part, state.get(FACING)));
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

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing == getDirectionToOther(stateIn.get(PART), stateIn.get(FACING))) {
            return facingState.getBlock() == this && facingState.get(PART) != stateIn.get(PART) ? stateIn.with(OCCUPIED, facingState.get(OCCUPIED)) : Blocks.AIR.getDefaultState();
        } else {
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, OCCUPIED, PART);
    }

    /**
     * Finds the player that is currently sleeping in this coffin
     *
     * @param worldIn
     * @param pos
     * @return
     */
    private @Nullable
    PlayerEntity getPlayerInCoffin(World worldIn, BlockPos pos) {
        for (PlayerEntity entityplayer : worldIn.getPlayers()) {
            if (VampirePlayer.get(entityplayer).isPlayerSleeping() && entityplayer.getBedLocation().equals(pos)) {
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

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }
    }
}
