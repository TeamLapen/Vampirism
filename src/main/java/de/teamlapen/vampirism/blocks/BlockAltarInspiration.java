package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.vampirism.tileentity.TileAltarInspiration;
import de.teamlapen.vampirism.world.VampirismWorldData;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Altar of inspiration used for vampire levels 1-4
 */
public class BlockAltarInspiration extends VampirismBlockContainer {
    public final static String regName = "altar_inspiration";

    public BlockAltarInspiration() {
        super(regName, Properties.create(Material.IRON).hardnessAndResistance(2f));
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileAltarInspiration();
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        return 1;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(IBlockState state) {
        return ToolType.PICKAXE;
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && !worldIn.isRemote) {
            LazyOptional<IFluidHandlerItem> opt = FluidLib.getFluidItemCap(stack);
            if (opt.isPresent()) {
                TileAltarInspiration tileEntity = (TileAltarInspiration) worldIn.getTileEntity(pos);
                if (!player.isSneaking() && tileEntity != null) {
                    tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map((handler) -> {
                        FluidActionResult result = FluidUtil.tryEmptyContainer(stack, handler, Integer.MAX_VALUE, player, true);
                        if (result.isSuccess()) {
                            player.setHeldItem(hand, result.getResult());
                            return true;
                        }
                        return false;
                    });

                }
                tileEntity.markDirty();
                return true;
            }
        }
        if (stack.isEmpty()) {
            TileAltarInspiration tileEntity = (TileAltarInspiration) worldIn.getTileEntity(pos);
            tileEntity.startRitual(player);
        }

        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }


    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
        super.onReplaced(state, worldIn, pos, newState, isMoving);
        VampirismWorldData.get(worldIn).onAltarInspirationDestroyed(pos);
    }

}
