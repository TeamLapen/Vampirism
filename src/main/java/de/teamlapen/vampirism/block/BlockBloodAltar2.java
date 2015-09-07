package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.item.ItemBloodBottle;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar2;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		this.setHardness(40.0F);
		this.setHarvestLevel("pickaxe", 1);
	}

	private void interactBottle(TileEntityBloodAltar2 te, ItemStack bottle, boolean creative) {
		if(bottle==null)return;
		int old=ItemBloodBottle.getBlood(bottle);
		if(old>0){
			int added = te.addBlood(old);
			if (!creative) {
				ItemBloodBottle.removeBlood(bottle, old);
			}
		}
		else{
			ItemBloodBottle.addBlood(bottle,te.removeBlood(ItemBloodBottle.MAX_BLOOD));
		}
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityBloodAltar2();
	}


	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			ItemStack item = null;
			try {
				item = player.inventory.getCurrentItem();
			} catch (NullPointerException e) {
			}
			TileEntityBloodAltar2 te = (TileEntityBloodAltar2) world.getTileEntity(pos);
			if (item != null && item.getItem() instanceof ItemBloodBottle) {
				interactBottle(te, item, player.capabilities.isCreativeMode);
				return true;
			} else if (item == null||player.isSneaking()) {
				startRitual(te, player);
			}

		}
		return false;
	}


	private void startRitual(TileEntityBloodAltar2 te, EntityPlayer p) {
		te.startRitual(p);
	}


}