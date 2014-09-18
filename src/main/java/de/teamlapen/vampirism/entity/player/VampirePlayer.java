package de.teamlapen.vampirism.entity.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.network.SpawnParticlePacket;
import de.teamlapen.vampirism.proxy.CommonProxy;
import de.teamlapen.vampirism.util.Logger;

public class VampirePlayer implements IExtendedEntityProperties {

	public final static String EXT_PROP_NAME = "VampirePlayer";
	/**
	 * 
	 * @param player
	 * @return VampirePlayer property of player
	 */
	public static final VampirePlayer get(EntityPlayer player) {
		return (VampirePlayer) player.getExtendedProperties(VampirePlayer.EXT_PROP_NAME);
	}
	private static final String getSaveKey(EntityPlayer player) {
		// no longer a username field, so use the command sender name instead:
		return player.getCommandSenderName() + ":" + EXT_PROP_NAME;
	}
	public static final void loadProxyData(EntityPlayer player) {
		VampirePlayer playerData = VampirePlayer.get(player);
		NBTTagCompound savedData = CommonProxy.getEntityData(getSaveKey(player));
		if (savedData != null) {
			playerData.loadNBTData(savedData);

		}
		playerData.applyModifiers(playerData.getLevel());
	}
	/**
	 * Registers vampire property to player
	 * 
	 * @param player
	 */
	public static final void register(EntityPlayer player) {
		player.registerExtendedProperties(VampirePlayer.EXT_PROP_NAME, new VampirePlayer(player));
	}
	public static void saveProxyData(EntityPlayer player) {
		VampirePlayer playerData = VampirePlayer.get(player);
		NBTTagCompound savedData = new NBTTagCompound();
		playerData.saveNBTData(savedData);
		CommonProxy.storeEntityData(getSaveKey(player), savedData);
	}
	private final EntityPlayer player;

	private final String KEY_LEVEL = "level";

	private final String KEY_BLOOD = "blood";

	private final int MAXBLOOD = 20;

	private final static int BLOOD_WATCHER = 20;

	private final static int LEVEL_WATCHER = 21;
	
	private BloodStats bloodStats;

