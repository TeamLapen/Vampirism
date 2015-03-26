package de.teamlapen.vampirism.block;

import java.util.Iterator;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.tileEntity.TileEntityCoffin;
import de.teamlapen.vampirism.tileEntity.TileEntityCoffinSec;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

/**
 * 
 * @author Moritz
 *
 */
public class BlockCoffin extends BasicBlockContainer {
	private final String TAG = "BlockCoffin";
	public static final String name = "blockCoffin";

	public BlockCoffin() {
		super(Material.rock, name);
	}

	/**
	 * Checks if the secondary block still exists
	 */
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z,
			Block block) {
		TileEntityCoffin tileEntity = (TileEntityCoffin) world
				.getTileEntity(x, y, z);
		if (tileEntity != null) {
			if (!(world.getBlock(tileEntity.secondary_x, tileEntity.secondary_y,
					tileEntity.secondary_z) instanceof BlockCoffinSec)) {
				Logger.i(TAG, "Removing primary coffin block");
				world.setBlockToAir(x, y, z);
				world.removeTileEntity(x, y, z);
			}
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block,
			int par) {
		TileEntityCoffin te = (TileEntityCoffin) world.getTileEntity(x,
				y, z);
		if (te == null) 
			return;
		world.setBlockToAir(te.secondary_x, te.secondary_y, te.secondary_z);
		world.removeTileEntity(te.secondary_x, te.secondary_y, te.secondary_z);
		world.removeTileEntity(x, y, z);
	}

	
	@Override
	public boolean onBlockActivated(World world, int x, int y,
			int z, EntityPlayer player, int par4, float f1, float f2,
			float f3) {
		if (world.isRemote) {
			return true;
		} else {
			//Gets the coordinates of the secondary block
			if(world.getTileEntity(x, y, z) instanceof TileEntityCoffin) {
				TileEntityCoffin te = (TileEntityCoffin) world.getTileEntity(x, y, z);
				x = te.secondary_x;
				y = te.secondary_y;
				z = te.secondary_z;
			}
			int meta = world.getBlockMetadata(x, y, z);

			if (world.provider.canRespawnHere()
					&& world.getBiomeGenForCoords(x, z) != BiomeGenBase.hell) {
				if (func_149976_c(meta)) {
					EntityPlayer playerSleepingHere = null;
					Iterator iterator = world.playerEntities.iterator();

					while (iterator.hasNext()) {
						EntityPlayer tempPlayer = (EntityPlayer) iterator
								.next();

						if (tempPlayer.isPlayerSleeping()) {
							ChunkCoordinates chunkcoordinates = tempPlayer.playerLocation;

							if (chunkcoordinates.posX == x
									&& chunkcoordinates.posY == y
									&& chunkcoordinates.posZ == z) {
								playerSleepingHere = tempPlayer;
							}
						}
					}

					if (playerSleepingHere != null) {
						player.addChatComponentMessage(new ChatComponentTranslation(
								"tile.bed.occupied", new Object[0]));
						return true;
					}

//					setMetaBasedOnWeirdness(world, x, y, z,
//							false);
				}

				EntityPlayer.EnumStatus enumstatus = VampirePlayer.get(player)
						.sleepInCoffinAt(x, y, z);

				if (enumstatus == EntityPlayer.EnumStatus.OK) {
//					setMetaBasedOnWeirdness(world, x, y, z, true);
					return true;
				} else {
					if (enumstatus == EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW) {
						player.addChatComponentMessage(new ChatComponentTranslation(
								"tile.bed.noSleep", new Object[0]));
					} else if (enumstatus == EntityPlayer.EnumStatus.NOT_SAFE) {
						player.addChatComponentMessage(new ChatComponentTranslation(
								"tile.bed.notSafe", new Object[0]));
					}

					return true;
				}
			} else {
				double d2 = x + 0.5D;
				double d0 = y + 0.5D;
				double d1 = z + 0.5D;
				world.setBlockToAir(x, y, z);

				if (world.getBlock(x, y, z) == this) {
					world.setBlockToAir(x, y, z);
					d2 = (d2 + x + 0.5D) / 2.0D;
					d0 = (d0 + y + 0.5D) / 2.0D;
					d1 = (d1 + z + 0.5D) / 2.0D;
				}

				world.newExplosion((Entity) null, x + 0.5F, y + 0.5F,
						z + 0.5F, 5.0F, true, true);
				return true;
			}
		}
	}

	/**
	 * Returns whether or not this bed block is the head of the bed.
	 */
	public static boolean isBlockHeadOfBed(int p_149975_0_) {
		return (p_149975_0_ & 8) != 0;
	}

	public static boolean func_149976_c(int meta) {
		return (meta & 4) != 0;
	}

//	public static void setMetaBasedOnWeirdness(World world, int x, int y,
//			int z, boolean flag) {
//		int meta = world.getBlockMetadata(x, y, z);
//
//		if (flag) {
//			meta |= 4;
//		} else {
//			meta &= -5;
//		}
//
//		world.setBlockMetadataWithNotify(x, y, z, meta, 4);
//	}

	@Override
	public void onBlockHarvested(World world, int par1, int par2, int par3,
			int par4, EntityPlayer player) {
		if (player.capabilities.isCreativeMode && isBlockHeadOfBed(par4)) {
			// int i1 = getDirection(par4);
			// par1 -= directionalArray[i1][0];
			// par3 -= directionalArray[i1][1];

			if (world.getBlock(par1, par2, par3) == this) {
				world.setBlockToAir(par1, par2, par3);
			}
		}
	}

	// Miscellaneous methods (rendertype etc.)
	@Override
	public int getMobilityFlag() {
		return 2;
	}


	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityCoffin();
	}
}