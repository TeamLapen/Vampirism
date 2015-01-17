package de.teamlapen.vampirism.tileEntity;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

public class TileEntityBloodAltarTier2 extends TileEntity {
	
	private int bloodAmount=0;
	private final int MAX_BLOOD=100;
	private final String KEY_BLOOD_AMOUNT="blood_amount";
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		bloodAmount=nbt.getInteger(KEY_BLOOD_AMOUNT);
		
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger(KEY_BLOOD_AMOUNT, bloodAmount);
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
	
	public int getBloodAmount(){
		return bloodAmount;
	}
	
	/**
	 * Adds blood to the altar
	 * @param amount
	 * @return amount that has actually been added
	 */
	public int addBlood(int amount){
		bloodAmount+=amount;
		if(bloodAmount>MAX_BLOOD){
			amount=bloodAmount-MAX_BLOOD;
			bloodAmount=MAX_BLOOD;
		}
		return amount;
	}
	
	public void startRitual(EntityPlayer p){
		VampirePlayer player=VampirePlayer.get(p);
		int level=player.getLevel();
		if(level<BALANCE.LEVELING.ALTAR_2_MIN_LEVEL||level>BALANCE.LEVELING.ALTAR_2_MAX_LEVEL){
			p.addChatMessage(new ChatComponentText("You can't use this altar on this level"));
			return;
		}
		int neededBlood=BALANCE.LEVELING.A2_getRequiredBlood(level);
		if(bloodAmount<neededBlood){
			p.addChatComponentMessage(new ChatComponentText("There is not enough blood in the altar"));
			return;
		}
		bloodAmount-=neededBlood;
		player.levelUp();
	}
}
