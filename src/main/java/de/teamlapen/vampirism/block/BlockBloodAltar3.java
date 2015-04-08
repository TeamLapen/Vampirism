package de.teamlapen.vampirism.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar3;

/**
 * 
 * @author Max
 *
 */
public class BlockBloodAltar3 extends BasicBlockContainer {
	private final static String TAG = "BlockBloodAltar3";
	public final static String name = "bloodAltar3";

	public BlockBloodAltar3() {
		super(Material.iron, name);
		this.setBlockTextureName("vampirism:iconBloodAltar2");
		this.setHardness(4.0F);
		this.setHarvestLevel("pickaxe", 2);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityBloodAltar3();
	}

	@Override
	public boolean onBlockActivated(World world, int par2, int par3, int par4, EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (!world.isRemote) {

		}
		return false;
	}
}
