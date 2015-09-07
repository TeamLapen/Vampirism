package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar1;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockBloodAltar1 extends BasicBlockContainer {
	public static final String name = "bloodAltar";
	private final String TAG = "BlockBloodAltar";

	public BlockBloodAltar1() {
		super(Material.rock, name);
		this.setHardness(8.0F);
		this.setHarvestLevel("pickaxe", 3);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityBloodAltar1 te = (TileEntityBloodAltar1) world.getTileEntity(pos);
		te.dropSword();
		super.breakBlock(world, pos, state);
	}



	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityBloodAltar1();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			ItemStack item = null;
			try {
				item = player.inventory.getCurrentItem();
			} catch (NullPointerException ignored) {
			}

			TileEntityBloodAltar1 te = (TileEntityBloodAltar1) world.getTileEntity(pos);
			te.onActivated(player, item);
		}
		return true;
	}

}
