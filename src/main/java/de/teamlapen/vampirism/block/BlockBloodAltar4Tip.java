package de.teamlapen.vampirism.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Pillar tip for altar 4
 * 
 * @author Maxanier
 *
 */
public class BlockBloodAltar4Tip extends BasicBlockContainer {

	/**
	 * Required for renderer
	 *
	 */
	public static class TileEntityBloodAltar4Tip extends TileEntity {

	}

	public final static String name = "bloodAltarTier4Tip";

	public BlockBloodAltar4Tip() {
		super(Material.rock, name);
		this.setHardness(3.0F);
		this.setHarvestLevel("pickaxe", 1);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityBloodAltar4Tip();
	}

}
