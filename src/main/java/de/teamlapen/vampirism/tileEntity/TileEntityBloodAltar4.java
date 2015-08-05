package de.teamlapen.vampirism.tileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.ModPotion;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.RenderScreenRedPacket;
import de.teamlapen.vampirism.network.SpawnCustomParticlePacket;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCompressed;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.material.MapColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;

import java.util.ArrayList;

/**
 * Tileentity used for BloodAltar 4
 * 
 * @author Max
 *
 */
public class TileEntityBloodAltar4 extends InventoryTileEntity {

	private enum LevReq {
		OK, STRUCTURE_WRONG, ITEM_MISSING, LEVEL_WRONG
	}

	public enum PHASE {
		NOT_RUNNING, PARTICLE_SPREAD, BEAM1, BEAM2, WAITING, LEVELUP, ENDING, CLEAN_UP
	}

	private final static String TAG = "TEBAltar4";

	private final static int[][][] structure1 = new int[][][] { { { 1, 0, 0, 0, 1 }, { 0, 0, 0, 0, 0 }, { 0, 0, 3, 0, 0 }, { 0, 0, 0/* Bed */, 0, 0 }, { 1, 0, 0/* Bed */, 0, 1 } },
			{ { 1, 0, 0, 0, 1 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 1, 0, 0, 0, 1 } },
			{ { 2, 0, 0, 0, 2 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 2, 0, 0, 0, 2 } } };
	private final static int[][][] structure2 = new int[][][] {
			{ { 0, 1, 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 0, 0, 0 }, { 1, 0, 0, 3, 0, 0, 1 }, { 0, 0, 0, 0/* Bed */, 0, 0, 0 }, { 0, 1, 0, 0/* Bed */, 0, 1, 0 } },
			{ { 0, 1, 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 0, 0, 0 }, { 1, 0, 0, 0, 0, 0, 1 }, { 0, 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 0, 1, 0 } },
			{ { 0, 1, 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 0, 0, 0 }, { 1, 0, 0, 0, 0, 0, 1 }, { 0, 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 0, 1, 0 } },
			{ { 0, 2, 0, 0, 0, 2, 0 }, { 0, 0, 0, 0, 0, 0, 0 }, { 2, 0, 0, 0, 0, 0, 2 }, { 0, 0, 0, 0, 0, 0, 0 }, { 0, 2, 0, 0, 0, 2, 0 } } };
	private int runningTick;
	private final int DURATION_TICK = 450;
	public static final int MIN_LEVEL = 4;

	/**
	 * Only available when running ({@link #runningTick}>0
	 */
	private EntityPlayer player;

	/**
	 * Only available when running ({@link #runningTick}>0
	 */
	private ChunkCoordinates[] tips;

	public TileEntityBloodAltar4() {
		super(new Slot[]{new Slot(ModItems.pureBlood, 44, 34), new Slot(ModItems.humanHeart, 80, 34), new Slot(new InventoryTileEntity.IItemSelector() {

			@Override
			public boolean isItemAllowed(ItemStack item) {
				// Placeholder
				return false;
			}
		}, 116, 34)});
	}

	/**
	 * Checks if the given amount of items is present and if that's the case removes them
	 * 
	 * @param bloodMeta
	 *            The meta value of the pure blood bottles which is required
	 * @param blood
	 * @param heart
	 * @param par3
	 * @return
	 */
	private boolean checkAndRemoveItems(int bloodMeta, int blood, int heart, int par3) {
		ItemStack stackPureBlood = this.getStackInSlot(0);
		ItemStack stackHeart = this.getStackInSlot(1);
		ItemStack stack3 = this.getStackInSlot(2);

		if (blood > 0) {
			if (stackPureBlood == null || stackPureBlood.stackSize < blood) {
				//Logger.d(TAG, "Pure blood bottles are not present");
				return false;
			}
			if (stackPureBlood.getItemDamage() < bloodMeta) {
				//Logger.d(TAG, "Pure blood is of the wrong level (" + stackPureBlood.getItemDamage() + "/" + bloodMeta + ")");
				return false;
			}
		}
		if (heart > 0) {
			if (stackHeart == null || stackHeart.stackSize < heart) {
				//Logger.d(TAG, "Hearts are not present");
				return false;
			}
		}
		if (par3 > 0) {
			if (stack3 == null || stack3.stackSize < par3) {
				//Logger.d(TAG, "Item 3 is not present");
				return false;
			}
		}
		this.decrStackSize(0, blood);
		this.decrStackSize(1, heart);
		this.decrStackSize(2, par3);
		return true;
	}


