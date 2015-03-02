package de.teamlapen.vampirism.tileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockCompressed;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.material.MapColor;
import net.minecraft.init.Blocks;
import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.block.BlockBloodAltarTier4;
import de.teamlapen.vampirism.block.BlockBloodAltarTier4Tip;
import de.teamlapen.vampirism.util.Logger;

/**
 * Tileentity used for BloodAltarTier4
 * 
 * @author Max
 *
 */
public class TileEntityBloodAltarTier4 extends InventoryTileEntity {

	private final static String TAG = "TEBAltar4";
	private final static int[][][] structure1 = new int[][][] { { { 1, 0, 0, 0, 1 }, { 0, 0, 0, 0, 0 }, { 0, 0, 3, 0, 0 }, { 0, 0, 4, 0, 0 }, { 1, 0, 4, 0, 1 } },
			{ { 1, 0, 0, 0, 1 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 1, 0, 0, 0, 1 } },
			{ { 2, 0, 0, 0, 2 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 2, 0, 0, 0, 2 } } };
	private final static int[][][] structure2 = new int[][][] { { { 0, 1, 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 0, 0, 0 }, { 1, 0, 0, 3, 0, 0, 1 }, { 0, 0, 0, 4, 0, 0, 0 }, { 0, 1, 0, 4, 0, 1, 0 } },
			{ { 0, 1, 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 0, 0, 0 }, { 1, 0, 0, 0, 0, 0, 1 }, { 0, 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 0, 1, 0 } },
			{ { 0, 1, 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 0, 0, 0 }, { 1, 0, 0, 0, 0, 0, 1 }, { 0, 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 0, 1, 0 } },
			{ { 0, 2, 0, 0, 0, 2, 0 }, { 0, 0, 0, 0, 0, 0, 0 }, { 2, 0, 0, 0, 0, 0, 2 }, { 0, 0, 0, 0, 0, 0, 0 }, { 0, 2, 0, 0, 0, 2, 0 } } };

	public TileEntityBloodAltarTier4() {
		super(new Slot[] { new Slot(56, 17), new Slot(56, 53) });
	}

	/**
	 * Checks if the blocks in the given box match to the structure description. If it matches it returns the block at the positions marked with 1 in the structure, otherwise its null
	 * 
	 * @param lx
	 *            Lower x
	 * @param ly
	 *            Lower y
	 * @param lz
	 *            Lower z
	 * @param hx
	 *            Higher x
	 * @param hy
	 *            Higher y
	 * @param hz
	 *            Higher z
	 * @param structure
	 * @return
	 */
	private Block checkBlocks(int lx, int ly, int lz, int hx, int hy, int hz, int[][][] structure) {
		Logger.i(TAG, "0.length: " + structure.length + ":" + structure[0].length + ":" + structure[0][0].length);
		Block blocktype = null;
		for (int x = lx; x <= hx; x++) {
			for (int z = lz; z <= hz; z++) {
				for (int y = ly; y <= hy; y++) {
					int type = structure[y - ly][z - lz][x - lx];
					Block b = worldObj.getBlock(x, y, z);
					Logger.i("TEst", "T:" + x + ":" + y + ":" + z + ";" + (x - lx) + ":" + (y - ly) + ":" + (z - lz) + ";" + type + ";" + b.getUnlocalizedName());
					if (type == 0) {
						if (!(b instanceof BlockAir)) {
							Logger.i(TAG, "Expected " + type + " found: " + b.getUnlocalizedName() + " at " + (x - lx) + ":" + (y - ly) + ":" + (z - lz));
							return null;
						}
					}
					if (type == 2) {
						if (!(b instanceof BlockBloodAltarTier4Tip)) {
							Logger.i(TAG, "Expected " + type + " found: " + b.getUnlocalizedName() + " at " + (x - lx) + ":" + (y - ly) + ":" + (z - lz));
							return null;
						}
					}
					if (type == 3) {
						if (!(b instanceof BlockBloodAltarTier4)) {
							Logger.i(TAG, "Expected " + type + " found: " + b.getUnlocalizedName() + " at " + (x - lx) + ":" + (y - ly) + ":" + (z - lz));
							return null;
						}
					}
					if (type == 4) {
						if (!(b instanceof BlockBed)) {
							Logger.i(TAG, "Expected " + type + " found: " + b.getUnlocalizedName() + " at " + (x - lx) + ":" + (y - ly) + ":" + (z - lz));
							return null;
						}
					}
					if (type == 1) {
						if (blocktype != null && !blocktype.equals(b)) {
							Logger.i(TAG, "Expected " + type + " found: " + b.getUnlocalizedName() + " at " + (x - lx) + ":" + (y - ly) + ":" + (z - lz));
							return null;
						}
						blocktype = b;
					}

				}
			}
		}
		return blocktype;
	}

	/**
	 * Determines the level of the structure build around the altar. TODO make it rotatable
	 * 
	 * @return
	 */
	private int determineLevel() {

		int level = 0;
		int x = this.xCoord;
		int y = this.yCoord;
		int z = this.zCoord;
		// setBlocks(x-2,y,z-2,x+2,y+2,z+2,structure1);
		int meta = worldObj.getBlockMetadata(x, y, z);
		Logger.i(TAG, "Meta: " + meta);
		Block type = null;
		Logger.i(TAG, "Testing structure 2");
		type = checkBlocks(x - 3, y, z - 2, x + 3, y + 3, z + 2, structure2);
		if (type != null) {
			if (type instanceof BlockCompressed) {
				if (((BlockCompressed) type).getMapColor(1).equals(MapColor.ironColor)) {
					return 3;
				}
				if (((BlockCompressed) type).getMapColor(1).equals(MapColor.goldColor)) {
					return 4;
				}
			}
		}
		Logger.i(TAG, "Testing structure 1");
		type = checkBlocks(x - 2, y, z - 2, x + 2, y + 2, z + 2, structure1);
		if (type != null) {
			if (type instanceof BlockStoneBrick) {
				return 1;
			}
			if (type instanceof BlockCompressed) {
				if (((BlockCompressed) type).getMapColor(1).equals(MapColor.ironColor)) {
					return 2;
				}
			}
		}
		return 0;

	}

	@Override
	public String getInventoryName() {
		return "Items-test-inventory-name";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	/**
	 * test method to set the blocks
	 * 
	 * @param lx
	 * @param ly
	 * @param lz
	 * @param hx
	 * @param hy
	 * @param hz
	 * @param structure
	 */
	private void setBlocks(int lx, int ly, int lz, int hx, int hy, int hz, int[][][] structure) {

		Block blocktype = null;
		for (int x = lx; x <= hx; x++) {
			for (int z = lz; z <= hz; z++) {
				for (int y = ly; y < hy; y++) {
					int type = structure[y - ly][z - lz][x - lx];
					if (type == 0) {
						worldObj.setBlock(x, y, z, Blocks.air);
					}
					if (type == 2) {
						worldObj.setBlock(x, y, z, ModBlocks.bloodAltarTier4Tip);
					}
					if (type == 3) {
						worldObj.setBlock(x, y, z, ModBlocks.bloodAltarTier4);
					}
					if (type == 4) {
						worldObj.setBlock(x, y, z, Blocks.bed);
					}
					if (type == 1) {
						worldObj.setBlock(x, y, z, Blocks.stonebrick);
					}

				}
			}
		}
	}
}
