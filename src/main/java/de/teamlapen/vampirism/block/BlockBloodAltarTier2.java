package de.teamlapen.vampirism.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.item.ItemBloodBottle;
import de.teamlapen.vampirism.item.ItemVampiresFear;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier2;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * Block for Blood Altar Tier 2
 * @author Maxanier
 *
 */
public class BlockBloodAltarTier2 extends BasicBlockContainer{
	private final static String TAG="BlockBloodAltarTier2";
	public final static String name="bloodAltarTier2";
	public BlockBloodAltarTier2() {
		super(Material.iron, name);

	}
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityBloodAltarTier2();
	}
	
	@Override
	public boolean onBlockActivated(World world, int par2, int par3,
			int par4, EntityPlayer player, int par6, float par7,
			float par8, float par9) {
			if (!world.isRemote) {
			ItemStack item = null;
			try {
				item = player.inventory.getCurrentItem();
			} catch (NullPointerException e) {
				Logger.i(TAG, "No item in hand");
			}
			TileEntityBloodAltarTier2 te = (TileEntityBloodAltarTier2) world.getTileEntity(par2, par3, par4);
			if(item != null && item.getItem() instanceof ItemBloodBottle) {
				Logger.i(TAG, "Blood is being added");
				addBlood(te,item);
				return true;
			}
			else if(item==null){
				startRitual(te,player);
			}
			
		}
		return false;
	}
	
	private void addBlood(TileEntityBloodAltarTier2 te,ItemStack item){
		int amount=ItemBloodBottle.getBlood(item);
		ItemBloodBottle.removeBlood(item,te.addBlood(amount));
	}
	
	private void startRitual(TileEntityBloodAltarTier2 te,EntityPlayer p){
		te.startRitual(p);
	}
	
	
	@SideOnly(Side.CLIENT)
	@Override
    public int getRenderBlockPass()
    {
        return 1;
    }
	
	@SideOnly(Side.CLIENT)
	@Override
	public String getItemIconName(){
		return "vampirism:spawnBloodAltar";
		
	}
	
}