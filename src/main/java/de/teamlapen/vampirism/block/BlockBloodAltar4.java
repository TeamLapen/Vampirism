package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.GuiHandler;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar4;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar4.PHASE;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Random;

/**
 * BlockBloodAltar4
 * 
 * @author Max
 *
 */
public class BlockBloodAltar4 extends BasicBlockContainer {

	public final static String name = "bloodAltarTier4";

	public BlockBloodAltar4() {
		super(Material.rock, name);
		this.setHardness(5.0F);
		this.setHarvestLevel("pickaxe", 2);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		dropItems(world,pos);
		super.breakBlock(world,pos,state);
	}



	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityBloodAltar4();
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

			if (item != null && item.stackSize > 0) {
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;

				EntityItem entityItem = new EntityItem(world, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz, item.copy());

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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntityBloodAltar4 te = (TileEntityBloodAltar4) world.getTileEntity(pos);
		if (player.isSneaking() && player.inventory.getCurrentItem() == null) {
			te.onBlockActivated(player);
			return true;
		}
		if (!player.isSneaking()) {
			if (!te.getPhase().equals(PHASE.NOT_RUNNING)) {
				player.addChatMessage(new ChatComponentTranslation("text.vampirism.ritual_still_running"));
				return false;
			}
			player.openGui(VampirismMod.instance, GuiHandler.ID_ALTAR_4, world, pos.getX(),pos.getY(),pos.getZ());
			return true;
		}
		return false;
	}



}
