package de.teamlapen.vampirism.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.client.render.RendererBloodAltarTier4Tip;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


/**
 * Pillar tip for altar tier 4
 * @author Maxanier
 *
 */
public class BlockBloodAltarTier4Tip extends BasicBlockContainer{

	/**
	 * Required for renderer
	 *
	 */
	public static class TileEntityBloodAltarTier4Tip extends TileEntity{
		
	}
	public final static String name="bloodAltarTier4Tip";
	public BlockBloodAltarTier4Tip() {
		super(Material.rock, name);
		this.setHardness(3.0F);
		this.setHarvestLevel("pickaxe", 1);
	}
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityBloodAltarTier4Tip();
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.blockIcon = iconRegister.registerIcon(RendererBloodAltarTier4Tip.textureLoc);
	}

}
