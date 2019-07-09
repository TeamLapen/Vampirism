package de.teamlapen.vampirism.blocks;

import com.mojang.datafixers.util.Either;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.tileentity.TileCoffin;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.common.extensions.IForgeDimension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Block coffin.
 */
public class BlockCoffin extends VampirismBlockContainer {

    public static final String name = "block_coffin";
    public static final EnumProperty<CoffinPart> PART = EnumProperty.create("part", CoffinPart.class);
    public static final BooleanProperty OCCUPIED = BooleanProperty.create("occupied");
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    private static final Logger LOGGER = LogManager.getLogger();

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

    public BlockCoffin() {
        super(name, Properties.create(Material.WOOD).hardnessAndResistance(0.2f));
        this.setDefaultState(this.getStateContainer().getBaseState().with(OCCUPIED, Boolean.TRUE).with(PART, CoffinPart.FOOT).with(FACING, Direction.NORTH));

    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileCoffin();
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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
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
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        Direction enumfacing = state.get(FACING);

        if (state.get(PART) == CoffinPart.HEAD) {
            if (world.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock() != this) {
                world.removeBlock(pos);
            }
        } else if (world.getBlockState(pos.offset(enumfacing)).getBlock() != this) {
            worldIn.removeBlock(pos);

            if (!world.isRemote()) {
                this.dropBlockAsItemWithChance(state, world, pos, 1, 0);
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
                TileCoffin tile = (TileCoffin) world.getTileEntity(pos);
                TileEntity other = state.get(PART) == CoffinPart.HEAD ? world.getTileEntity(pos.offset(state.get(FACING).getOpposite())) : world.getTileEntity(pos.offset(state.get(FACING)));
                if (!(other instanceof TileCoffin)) {
                    return true;
                }
                tile.changeColor(((DyeItem) heldItem.getItem()).getDyeColor());
                ((TileCoffin) other).changeColor(((DyeItem) heldItem.getItem()).getDyeColor());
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

    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        CoffinPart part = state.get(PART);
        BlockPos blockpos = pos.offset(getDirectionToOther(part, state.get(FACING)));
        BlockState iblockstate = worldIn.getBlockState(blockpos);
        if (iblockstate.getBlock() == this && iblockstate.get(PART) != part) {
            worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
            if (!worldIn.isRemote && !player.abilities.isCreativeMode) {
                if (part == CoffinPart.HEAD) {
                    state.dropBlockAsItem(worldIn, pos, 0);
                } else {
                    iblockstate.dropBlockAsItem(worldIn, blockpos, 0);
                }
            }

            player.addStat(Stats.BLOCK_MINED.get(this));
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public void setBedOccupied(BlockState state, IWorldReader world, BlockPos pos, PlayerEntity player, boolean occupied) {
        if (world instanceof IWorldWriter) {
            ((IWorldWriter) world).setBlockState(pos, state.with(OCCUPIED, occupied), 4);
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
