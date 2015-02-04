package de.teamlapen.vampirism.tileEntity;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.SpawnCustomParticlePacket;
import de.teamlapen.vampirism.network.SpawnParticlePacket;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

/**
 * TileEntity for Tier2 BloodAltar
 * @author Maxanier
 */
public class TileEntityBloodAltarTier2 extends TileEntity {
	
	private int bloodAmount=0;
	private final int MAX_BLOOD=100;
	private final String KEY_BLOOD_AMOUNT="blood_amount";
	private int ritualTicksLeft=0;
	private EntityPlayer ritualPlayer;
	private final int RITUAL_TIME=60;
	
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
	
	public void setBlood(int amount)  {
		bloodAmount = amount;
	}
	
	public void startRitual(EntityPlayer p){
		if(ritualTicksLeft>0)return;
		
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
		
		NBTTagCompound data=new NBTTagCompound();
		data.setInteger("player_id", p.getEntityId());
		VampirismMod.modChannel.sendToAll(new SpawnCustomParticlePacket(0,this.xCoord,this.yCoord,this.zCoord,40,data));
		
		ritualPlayer=p;
		ritualTicksLeft=RITUAL_TIME;
	}
	
	@Override
	public void updateEntity(){
		if(ritualTicksLeft==0)return;
		
		switch(ritualTicksLeft){
		case 5:
			getWorldObj().addWeatherEffect(
					new EntityLightningBolt(getWorldObj(), this.xCoord,this.yCoord,this.zCoord));
			ritualPlayer.setHealth(ritualPlayer.getMaxHealth());
			
			VampirePlayer.get(ritualPlayer).getBloodStats().addBlood(VampirePlayer.MAXBLOOD);
			
			break;
		case 1:
			VampirePlayer player=VampirePlayer.get(ritualPlayer);
			bloodAmount-=BALANCE.LEVELING.A2_getRequiredBlood(player.getLevel());
			ritualPlayer.addPotionEffect(new PotionEffect(Potion.regeneration.id,player.getLevel()*5));
			player.levelUp();
			break;
		}
		
		ritualTicksLeft--;
	}
	
	public int getMaxBlood() {
		return MAX_BLOOD;
	}
	
}
