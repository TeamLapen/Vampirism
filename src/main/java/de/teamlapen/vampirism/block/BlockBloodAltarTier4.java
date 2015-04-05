package de.teamlapen.vampirism.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import de.teamlapen.vampirism.GuiHandler;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.render.RendererBloodAltarTier4;
import de.teamlapen.vampirism.client.render.RendererBloodAltarTier4Tip;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier4;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier4.PHASE;
import de.teamlapen.vampirism.util.Logger;

/**
 * BlockBloodAltarTier4
 * 
 * @author Max
 *
 */
public class BlockBloodAltarTier4 extends BasicBlockContainer {

	public final static String name = "bloodAltarTier4";

	public BlockBloodAltarTier4() {
		super(Material.rock, name);
		this.setHardness(5.0F);
		this.setHarvestLevel("pickaxe", 2);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
		dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityBloodAltarTier4();
	}

	private void dropItems(World world, int x, int y, int z) {
		Random rand = new Random();

		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof IInventory)) {
			return;
		}
		IInventory inventory = (IInventory) tileEntity;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack item = inventory.getStackInSlot(i);

			if (item != null && item.stackSize > 0) {
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;

				EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz, item.copy());

				if (item.hasTagCompound()) {
					entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
				}

				float factor = 0.05F;
				entityItem.motionX = rand.nextGaussian() * factor;
				entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
				entityItem.motionZ = rand.nextGaussian() * factor;
				world.spawnEntityInWorld(entityItem);
				item.stackSize = 0;
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		TileEntityBloodAltarTier4 te = (TileEntityBloodAltarTier4) world.getTileEntity(x, y, z);
		if(player.isSneaking()&&player.inventory.getCurrentItem()==null){
			te.onBlockActivated(player);
			return true;
		}
		if (!player.isSneaking()) {
			if(!te.getPhase().equals(PHASE.NOT_RUNNING)){
				player.addChatMessage(new ChatComponentTranslation("text.vampirism:ritual_still_running"));
				return false;
			}
			player.openGui(VampirismMod.instance, GuiHandler.ID_ALTAR_4, world, x, y, z);
			return true;
		}
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.blockIcon = iconRegister.registerIcon("bloodAltarTier4");
	}

}
