package de.teamlapen.vampirism.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.GuiHandler;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.render.RendererBloodAltarTier4Tip;
import de.teamlapen.vampirism.client.render.RendererChurchAltar;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockChurchAltar extends BasicBlockContainer {
	public static final String name="churchAltar";
	
	public static class TileEntityChurchAltar extends TileEntity{
		
	}

	public BlockChurchAltar() {
		super(Material.rock, name);
	}
	
	@Override
	public boolean onBlockActivated(World world, int posX, int posY, int posZ, EntityPlayer player, int par6, float par7, float par8, float par9) {
		player.openGui(VampirismMod.instance, GuiHandler.ID_CONVERT_BACK, world, posX, posY, posZ);
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityChurchAltar();
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.blockIcon = iconRegister.registerIcon("churchAltar");
	}

}
