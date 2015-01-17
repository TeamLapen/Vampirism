package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public abstract class BasicBlock extends Block {
	protected BasicBlock(Material p_i45394_1_,String name) {
		super(p_i45394_1_);
		setCreativeTab(VampirismMod.tabVampirism);
		this.setBlockName(name);
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("block.%s%s", REFERENCE.MODID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}
}
