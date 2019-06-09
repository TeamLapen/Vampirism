package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.tileentity.TileCoffin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.*;
import net.minecraftforge.common.extensions.IForgeDimension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Block coffin.
 */
public class BlockCoffin extends VampirismBlockContainer {

    public static final String name = "block_coffin";
    public static final EnumProperty<CoffinPart> PART = EnumProperty.create("part", CoffinPart.class);
    public static final BooleanProperty OCCUPIED = BooleanProperty.create("occupied");
    public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;

    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean isOccupied(IBlockReader world, BlockPos pos) {
        return world.getBlockState(pos).get(OCCUPIED);
    }

    public static void setCoffinOccupied(World world, BlockPos pos, boolean value) {
        IBlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.with(OCCUPIED, value), 4);
    }

    public static boolean isHead(IBlockReader world, BlockPos pos) {
        return world.getBlockState(pos).get(PART) == CoffinPart.HEAD;
    }

    private static EnumFacing getDirectionToOther(CoffinPart type, EnumFacing facing) {
        return type == CoffinPart.FOOT ? facing : facing.getOpposite();
    }

    public BlockCoffin() {
        super(name, Properties.create(Material.WOOD).hardnessAndResistance(0.2f));
        this.setDefaultState(this.getStateContainer().getBaseState().with(OCCUPIED, Boolean.TRUE).with(PART, CoffinPart.FOOT).with(FACING, EnumFacing.NORTH));

    }

    @Override
    public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileCoffin();
    }

    @Override
    public EnumFacing getBedDirection(IBlockState state, IWorldReader world, BlockPos pos) {
        return state.get(FACING);
    }

    @Override
    public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
        return state.get(PART) == CoffinPart.FOOT ? Items.AIR : super.getItemDropped(state, worldIn, pos, fortune))
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @Nullable
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        EnumFacing enumfacing = context.getPlacementHorizontalFacing();
        BlockPos blockpos = context.getPos();
        BlockPos blockpos1 = blockpos.offset(enumfacing);
        return context.getWorld().getBlockState(blockpos1).isReplaceable(context) ? this.getDefaultState().with(FACING, enumfacing) : null;
    }

    @Override
    public boolean isBedFoot(IBlockState state, IWorldReader world, BlockPos pos) {
        return state.get(PART) == CoffinPart.FOOT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.DESTROY;
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }


    @Override
    public boolean isBed(IBlockState state, IBlockReader world, BlockPos pos, Entity player) {
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        EnumFacing enumfacing = state.get(FACING);

        if (state.get(PART) == CoffinPart.HEAD) {
            if (worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock() != this) {
                worldIn.removeBlock(pos);
            }
        } else if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() != this) {
            worldIn.removeBlock(pos);

            if (!worldIn.isRemote) {
                this.dropBlockAsItemWithChance(state, worldIn, pos, 1, 0);
            }
        }
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        if (world.isRemote) {
            return true;
        } else {
            ItemStack heldItem = player.getHeldItem(hand);
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemDye) {
                TileCoffin tile = (TileCoffin) world.getTileEntity(pos);
                TileEntity other = state.get(PART) == CoffinPart.HEAD ? world.getTileEntity(pos.offset(state.get(FACING).getOpposite())) : world.getTileEntity(pos.offset(state.get(FACING)));
                if (!(other instanceof TileCoffin)) {
                    return true;
                }
                tile.changeColor(((ItemDye) heldItem.getItem()).getDyeColor());
                ((TileCoffin) other).changeColor(((ItemDye) heldItem.getItem()).getDyeColor());
                if (!player.isCreative()) {
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
                player.sendMessage(new TextComponentTranslation("text.vampirism.coffin.cant_use"));
                return true;
            }

            IForgeDimension.SleepResult sleepResult = world.dimension.canSleepAt(player, pos);
            if (sleepResult != IForgeDimension.SleepResult.BED_EXPLODES) {
                if (sleepResult == IForgeDimension.SleepResult.DENY) return true;
                if (state.get(OCCUPIED)) {
                    EntityPlayer entityplayer = this.getPlayerInCoffin(world, pos);

                    if (entityplayer != null) {
                        player.sendStatusMessage(new TextComponentTranslation("text.vampirism.coffin.occupied"), true);
                        return true;
                    }

                    state = state.with(OCCUPIED, Boolean.FALSE);
                    world.setBlockState(pos, state, 4);
                }

                IVampirePlayer vampire = VReference.VAMPIRE_FACTION.getPlayerCapability(player);

                EntityPlayer.SleepResult entityplayer$enumstatus = vampire.trySleep(pos);

                if (entityplayer$enumstatus == EntityPlayer.SleepResult.OK) {
                    state = state.with(OCCUPIED, Boolean.TRUE);
                    world.setBlockState(pos, state, 4);
                    return true;
                } else {
                    if (entityplayer$enumstatus == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW) {
                        player.sendStatusMessage(new TextComponentTranslation("text.vampirism.coffin.no_sleep"), true);
                    } else if (entityplayer$enumstatus == EntityPlayer.SleepResult.NOT_SAFE) {
                        player.sendStatusMessage(new TextComponentTranslation("block.minecraft.bed.not_safe"), true);
                    }

                    return true;
                }
            } else {
                player.sendMessage(new TextComponentTranslation("text.vampirism.coffin.wrong_dimension"));
                return true;
            }
        }
    }

    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        CoffinPart part = state.get(PART);
        BlockPos blockpos = pos.offset(getDirectionToOther(part, state.get(FACING)));
        IBlockState iblockstate = worldIn.getBlockState(blockpos);
        if (iblockstate.getBlock() == this && iblockstate.get(PART) != part) {
            worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
            if (!worldIn.isRemote && !player.isCreative()) {
                if (part == CoffinPart.HEAD) {
                    state.dropBlockAsItem(worldIn, pos, 0);
                } else {
                    iblockstate.dropBlockAsItem(worldIn, blockpos, 0);
                }
            }

            player.addStat(StatList.BLOCK_MINED.get(this));
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public void setBedOccupied(IBlockState state, IWorldReader world, BlockPos pos, EntityPlayer player, boolean occupied) {
        if (world instanceof IWorldWriter) {
            ((IWorldWriter) world).setBlockState(pos, state.with(OCCUPIED, occupied), 4);
        }
    }

    @Override
    public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing == getDirectionToOther(stateIn.get(PART), stateIn.get(FACING))) {
            return facingState.getBlock() == this && facingState.get(PART) != stateIn.get(PART) ? stateIn.with(OCCUPIED, facingState.get(OCCUPIED)) : Blocks.AIR.getDefaultState();
        } else {
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
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
    EntityPlayer getPlayerInCoffin(World worldIn, BlockPos pos) {
        for (EntityPlayer entityplayer : worldIn.playerEntities) {
            if (VampirePlayer.get(entityplayer).isPlayerSleeping() && entityplayer.bedLocation.equals(pos)) {
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
