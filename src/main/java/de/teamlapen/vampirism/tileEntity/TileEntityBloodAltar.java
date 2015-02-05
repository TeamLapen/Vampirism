package de.teamlapen.vampirism.tileEntity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
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
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.item.ItemVampiresFear;
import de.teamlapen.vampirism.network.BloodAltarPacket;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * TileEntity BloodAltar: Initial vampire ritual
 * @author Mistadon
 */
public class TileEntityBloodAltar extends TileEntity {
	private boolean occupied = false;
	public final String BLOODALTAR_OCCUPIED_NBTKEY = "bloodaltaroccupied";
	private final double DISTANCE_AROUND_ALTAR = 5.0;
	private final int LIGHTNINGBOLT_AMOUNT = 10;
	private final String TAG = "TEBloodAltar";

	public TileEntityBloodAltar() {
		super();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.occupied = nbt.getBoolean(BLOODALTAR_OCCUPIED_NBTKEY);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean(BLOODALTAR_OCCUPIED_NBTKEY, occupied);
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean flag, EntityPlayer player) {
		if (flag != occupied && player != null)
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
		
		//Put sword into altar
		player.inventory.consumeInventoryItem(itemStack.getItem());
		setOccupied(true, player);
		
		//Load villagers and spawn lighting bolts on them, not really used atm
		List entityList = getWorldObj().loadedEntityList;
		ArrayList<EntityVillager> list = getVillagersInRadius(entityList,
				DISTANCE_AROUND_ALTAR);
		for (EntityVillager v : list) {
			for (int i = 0; i < LIGHTNINGBOLT_AMOUNT; i++)
				getWorldObj().addWeatherEffect(
						new EntityLightningBolt(getWorldObj(), v.posX, v.posY,
								v.posZ));
		}
		//Check the needed conditions
		if (!(this.worldObj.isDaytime())
				&& ItemVampiresFear.getBlood(itemStack) >= BALANCE.LEVELING.ALTAR_1_BLOOD && list.size()>=BALANCE.LEVELING.R1_VILLAGERS) {
			//Conditions met, level up +effect
			VampirePlayer vp=VampirePlayer.get(player);
			if(vp.getLevel()==0){
				vp.levelUp();
			}
			player.addPotionEffect(new PotionEffect(Potion.resistance.id,30,2));
			this.worldObj.createExplosion(null, xCoord, yCoord, zCoord, 5.0F,false);
			Logger.i(TAG,
					"Ritual ended, player is now a vampire: ");
		} else {
			//Drop sword
			itemStack.stackSize=1;
            EntityItem entityitem = new EntityItem(this.worldObj, this.xCoord,this.yCoord+1, this.zCoord, itemStack);
            entityitem.delayBeforeCanPickup = 10;
			this.worldObj.spawnEntityInWorld(entityitem);
			this.setOccupied(false, player);
			Logger.i(TAG, "Not daytime or not enough blood or not enough villagers, ritual will fail: "+(!this.worldObj.isDaytime())+":"+ItemVampiresFear.getBlood(itemStack)+":"+list.size());
		}
	}

	/**
	 * Gets a list of all the villagers in entityList that are near this TileEntity
	 * @param entityList A list of all the entities to check
	 * @param distance The maximum distance from this TileEntity
	 * @return List with with all the villagers that are found around this TileEntity
	 */
	private ArrayList<EntityVillager> getVillagersInRadius(List entityList,
			double distance) {
		ArrayList<EntityVillager> list = new ArrayList<EntityVillager>();
		for (Object entity : entityList) {
			if (EntityVillager.class.isInstance(entity)) {
				EntityVillager v = (EntityVillager) entity;
				if (Math.sqrt(Math.pow(v.posX - xCoord, 2)
						+ Math.pow(v.posY - yCoord, 2)
						+ Math.pow(v.posZ - zCoord, 2)) <= distance)
					list.add((EntityVillager) entity);
			}
		}
		Logger.i(TAG, list.size() + "villagers found in a " + distance
				+ " block radius around the altar");
		return list;
	}

	@Override
	public void onDataPacket(NetworkManager net,
			S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord,
				this.zCoord, 1, nbtTag);
	}
}
