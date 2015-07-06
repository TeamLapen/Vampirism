package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;

/**
 * Simple class to make the BlockStairs constructor available as well as set some Vampirism related values
 */
public class BlockCastleStairs extends BlockStairs {
	public final static String name="castleStairs";
	public BlockCastleStairs(Block block, int meta) {
		super(block, meta);
		this.setBlockName("castleStairs");
		this.setCreativeTab(VampirismMod.tabVampirism);
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("block.%s%s", REFERENCE.MODID.toLowerCase() + ".", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}
}
