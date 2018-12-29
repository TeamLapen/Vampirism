package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.tileentity.TileAltarInfusion;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Altar of infusion
 */
public class BlockAltarInfusion extends VampirismBlockContainer {
    private final static String name = "altar_infusion";

    public BlockAltarInfusion() {
        super(name, Material.ROCK);
        this.setHardness(5.0F);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        dropItems(worldIn, pos);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileAltarInfusion();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing faing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = playerIn.getHeldItem(hand);
        TileAltarInfusion te = (TileAltarInfusion) worldIn.getTileEntity(pos);
        //If empty hand and can start -> Start
        if (worldIn.isRemote) return true;
        int result = te.canActivate(playerIn, true);
        if (heldItem.isEmpty()) {
            VampirismMod.log.d("AltarInfusion", "Trying to activate. Result: %s", result);
            if (result == 1) {
                te.startRitual(playerIn);
                return true;
            }

        }
        //If non empty hand or missing items -> open GUI
        else if (!heldItem.isEmpty() || result == -4) {
            if (te.getCurrentPhase() != TileAltarInfusion.PHASE.NOT_RUNNING) {
                playerIn.sendMessage(new TextComponentTranslation("text.vampirism.ritual_still_running"));
                return true;
            }
            playerIn.openGui(VampirismMod.instance, ModGuiHandler.ID_ALTAR_INFUSION, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return true;
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

                if (item.hasTagCompound()) {
                    entityItem.getItem().setTagCompound(item.getTagCompound().copy());
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