	/**
	 * Checks if the requirements for a level up are met
	 * 
	 * IF CHANGED MAKE SURE TO ADJUST THE GUIDE AS WELL {@link de.teamlapen.vampirism.guide.VampirismGuide#createItemRequirementsAltar4()}
	 * @param player
	 * @param sl
	 *            Level of the structure
	 * @return
	 */
	private LevReq checkLevelRequirement(EntityPlayer player, int sl) {
		if (sl == 0)
			return LevReq.STRUCTURE_WRONG;

		int pl = VampirePlayer.get(player).getLevel();
		if (pl < MIN_LEVEL || pl > REFERENCE.HIGHEST_REACHABLE_LEVEL)
			return LevReq.LEVEL_WRONG;
		if (pl == 4) {
			if (sl != 1)
				return LevReq.STRUCTURE_WRONG;
			if (!checkAndRemoveItems(0, 0, 5, 0))
				return LevReq.ITEM_MISSING;
		} else if (pl == 5) {
			if (sl != 1)
				return LevReq.STRUCTURE_WRONG;
			if (!checkAndRemoveItems(0, 1, 0, 0))
				return LevReq.ITEM_MISSING;
		} else if (pl == 6) {
			if (sl != 1)
				return LevReq.STRUCTURE_WRONG;
			if (!checkAndRemoveItems(0, 1, 5, 0))
				return LevReq.ITEM_MISSING;
		} else if (pl == 7) {
			if (sl != 2)
				return LevReq.STRUCTURE_WRONG;
			if (!checkAndRemoveItems(1, 1, 0, 0))
				return LevReq.ITEM_MISSING;
		} else if (pl == 8) {
			if (sl != 2)
				return LevReq.STRUCTURE_WRONG;
			if (!checkAndRemoveItems(1, 1, 5, 0))
				return LevReq.ITEM_MISSING;
		} else if (pl == 9) {
			if (sl != 3)
				return LevReq.STRUCTURE_WRONG;
			if (!checkAndRemoveItems(2, 1, 5, 0))
				return LevReq.ITEM_MISSING;
		} else if (pl == 10) {
			if (sl != 3)
				return LevReq.STRUCTURE_WRONG;
			if (!checkAndRemoveItems(2, 1, 5, 0))
				return LevReq.ITEM_MISSING;
		} else if (pl == 11) {
			if (sl != 4)
				return LevReq.STRUCTURE_WRONG;
			if (!checkAndRemoveItems(3, 1, 10, 0))
				return LevReq.ITEM_MISSING;
		} else if (pl == 12) {
			if (sl != 4)
				return LevReq.STRUCTURE_WRONG;
			if (!checkAndRemoveItems(3, 1, 5, 0))
				return LevReq.ITEM_MISSING;
		} else if (pl == 13) {
			if (sl != 4)
				return LevReq.STRUCTURE_WRONG;
			if (!checkAndRemoveItems(4, 2, 0, 0))
				return LevReq.ITEM_MISSING;
		}
		return LevReq.OK;

	}

	private int determineLevel2() {
		int x = this.xCoord;
		int y = this.yCoord;
		int z = this.zCoord;
		Block type = null;
		ChunkCoordinates[] pos = findTips();
		if (pos.length != 6 && pos.length != 4) {
			Logger.d(TAG, "The tip count is wrong");
			return 0;
		}
		boolean large = pos.length == 6;
		for (ChunkCoordinates p : pos) {
			for (int i = 1; i <= (large ? 3 : 2); i++) {
				Block b = worldObj.getBlock(p.posX, p.posY - i, p.posZ);
				if (type == null) type = b;
				else {
					Logger.d(TAG, "Looking for %s but found %s at %d %d %d", type.getUnlocalizedName(), b.getUnlocalizedName(), p.posX, p.posY - i, p.posZ);
					if (!type.equals(b)) return 0;
				}
			}
		}
		if (large) {
			if (type instanceof BlockCompressed) {
				if (type.getMapColor(1).equals(MapColor.ironColor)) {
					return 3;
				}
				if (type.getMapColor(1).equals(MapColor.goldColor)) {
					return 4;
				}
			}
		} else {
			if (type instanceof BlockStoneBrick) {
				return 1;
			}
			if (type instanceof BlockCompressed) {
				if (type.getMapColor(1).equals(MapColor.ironColor)) {
					return 2;
				}
			}
		}
		return 0;
	}

