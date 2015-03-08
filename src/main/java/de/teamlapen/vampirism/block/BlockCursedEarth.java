package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.material.Material;

public class BlockCursedEarth extends BasicBlock {

	public final static String name = "cursedEarth";

	public BlockCursedEarth() {
		super(Material.ground, name);
		this.setHardness(0.5F);
		this.setResistance(2.0F);
		this.setHarvestLevel("pickaxe", 2);
		this.setStepSound(soundTypeGravel);
		this.setBlockTextureName(REFERENCE.MODID + ":" + name);
	}
}
