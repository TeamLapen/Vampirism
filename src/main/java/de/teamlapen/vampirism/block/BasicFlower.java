package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;

/**
 * 
 * TODO make multiple types possible
 *
 */
public abstract class BasicFlower extends BlockBush {




	protected BasicFlower(String name) {
		super(Material.plants);
		setCreativeTab(VampirismMod.tabVampirism);
		this.setStepSound(soundTypeGrass);
		this.setHardness(0.0F);
		this.setUnlocalizedName(name);
	}


}