	/**
	 * Rotate the lower 2x2 matrix by 90 degrees
	 * @param matrix
	 * @return
	 */
	private int[][][] rotateBy90(int[][][] matrix){
		int[][][] result=new int[matrix.length][][];
		for(int y=0;y<matrix.length;y++){
			result[y]=rotateBy90(matrix[y]);
		}
		return result;
	}

	/**
	 * Rotate the MxN matrix by 90 degrees
	 * @param matrix
	 * @return
	 */
	private int[][] rotateBy90(int[][] matrix){
		int M=matrix.length;
		int N=matrix[0].length;
		int[][] result = new int[N][M];
		for(int m=0;m<M;m++){
			for(int n=0;n<N;n++){
				result[N-1-n][m]=matrix[m][n];
			}
		}
		return result;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}

	@Override
	public String getInventoryName() {
		return "block.vampirism.bloodAltarTier4.name";
	}

	/**
	 * Returns the phase the ritual is in
	 * 
	 * @return
	 */
	public PHASE getPhase() {
		if (runningTick < 1) {
			return PHASE.NOT_RUNNING;
		}
		if (runningTick == 1) {
			return PHASE.CLEAN_UP;
		}
		if (runningTick > (DURATION_TICK - 100)) {
			return PHASE.PARTICLE_SPREAD;
		}
		if (runningTick < DURATION_TICK - 160 && runningTick >= (DURATION_TICK - 200)) {
			return PHASE.BEAM1;
		}
		if (runningTick < (DURATION_TICK - 200) && (runningTick > 50)) {
			return PHASE.BEAM2;
		}
		if (runningTick == 50) {
			return PHASE.LEVELUP;
		}
		if (runningTick < 50) {
			return PHASE.ENDING;
		}
		return PHASE.WAITING;
	}

