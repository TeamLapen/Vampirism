package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Simple class to make the BlockStairs constructor available as well as set some Vampirism related values
 */
public class BlockCastleStairs extends BlockStairs {
	public final static String name="castleStairs";
	public BlockCastleStairs(IBlockState modelState) {
		super(modelState);
		this.setUnlocalizedName("castleStairs");
		this.setCreativeTab(VampirismMod.tabVampirism);
	}

}
