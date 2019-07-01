package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.tileentity.TileAltarInfusion;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Altar of infusion
 */
public class BlockAltarInfusion extends VampirismBlockContainer {
    private final static String name = "altar_infusion";

    public BlockAltarInfusion() {
        super(name, Properties.create(Material.ROCK).hardnessAndResistance(5));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileAltarInfusion();
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        return 2;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(IBlockState state) {
        return ToolType.PICKAXE;
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        TileAltarInfusion te = (TileAltarInfusion) worldIn.getTileEntity(pos);
        //If empty hand and can start -> Start
        if (worldIn.isRemote || te == null) return true;
        int result = te.canActivate(player, true);
        if (heldItem.isEmpty()) {
            if (result == 1) {
                te.startRitual(player);
                return true;
            }

        }
        //If non empty hand or missing items -> open GUI
        if (!heldItem.isEmpty() || result == -4) {
            if (te.getCurrentPhase() != TileAltarInfusion.PHASE.NOT_RUNNING) {
                player.sendMessage(new TextComponentTranslation("text.vampirism.ritual_still_running"));
                return true;
            }
            //player.openGui(VampirismMod.instance, ModGuiHandler.ID_ALTAR_INFUSION, worldIn, pos.getX(), pos.getY(), pos.getZ());//TODO 1.14
            return true;
        }
        return true;
    }


    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            dropItems(worldIn, pos);

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    private void dropItems(World world, BlockPos pos) {
        Random rand = new Random();

        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof IInventory)) {
            return;
        }
        IInventory inventory = (IInventory) tileEntity;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack item = inventory.getStackInSlot(i);

            if (!item.isEmpty()) {
                float rx = rand.nextFloat() * 0.8F + 0.1F;
                float ry = rand.nextFloat() * 0.8F + 0.1F;
                float rz = rand.nextFloat() * 0.8F + 0.1F;

                EntityItem entityItem = new EntityItem(world, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz, item.copy());

                if (item.hasTag()) {
                    entityItem.getItem().setTag(item.getTag().copy());
                }

                float factor = 0.05F;
                entityItem.motionX = rand.nextGaussian() * factor;
                entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
                entityItem.motionZ = rand.nextGaussian() * factor;
                world.spawnEntity(entityItem);
                inventory.setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }
    }
}