	/**
	 * Returns the affected player. If the ritual isn't running it returns null
	 * 
	 * @return
	 */
	public EntityPlayer getPlayer() {
		if (this.runningTick <= 1)
			return null;
		return this.player;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	public int getRunningTick() {
		return runningTick;
	}

	/**
	 * Returns the position of the tips. If the ritual isn't running it returns null
	 * 
	 * @return
	 */
	public ChunkCoordinates[] getTips() {
		if (this.runningTick <= 1)
			return null;
		return this.tips;
	}

	/**
	 * Searchs for the tips and returns their position
	 *
	 * @return
	 */
	private ChunkCoordinates[] findTips() {
		ArrayList<ChunkCoordinates> coord = new ArrayList<ChunkCoordinates>();
		int lx = this.xCoord - 3;
		int ly = this.yCoord;
		int lz = this.zCoord - 3;
		int hx = lx + 6;
		int hy = ly + 4;
		int hz = lz + 6;

		for (int x = lx; x <= hx; x++) {
			for (int z = lz; z <= hz; z++) {
				for (int y = ly; y <= hy; y++) {
					if (worldObj.getBlock(x, y, z).equals(ModBlocks.bloodAltar4Tip)) {
						coord.add(new ChunkCoordinates(x, y, z));
					}
				}
			}
		}
		return coord.toArray(new ChunkCoordinates[coord.size()]);
	}

	/**
	 * Called when the ritual should start
	 * 
	 * @param player
	 */
	public void onBlockActivated(EntityPlayer player) {
		if (runningTick > 0) {
			return;
		}
		int sl = 0;
		try {
			sl = this.determineLevel2();
		} catch (Exception e) {
			Logger.e(TAG, e, "Failed to determine level");
		}
		LevReq result = checkLevelRequirement(player, sl);
		Logger.d(TAG, "SL: " + sl + " Result: " + result);
		if (result != LevReq.OK) {
			if (!this.worldObj.isRemote) {
				if (result == LevReq.ITEM_MISSING)
					player.addChatMessage(new ChatComponentTranslation("text.vampirism.ritual_missing_times"));
				if (result == LevReq.STRUCTURE_WRONG)
					player.addChatMessage(new ChatComponentTranslation("text.vampirism.ritual_structure_wrong"));
				if (result == LevReq.LEVEL_WRONG)
					player.addChatMessage(new ChatComponentTranslation("text.vampirism.ritual_level_wrong"));
			}

			return;
		}
		runningTick = DURATION_TICK;
		this.player = player;
		tips = findTips();
		if (!this.worldObj.isRemote) {
			for (int i = 0; i < tips.length; i++) {
				NBTTagCompound data = new NBTTagCompound();
				data.setInteger("destX", tips[i].posX);
				data.setInteger("destY", tips[i].posY);
				data.setInteger("destZ", tips[i].posZ);
				data.setInteger("age", 100);
				VampirismMod.modChannel.sendToAll(new SpawnCustomParticlePacket(1, this.xCoord, this.yCoord, this.zCoord, 5, data));
			}
			this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		player.addPotionEffect(new PotionEffect(Potion.resistance.id, DURATION_TICK, 10));
		this.markDirty();

	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		int tick = tagCompound.getInteger("tick");
		if (tick > 0 && player == null) {
			try {
				this.player = (EntityPlayer) this.worldObj.getEntityByID(tagCompound.getInteger("playerId"));
				this.tips = findTips();
				this.runningTick = tick;
			} catch (NullPointerException e) {
			}
		}
		if (player == null) {
			this.runningTick = 0;
			this.tips = null;
		}
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
	@SuppressWarnings("unused")
	private void setBlocks(int lx, int ly, int lz, int hx, int hy, int hz, int[][][] structure) {

		for (int x = lx; x <= hx; x++) {
			for (int z = lz; z <= hz; z++) {
				for (int y = ly; y < hy; y++) {
					int type = structure[y - ly][z - lz][x - lx];
					if (type == 0) {
						worldObj.setBlock(x, y, z, Blocks.air);
					}
					if (type == 2) {
						worldObj.setBlock(x, y, z, ModBlocks.bloodAltar4Tip);
					}
					if (type == 3) {
						worldObj.setBlock(x, y, z, ModBlocks.bloodAltar4);
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

	@Override
	public void updateEntity() {
		runningTick--;
		if (runningTick <= 0)
			return;
		if (player == null || player.isDead) {
			runningTick = 1;
		} else {
			player.motionX = 0;
			if (player.motionY >= 0) {
				player.motionY = 0;
			} else {
				player.motionY = player.motionY / 2;
			}
			player.motionZ = 0;
		}

		PHASE phase = getPhase();
		if (!this.worldObj.isRemote) {
			if (phase.equals(PHASE.PARTICLE_SPREAD)) {
				if (runningTick % 15 == 0) {
					for (int i = 0; i < tips.length; i++) {
						NBTTagCompound data = new NBTTagCompound();
						data.setInteger("destX", tips[i].posX);
						data.setInteger("destY", tips[i].posY);
						data.setInteger("destZ", tips[i].posZ);
						data.setInteger("age", 60);
						VampirismMod.modChannel.sendToAll(new SpawnCustomParticlePacket(1, this.xCoord, this.yCoord, this.zCoord, 5, data));
					}
				}
			}
			if (runningTick == DURATION_TICK - 200) {
				VampirismMod.modChannel.sendTo(new RenderScreenRedPacket(DURATION_TICK - 250, 50), (EntityPlayerMP) player);
			}
		}

		if (phase.equals(PHASE.CLEAN_UP)) {
			player = null;
			tips = null;
			this.markDirty();
		}
		if (phase.equals(PHASE.LEVELUP)) {
			VampirePlayer.get(player).levelUp();
			if (this.worldObj.isRemote) {
				this.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "random.explode", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
				this.worldObj.spawnParticle("hugeexplosion", player.posX, player.posY, player.posZ, 1.0D, 0.0D, 0.0D);
			} else {
				player.addPotionEffect(new PotionEffect(ModPotion.saturation.id, 400, 2));
			}
			player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 400, 2));
			player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 400, 2));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("tick", runningTick);
		if (player != null) {
			tagCompound.setInteger("playerId", player.getEntityId());
		}
	}

}
