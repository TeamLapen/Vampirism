package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.ModGuiHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import javax.annotation.Nullable;


public class BlockWeaponTable extends VampirismBlock {
    public final static String regName = "weaponTable";
    public static final int MAX_LAVA = 5;
    public static final int MB_PER_META = 200;
    public static final PropertyInteger LAVA = PropertyInteger.create("lava", 0, MAX_LAVA);

    public BlockWeaponTable() {
        super(regName, Material.IRON);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LAVA, 0));


    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(LAVA);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LAVA, meta);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            int lava = state.getValue(LAVA);
            boolean flag = false;
            if (lava < MAX_LAVA) {
                if (heldItem != null && FluidContainerRegistry.isFilledContainer(heldItem)) {
                    FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(heldItem);
                    if (fluidStack.getFluid().equals(FluidRegistry.LAVA) && fluidStack.amount >= MB_PER_META) {
                        IBlockState changed = state.withProperty(LAVA, Math.min(MAX_LAVA, lava + fluidStack.amount / MB_PER_META));
                        worldIn.setBlockState(pos, changed);
                        playerIn.setHeldItem(hand, FluidContainerRegistry.drainFluidContainer(heldItem));
                        flag = true;
                    }
                } else if (heldItem != null && heldItem.getItem() instanceof IFluidContainerItem) {
                    IFluidContainerItem item = (IFluidContainerItem) heldItem.getItem();
                    FluidStack fluidStack = item.getFluid(heldItem);
                    int missing = (MAX_LAVA - lava) * MB_PER_META;
                    if (fluidStack.getFluid().equals(FluidRegistry.LAVA) && fluidStack.amount >= MB_PER_META) {
                        FluidStack drained = item.drain(heldItem, missing, true);
                        IBlockState changed = state.withProperty(LAVA, Math.min(MAX_LAVA, lava + drained.amount / MB_PER_META));
                        worldIn.setBlockState(pos, changed);
                        playerIn.setHeldItem(hand, FluidContainerRegistry.drainFluidContainer(heldItem));
                        flag = true;
                    }
                }
            }
            if (!flag) {
                playerIn.openGui(VampirismMod.instance, ModGuiHandler.ID_WEAPON_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());

            }
        }
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LAVA);
    }
}
