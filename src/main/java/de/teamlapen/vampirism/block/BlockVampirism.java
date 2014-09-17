package de.teamlapen.vampirism.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import de.teamlapen.vampirism.util.REFERENCE;

public class BlockVampirism extends Block {

	public BlockVampirism(Material material) {
		super(material);
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("block.%s%s", REFERENCE.MODID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean shouldRenderBlock() {
		return false;
	}
}
