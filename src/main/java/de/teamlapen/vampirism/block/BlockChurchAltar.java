package de.teamlapen.vampirism.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import de.teamlapen.vampirism.GuiHandler;
import de.teamlapen.vampirism.VampirismMod;

public class BlockChurchAltar extends BasicBlockContainer {
	public static class TileEntityChurchAltar extends TileEntity {

	}

	public static final String name = "churchAltar";

	public BlockChurchAltar() {
		super(Material.rock, name);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityChurchAltar();
	}

	@Override
	public boolean onBlockActivated(World world, int posX, int posY, int posZ, EntityPlayer player, int par6, float par7, float par8, float par9) {
		player.openGui(VampirismMod.instance, GuiHandler.ID_CONVERT_BACK, world, posX, posY, posZ);
		return true;
	}

}
