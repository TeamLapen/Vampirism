package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Simple class to make the BlockStairs constructor available as well as set some Vampirism related values
 */
public class BlockCastleStairs extends BlockStairs {
	public final static String name="castleStairs";
	public BlockCastleStairs(Block block, int meta) {
		super(block, meta);
		this.setUnlocalizedName("castleStairs");
		this.setCreativeTab(VampirismMod.tabVampirism);
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("block.%s%s", REFERENCE.MODID.toLowerCase() + ".", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_) {

	}
}
