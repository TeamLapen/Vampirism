package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.GuiHandler;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockChurchAltar extends BasicBlock {
	public static final String name="churchAltar";

	public BlockChurchAltar() {
		super(Material.rock, name);
	}
	
	@Override
	public boolean onBlockActivated(World world, int posX, int posY, int posZ, EntityPlayer player, int par6, float par7, float par8, float par9) {
		player.openGui(VampirismMod.instance, GuiHandler.ID_CONVERT_BACK, world, posX, posY, posZ);
		return true;
	}

}
