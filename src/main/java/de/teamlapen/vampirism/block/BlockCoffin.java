package de.teamlapen.vampirism.block;

import java.util.Iterator;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

/**
 * 
 * @author Moritz
 *
 */
public class BlockCoffin extends BlockDirectional {
	public static final int[][] directionalArray = new int[][] { { 0, 1 },
			{ -1, 0 }, { 0, -1 }, { 1, 0 } };

	public BlockCoffin() {
		super(Material.rock);
		this.setTheseBlockBounds();
	}

	@Override
	public boolean onBlockActivated(World world, int blockX, int blockY,
			int blockZ, EntityPlayer player, int par4, float f1, float f2,
			float f3) {
		if (world.isRemote) {
			return true;
		} else {
			// Checks the direction of the coffin, searches for the head of the
			// bed (weird code, I know) and gets the metadata of the bed's head
			int meta = world.getBlockMetadata(blockX, blockY, blockZ);

			if (!isBlockHeadOfBed(meta)) {
				int direction = getDirection(meta);
				blockX += directionalArray[direction][0];
				blockZ += directionalArray[direction][1];

				if (world.getBlock(blockX, blockY, blockZ) != this)
					return true;

				meta = world.getBlockMetadata(blockX, blockY, blockZ);
			}

			if (world.provider.canRespawnHere()
					&& world.getBiomeGenForCoords(blockX, blockZ) != BiomeGenBase.hell) {
				if (func_149976_c(meta)) {
					EntityPlayer playerSleepingHere = null;
					Iterator iterator = world.playerEntities.iterator();

					while (iterator.hasNext()) {
						EntityPlayer tempPlayer = (EntityPlayer) iterator
								.next();

						if (tempPlayer.isPlayerSleeping()) {
							ChunkCoordinates chunkcoordinates = tempPlayer.playerLocation;

							if (chunkcoordinates.posX == blockX
									&& chunkcoordinates.posY == blockY
									&& chunkcoordinates.posZ == blockZ) {
								playerSleepingHere = tempPlayer;
							}
						}
					}

					if (playerSleepingHere != null) {
						player.addChatComponentMessage(new ChatComponentTranslation(
								"tile.bed.occupied", new Object[0]));
						return true;
					}

					setMetaBasedOnWeirdness(world, blockX, blockY, blockZ, false);
				}

				EntityPlayer.EnumStatus enumstatus = VampirePlayer.get(player).sleepInCoffinAt(
						blockX, blockY, blockZ);

				if (enumstatus == EntityPlayer.EnumStatus.OK) {
					setMetaBasedOnWeirdness(world, blockX, blockY, blockZ, true);
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
				double d2 = blockX + 0.5D;
				double d0 = blockY + 0.5D;
				double d1 = blockZ + 0.5D;
				world.setBlockToAir(blockX, blockY, blockZ);
				int k1 = getDirection(meta);
				blockX += directionalArray[k1][0];
				blockZ += directionalArray[k1][1];

				if (world.getBlock(blockX, blockY, blockZ) == this) {
					world.setBlockToAir(blockX, blockY, blockZ);
					d2 = (d2 + blockX + 0.5D) / 2.0D;
					d0 = (d0 + blockY + 0.5D) / 2.0D;
					d1 = (d1 + blockZ + 0.5D) / 2.0D;
				}

				world.newExplosion((Entity) null, blockX + 0.5F, blockY + 0.5F,
						blockZ + 0.5F, 5.0F, true, true);
				return true;
			}
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which
	 * neighbor changed (coordinates passed are their own) Args: x, y, z,
	 * neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_,
			int p_149695_3_, int p_149695_4_, Block p_149695_5_) {
		int l = p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_,
				p_149695_4_);
		int i1 = getDirection(l);

		if (isBlockHeadOfBed(l)) {
			if (p_149695_1_.getBlock(p_149695_2_ - directionalArray[i1][0],
					p_149695_3_, p_149695_4_ - directionalArray[i1][1]) != this) {
				p_149695_1_
						.setBlockToAir(p_149695_2_, p_149695_3_, p_149695_4_);
			}
		} else if (p_149695_1_.getBlock(p_149695_2_ + directionalArray[i1][0],
				p_149695_3_, p_149695_4_ + directionalArray[i1][1]) != this) {
			p_149695_1_.setBlockToAir(p_149695_2_, p_149695_3_, p_149695_4_);

			if (!p_149695_1_.isRemote) {
				this.dropBlockAsItem(p_149695_1_, p_149695_2_, p_149695_3_,
						p_149695_4_, l, 0);
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

	public static void setMetaBasedOnWeirdness(World world, int x, int y, int z,
			boolean flag) {
		int meta = world.getBlockMetadata(x, y, z);

		if (flag) {
			meta |= 4;
		} else {
			meta &= -5;
		}

		world.setBlockMetadataWithNotify(x, y, z, meta, 4);
	}

	/**
	 * public static ChunkCoordinates func_149977_a(World p_149977_0_, int
	 * p_149977_1_, int p_149977_2_, int p_149977_3_, int p_149977_4_) { int i1
	 * = p_149977_0_.getBlockMetadata(p_149977_1_, p_149977_2_, p_149977_3_);
	 * int j1 = BlockDirectional.getDirection(i1);
	 * 
	 * for (int k1 = 0; k1 <= 1; ++k1) { int l1 = p_149977_1_ -
	 * field_149981_a[j1][0] * k1 - 1; int i2 = p_149977_3_ -
	 * field_149981_a[j1][1] * k1 - 1; int j2 = l1 + 2; int k2 = i2 + 2;
	 * 
	 * for (int l2 = l1; l2 <= j2; ++l2) { for (int i3 = i2; i3 <= k2; ++i3) {
	 * if (World.doesBlockHaveSolidTopSurface(p_149977_0_, l2, p_149977_2_ - 1,
	 * i3) && !p_149977_0_.getBlock(l2, p_149977_2_, i3)
	 * .getMaterial().isOpaque() && !p_149977_0_.getBlock(l2, p_149977_2_ + 1,
	 * i3) .getMaterial().isOpaque()) { if (p_149977_4_ <= 0) { return new
	 * ChunkCoordinates(l2, p_149977_2_, i3); }
	 * 
	 * --p_149977_4_; } } } }
	 * 
	 * return null; }
	 **/

	@Override
	public void onBlockHarvested(World world, int par1, int par2, int par3,
			int par4, EntityPlayer player) {
		if (player.capabilities.isCreativeMode && isBlockHeadOfBed(par4)) {
			int i1 = getDirection(par4);
			par1 -= directionalArray[i1][0];
			par3 -= directionalArray[i1][1];

			if (world.getBlock(par1, par2, par3) == this) {
				world.setBlockToAir(par1, par2, par3);
			}
		}
	}

	// Miscellaneous methods (rendertype etc.)
	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getMobilityFlag() {
		return 2;
	}

	private void setTheseBlockBounds() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_,
			int p_149719_2_, int p_149719_3_, int p_149719_4_) {
		this.setTheseBlockBounds();
	}
}