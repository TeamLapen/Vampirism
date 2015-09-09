package de.teamlapen.vampirism.tileEntity;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.ModPotion;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.item.ItemLeechSword;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;
import java.util.List;

/**
 * Beacon style blood altar
 * 
 * @author maxanier @author Mistadon
 */
public class TileEntityBloodAltar1 extends TileEntity implements IUpdatePlayerListBox {
	private boolean occupied = false;
	private int bloodAmount;
	public final String OCCUPIED_NBTKEY = "occupied";
	public final String BLOOD_NBTKEY = "blood";
	public final String TICK_NBTKEY = "tick";
	public final String INFINITE_NBTKEY = "infinite";
	private final String TAG = "TEBloodAltar";
	public int distance = 25;
	private int tickCounter = 0;
	private TileEntityBeacon fakeBeacon;

	public boolean isInfinite() {
		return infinite;
	}


	/**
	 * If true the altar does not consume any blood from the sword.
	 * Reset when the sword is taken out.
	 */
	private boolean infinite;

	public TileEntityBloodAltar1() {
		super();
		infinite=false;
	}

	public void dropSword() {
		if (this.isOccupied()) {
			EntityItem sword = new EntityItem(this.worldObj, pos.getX(), pos.getY() + 1, pos.getZ(), getSwordToEject());
			this.worldObj.spawnEntityInWorld(sword);
			infinite=false;
		}
	}

	public int getBloodLeft() {
		return bloodAmount;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new S35PacketUpdateTileEntity(pos, 1, nbtTag);
	}

	@SideOnly(Side.CLIENT)
	public TileEntityBeacon getFakeBeacon() {
		return fakeBeacon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	private ItemStack getSwordToEject() {
		ItemStack s = new ItemStack(ModItems.leechSword, 1);
		ItemLeechSword.setBlood(s, bloodAmount);
		bloodAmount = 0;
		return s;
	}

	public boolean isActive() {
		return bloodAmount > 0;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void makeInfinite(){
		this.infinite=true;
		this.bloodAmount=ItemLeechSword.MAX_BLOOD;
		this.occupied=true;
		this.markDirty();
	}

	/**
	 * Marks the block dirty and ready for update
	 */
	@Override
	public void markDirty() {
		super.markDirty();
		this.worldObj.markBlockForUpdate(this.pos);
	}

	public void onActivated(EntityPlayer player, ItemStack itemStack) {
		if (occupied) {
			if (itemStack == null) {
				player.inventory.setInventorySlotContents(player.inventory.currentItem, getSwordToEject());
			} else {
				dropSword();
			}
			occupied = false;
			markDirty();
		} else if (itemStack != null) {
			if (ModItems.leechSword.equals(itemStack.getItem())) {
				this.startRitual(player, itemStack);
			}
		}
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.occupied = nbt.getBoolean(OCCUPIED_NBTKEY);
		this.bloodAmount = nbt.getInteger(BLOOD_NBTKEY);
		this.tickCounter = nbt.getInteger(TICK_NBTKEY);
		if(nbt.hasKey(INFINITE_NBTKEY)){
			this.infinite = nbt.getBoolean(INFINITE_NBTKEY);
		}
	}

	@Override
	public void setWorldObj(World world) {
		super.setWorldObj(world);
		if (world.isRemote) {
			fakeBeacon = new TileEntityBeacon();
			Helper.Reflection.setPrivateField(TileEntityBeacon.class, fakeBeacon, true, Helper.Obfuscation.getPosNames("TileEntityBeacon/field_146015_k"));
			fakeBeacon.setWorldObj(world);
		}
	}

	public void startRitual(EntityPlayer player, ItemStack itemStack) {
		if (VampirePlayer.get(player).getLevel() == 0) {
			player.addChatMessage(new ChatComponentTranslation("text.vampirism.ritual_no_vampire"));
			return;
		}
		// Put sword into altar
		player.inventory.consumeInventoryItem(itemStack.getItem());
		occupied = true;
		this.bloodAmount = ItemLeechSword.getBlood(itemStack);

		markDirty();
	}


	@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
	}



	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean(OCCUPIED_NBTKEY, occupied);
		nbt.setInteger(BLOOD_NBTKEY, bloodAmount);
		nbt.setInteger(TICK_NBTKEY, tickCounter);
		nbt.setBoolean(INFINITE_NBTKEY,infinite);
	}

	@Override
	public void update() {
		if (this.worldObj.getTotalWorldTime() % 100L == 0L && !this.worldObj.isRemote) {
			if (bloodAmount > 0) {
				if(!infinite){
					bloodAmount--;
				}
				if (bloodAmount == 0) {
					this.markDirty();
				}
				AxisAlignedBB axisalignedbb = new AxisAlignedBB(pos,pos.add(1,worldObj.getHeight(),1)).expand(distance, distance,
						distance);
				List list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
				Iterator iterator = list.iterator();
				EntityPlayer entityplayer;

				while (iterator.hasNext()) {
					entityplayer = (EntityPlayer) iterator.next();
					VampirePlayer vampire = VampirePlayer.get(entityplayer);
					if (vampire.getLevel() > 0) {
						entityplayer.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 120, 0, true,false));
						entityplayer.addPotionEffect(new PotionEffect(ModPotion.saturation.id, 120, 1, true,false));
					}
				}
			}

		}
	}
}
