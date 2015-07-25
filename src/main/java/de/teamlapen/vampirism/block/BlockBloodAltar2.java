package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.util.Logger;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.item.ItemBloodBottle;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar2;

/**
 * Block for Blood Altar 2
 * 
 * @author Maxanier
 *
 */
public class BlockBloodAltar2 extends BasicBlockContainer {
	private final static String TAG = "BlockBloodAltar2";
	public final static String name = "bloodAltarTier2";

	public BlockBloodAltar2() {
		super(Material.iron, name);
		this.setHardness(4.0F);
		this.setHarvestLevel("pickaxe", 1);
	}

	private void interactBottle(TileEntityBloodAltar2 te,ItemStack bottle){
		if(bottle==null)return;
		int old=ItemBloodBottle.getBlood(bottle);
		if(old>0){
			ItemBloodBottle.removeBlood(bottle,te.addBlood(old));
		}
		else{
			ItemBloodBottle.addBlood(bottle,te.removeBlood(ItemBloodBottle.MAX_BLOOD));
		}
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityBloodAltar2();
	}

	/*
	 * Not needed anymore
	 * 
	 * @SideOnly(Side.CLIENT)
	 * 
	 * @Override public String getItemIconName() { return "vampirism:spawnBloodAltar";
	 * 
	 * }
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int par2, int par3, int par4, EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (!world.isRemote) {
			ItemStack item = null;
			try {
				item = player.inventory.getCurrentItem();
			} catch (NullPointerException e) {
			}
			TileEntityBloodAltar2 te = (TileEntityBloodAltar2) world.getTileEntity(par2, par3, par4);
			if (item != null && item.getItem() instanceof ItemBloodBottle) {
				Logger.t("Interact");
				interactBottle(te, item);
				return true;
			} else if (item == null&&player.isSneaking()) {
				Logger.t("start");
				startRitual(te, player);
			}

		}
		return false;
	}

	private void startRitual(TileEntityBloodAltar2 te, EntityPlayer p) {
		te.startRitual(p);
	}

	// public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
	// return p_149691_1_ == 1 ? this.field_149935_N
	// : (p_149691_1_ == 0 ? this.field_149935_N
	// : (p_149691_1_ != p_149691_2_ ? this.blockIcon
	// : this.field_149936_O));
	// }
	//
	// @SideOnly(Side.CLIENT)
	// public void registerBlockIcons(IIconRegister p_149651_1_) {
	// this.blockIcon = p_149651_1_.registerIcon("furnace_side");
	// this.field_149936_O = p_149651_1_
	// .registerIcon(this.field_149932_b ? "furnace_front_on"
	// : "furnace_front_off");
	// this.field_149935_N = p_149651_1_.registerIcon("furnace_top");
	// }

}