package de.teamlapen.vampirism.tileEntity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.item.ItemVampiresFear;
import de.teamlapen.vampirism.network.BloodAltarPacket;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Logger;

/**
 * TileEntity BloodAltar: Initial vampire ritual
 * 
 * @author Mistadon
 */
public class TileEntityBloodAltar extends TileEntity {
	private boolean occupied = false;
	public final String BLOODALTAR_OCCUPIED_NBTKEY = "bloodaltaroccupied";
	private int tickCounter = 0;
	private final int TICK_DURATION = 40;
	private final String TAG = "TEBloodAltar";

	public TileEntityBloodAltar() {
		super();
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord,
				this.zCoord, 1, nbtTag);
	}

	public boolean isOccupied() {
		return occupied;
	}

	@Override
	public void onDataPacket(NetworkManager net,
			S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
	}

	public void setOccupied(boolean flag, boolean sendPacket) {
		if (sendPacket)
			VampirismMod.modChannel.sendToAll(new BloodAltarPacket(flag,
					this.xCoord, this.yCoord, this.zCoord));
		occupied = flag;
	}

	/**
	 * Code for the vampirism ritual to start
	 * 
	 * @param player
	 *            Player who started the ritual and will become a vampire
	 * @param itemStack
	 *            The sword's ItemStack that is going to be consumed
	 **/
	public void startVampirismRitual(EntityPlayer player, ItemStack itemStack) {
		Logger.i(TAG, "Starting Vampirism-Ritual");
		if (VampirePlayer.get(player).getLevel() == 0) {
			player.addChatMessage(new ChatComponentTranslation(
					"text.vampirism:ritual_no_vampire"));
			return;
		}
		// Put sword into altar
		player.inventory.consumeInventoryItem(itemStack.getItem());
		setOccupied(true, true);
		
		// TODO small animation
		this.worldObj.spawnEntityInWorld(new EntityLightningBolt(worldObj, player.posX, player.posY, player.posZ));

		if(ItemVampiresFear.MAX_BLOOD <= ItemVampiresFear.getBlood(itemStack))
			player.addPotionEffect(new PotionEffect(Potion.regeneration.id,
					20 * 60, 1));
	}

	public void ejectSword() {
		if(!this.worldObj.isRemote) {
			EntityItem sword = new EntityItem(this.worldObj, this.xCoord, this.yCoord + 1, this.zCoord, new ItemStack(ModItems.vampiresFear, 1));
			sword.delayBeforeCanPickup = 10;
			this.worldObj.spawnEntityInWorld(sword);
		}
		this.setOccupied(false, true);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean(BLOODALTAR_OCCUPIED_NBTKEY, occupied);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.occupied = nbt.getBoolean(BLOODALTAR_OCCUPIED_NBTKEY);
	}

	@Override
	public void updateEntity() {
		if (this.occupied)
			tickCounter++;
		if (tickCounter > TICK_DURATION && occupied) {
			ejectSword();
			tickCounter = 0;
		}
	}
}
