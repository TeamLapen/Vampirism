package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.tileentity.TileAlchemicalCauldron;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;



public class BlockAlchemicalCauldron extends VampirismBlockContainer {

    public final static String regName = "alchemical_cauldron";
    public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;

    /**
     * 0: No liquid,
     * 1: Liquid,
     * 2: Boiling liquid
     */
    public static final IntegerProperty LIQUID = IntegerProperty.create("liquid", 0, 2);
    public static final BooleanProperty BURNING = BooleanProperty.create("burning");
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.07, 0, 0.07, 0.93, 0.87, 0.93);

    public BlockAlchemicalCauldron() {
        super(regName, Block.Properties.create(Material.IRON).hardnessAndResistance(4f));
        this.setDefaultState(this.stateContainer.getBaseState().with(LIQUID, 0).with(FACING, EnumFacing.NORTH).with(BURNING, false));
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileAlchemicalCauldron();
    }

    @Nullable
    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
    }

    @Override
    public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileAlchemicalCauldron tile = getTile(worldIn, pos);
            if (tile != null) {
                if (tile.canUse(playerIn)) {
                    playerIn.openGui(VampirismMod.instance, ModGuiHandler.ID_ALCHEMICAL_CAULDRON, worldIn, pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }
        return true;
    }

    /*
    TODO 1.13 make sure tile entity updates block state
    @Override
    public IBlockState getActualState(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        TileAlchemicalCauldron t = getTile(worldIn, pos);
        if (t != null) {
            state = state.withProperty(LIQUID, !t.isFilled() ? 0 : t.isCooking() ? 2 : 1).withProperty(BURNING, t.isBurning());
        }
        return state;
    }
     */

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }


    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public IBlockState rotate(IBlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }



    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(FACING, LIQUID, BURNING);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileAlchemicalCauldron tile = getTile(worldIn, pos);
        if (tile != null && placer instanceof EntityPlayer) {
            tile.setOwner((EntityPlayer) placer);
        }
    }

    @Nullable
    private TileAlchemicalCauldron getTile(IBlockReader world, BlockPos pos) {
        TileEntity t = world.getTileEntity(pos);
        if (t instanceof TileAlchemicalCauldron) return (TileAlchemicalCauldron) t;
        return null;
    }
}
