package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.IBlockRegistrable;
import de.teamlapen.vampirism.util.IIgnorePropsForRender;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Simple class to make the BlockStairs constructor available as well as set some Vampirism related values
 */
public class BlockCastleStairs extends BlockStairs {
	public final static String name="castleStairs";
	private final IBlockState modelState;
	public BlockCastleStairs(IBlockState modelState) {
		super(modelState);
		this.modelState=modelState;
		this.setUnlocalizedName(REFERENCE.MODID+"."+name);
		this.setCreativeTab(VampirismMod.tabVampirism);

	}



	@Override
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		modelState.getBlock().randomDisplayTick(worldIn,pos,modelState,rand);
	}

}