	public VampirePlayer(EntityPlayer player) {
		this.player = player;
		this.player.getDataWatcher().addObject(BLOOD_WATCHER, MAXBLOOD);
		this.player.getDataWatcher().addObject(LEVEL_WATCHER, 0);
		bloodStats=new BloodStats();
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void addBlood(int a) {
		int blood = getBlood();
		setBlood(Math.min(blood+a, MAXBLOOD));
	}

	private void applyModifiers(int level) {
		PlayerModifiers.applyModifiers(level, player);

	}

	private int getBlood() {
		return this.player.getDataWatcher().getWatchableObjectInt(BLOOD_WATCHER);
	}

	public int getLevel() {
		return this.player.getDataWatcher().getWatchableObjectInt(LEVEL_WATCHER);
	}
	
	public BloodStats getBloodStats(){
		return bloodStats;
	}

	@Override
	public void init(Entity entity, World world) {

	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent e){
		if(e.entity==player){
			onUpdate();
		}
	}
	
	private void onUpdate(){
		if(getLevel()>0){
			this.bloodStats.onUpdate();
		}
		
	}

	public void levelUp() {
		int level = getLevel();
		level++;
		this.applyModifiers(level);
		setLevel(level);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		setBlood(properties.getInteger(KEY_LEVEL));
		setLevel(properties.getInteger(KEY_BLOOD));
		this.bloodStats.readNBT(properties);

	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setInteger(KEY_LEVEL, getLevel());
		properties.setInteger(KEY_BLOOD, getBlood());
		this.bloodStats.writeNBT(properties);
		compound.setTag(EXT_PROP_NAME, properties);

	}

	private void setBlood(int b) {
			this.player.getDataWatcher().updateObject(BLOOD_WATCHER, b);
		
	}

	/**
	 * For testing only, make private later
	 * 
	 * @param l
	 */
	public void setLevel(int l) {
		if (l >= 0) {
			this.player.getDataWatcher().updateObject(LEVEL_WATCHER, l);
			this.applyModifiers(l);
		}
	}

	/**
	 * Suck blood from an EntityLiving. Only sucks blood if health is low enough
	 * and if the entity has blood
	 * 
	 * @param e
	 *            Entity to suck blood from
	 */
	public void suckBlood(EntityLiving e) {
		if(e.worldObj.isRemote){
			return;
		}
		VampireMob mob = VampireMob.get(e);
		int amount = mob.bite();
		if (amount > 0) {
			this.bloodStats.addStats(amount, 1F);
			
			VampirismMod.modChannel.sendToAll(new SpawnParticlePacket("magicCrit",e.posX,e.posY,e.posZ,player.posX-e.posX,player.posY-e.posY,player.posZ-e.posZ,10));
			VampirismMod.modChannel.sendTo(new SpawnParticlePacket("blood_eat",0,0,0,0,0,0,10), (EntityPlayerMP)player);
		}
		else if(amount==-1){
			player.attackEntityFrom(DamageSource.outOfWorld, 1);
		}
		else if(amount==-2){
			player.addPotionEffect(new PotionEffect(19,80,1));
			player.addPotionEffect(new PotionEffect(9,120,0));
		}
	}

	/**
	 * Suck blood from an EntityLiving belonging to the given id. Only sucks
	 * blood if health is low enough and if the entity has blood
	 * 
	 * @param e
	 *            Id of Entity to suck blood from
	 */
	public void suckBlood(int entityId) {
		Entity e = player.worldObj.getEntityByID(entityId);
		if (e != null && e instanceof EntityLiving) {
			suckBlood((EntityLiving) e);
		}
	}
	
	
	public class BloodStats{
		private float bloodExhaustionLevel;
		private float bloodSaturationLevel;
		private int bloodTimer;
		private int prevBloodLevel;
		
		private final float maxExhaustion=40F;
		
		
		private void writeNBT(NBTTagCompound nbt){
			nbt.setInteger("bloodTimer", bloodTimer);
			nbt.setFloat("bloodExhaustionLevel", bloodExhaustionLevel);
			nbt.setFloat("bloodSaturationlevel", bloodSaturationLevel);
		}
		
		private void readNBT(NBTTagCompound nbt){
			if(nbt.hasKey("bloodTimer")){
				bloodTimer=nbt.getInteger("bloodTimer");
				bloodExhaustionLevel=nbt.getFloat("bloodExhaustionLevel");
				bloodSaturationLevel=nbt.getFloat("bloodSaturationLevel");
			}
		}
		
		public void addExhaustion(float amount){
			this.bloodExhaustionLevel = Math.min(bloodExhaustionLevel+amount, maxExhaustion);
		}
		
		private void addStats(int blood,float saturationModifier){
			addBlood(blood);
			this.bloodSaturationLevel= Math.min(bloodSaturationLevel+blood*saturationModifier*2.0F, getBlood());
		}
		
		/**
		 * Updates players bloodlevel. Working similar to player foodstats
		 */
		private void onUpdate(){
			player.getFoodStats().setFoodLevel(10);
			EnumDifficulty enumdifficulty = player.worldObj.difficultySetting;
			prevBloodLevel=getBlood();
			
			if(this.bloodExhaustionLevel > 4.0F){
				Logger.i("testexhautsion", bloodExhaustionLevel+":"+bloodSaturationLevel+":"+getBloodLevel());
				this.bloodExhaustionLevel-=4.0F;
				
				if(this.bloodSaturationLevel>0.0F){
					this.bloodSaturationLevel=Math.max(bloodSaturationLevel - 1.0F,0F);
				}
				else if(enumdifficulty != EnumDifficulty.PEACEFUL){
					setBlood(Math.max(getBlood()-1, 0));
				}
			}
			
			if(player.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration") && getBlood() >= 0.9*MAXBLOOD && player.shouldHeal()){
				++this.bloodTimer;
				if(this.bloodTimer>=80){
					player.heal(1.0F);
					this.addExhaustion(3.0F);
					this.bloodTimer=0;
				}
			}
			else if(getBlood() <=0){
				++this.bloodTimer;
				if(this.bloodTimer>=80){
					if(player.getHealth()>10.0F|| enumdifficulty == EnumDifficulty.HARD || player.getHealth() > 1.0F && enumdifficulty == EnumDifficulty.NORMAL){
						player.attackEntityFrom(DamageSource.starve, 1.0F);
					}
					this.bloodTimer=0;
				}
			}
			else{
				this.bloodTimer=0;
			}
		}
		
		public int getBloodLevel(){
			return getBlood();
		}
		
		@SideOnly(Side.CLIENT)
		public int getPrevBloodLevel(){
			return prevBloodLevel;
		}
		
	}

}
