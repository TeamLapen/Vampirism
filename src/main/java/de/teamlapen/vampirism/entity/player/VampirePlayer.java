package de.teamlapen.vampirism.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.item.ItemBloodBottle;
import de.teamlapen.vampirism.network.SpawnParticlePacket;
import de.teamlapen.vampirism.proxy.CommonProxy;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.ModItems;
import de.teamlapen.vampirism.util.REFERENCE;

public class VampirePlayer implements IExtendedEntityProperties {

	public class BloodStats {
		private float bloodExhaustionLevel;
		private float bloodSaturationLevel;
		private int bloodTimer;
		private int prevBloodLevel;
		private int bloodToAdd;

		private final float maxExhaustion = 40F;

		public void addExhaustion(float amount) {
			this.bloodExhaustionLevel = Math.min(bloodExhaustionLevel + amount, maxExhaustion);
		}

		private void addStats(int blood, float saturationModifier) {
			addBlood(blood);
			this.bloodSaturationLevel = Math.min(bloodSaturationLevel + blood * saturationModifier * 2.0F, getBlood());
		}

		public int getBloodLevel() {
			return getBlood();
		}
		
		/**
		 * Changes the blood amount on next update
		 * @param amount Amount to add or remove (+/-)
		 * @return
		 */
		protected void changeBlood(int amount){
			bloodToAdd+=amount;
		}

		@SideOnly(Side.CLIENT)
		public int getPrevBloodLevel() {
			return prevBloodLevel;
		}

		/**
		 * Updates players bloodlevel. Working similar to player foodstats
		 */
		private synchronized void onUpdate() {
			if(player.worldObj.isRemote){
				return;
			}
			player.getFoodStats().setFoodLevel(10);
			EnumDifficulty enumdifficulty = player.worldObj.difficultySetting;
			
			int newBloodLevel=getBlood();
			newBloodLevel=Math.min(newBloodLevel+bloodToAdd, MAXBLOOD);
			if(newBloodLevel<0)newBloodLevel=0;
			bloodToAdd=0;

			if (this.bloodExhaustionLevel > 4.0F) {
				this.bloodExhaustionLevel -= 4.0F;

				if (this.bloodSaturationLevel > 0.0F) {
					this.bloodSaturationLevel = Math.max(bloodSaturationLevel - 1.0F, 0F);
				} else if (enumdifficulty != EnumDifficulty.PEACEFUL) {
					
					newBloodLevel = (Math.max(newBloodLevel - 1, 0));
				}
			}

			if (player.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration") && newBloodLevel >= 0.9 * MAXBLOOD && player.shouldHeal()) {
				++this.bloodTimer;
				if (this.bloodTimer >= 80) {
					player.heal(1.0F);
					this.addExhaustion(3.0F);
					this.bloodTimer = 0;
				}
			} else if (newBloodLevel <= 0) {
				++this.bloodTimer;
				if (this.bloodTimer >= 80) {
					if (player.getHealth() > 10.0F || enumdifficulty == EnumDifficulty.HARD || player.getHealth() > 1.0F
							&& enumdifficulty == EnumDifficulty.NORMAL) {
						player.attackEntityFrom(DamageSource.starve, 1.0F);
					}
					this.bloodTimer = 0;
				}
			} else {
				this.bloodTimer = 0;
			}
			setBloodData(newBloodLevel);
		}

		private void readNBT(NBTTagCompound nbt) {
			if (nbt.hasKey("bloodTimer")) {
				bloodTimer = nbt.getInteger("bloodTimer");
				bloodExhaustionLevel = nbt.getFloat("bloodExhaustionLevel");
				bloodSaturationLevel = nbt.getFloat("bloodSaturationLevel");
			}
		}

		private void writeNBT(NBTTagCompound nbt) {
			nbt.setInteger("bloodTimer", bloodTimer);
			nbt.setFloat("bloodExhaustionLevel", bloodExhaustionLevel);
			nbt.setFloat("bloodSaturationlevel", bloodSaturationLevel);
		}

	}

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

	public static void saveProxyData(EntityPlayer player,boolean resetBlood) {
		VampirePlayer playerData = VampirePlayer.get(player);
		if(resetBlood){
			playerData.setBloodData(MAXBLOOD);
		}
		NBTTagCompound savedData = new NBTTagCompound();
		playerData.saveNBTData(savedData);
		CommonProxy.storeEntityData(getSaveKey(player), savedData);
	}

	public final static String EXT_PROP_NAME = "VampirePlayer";

	private final EntityPlayer player;

	private final String KEY_LEVEL = "level";

	private final String KEY_BLOOD = "blood";
	
	private final String KEY_AUTOFILL = "autofill";

	public final static int MAXBLOOD = 20;

	private final static int BLOOD_WATCHER = 20;

	private final static int LEVEL_WATCHER = 21;
	
//	private final static int AUTOFILL_WATCHER = 22;

	private BloodStats bloodStats;

	private boolean autoFillBlood;

	public VampirePlayer(EntityPlayer player) {
		this.player = player;
		this.player.getDataWatcher().addObject(BLOOD_WATCHER, MAXBLOOD);
		this.player.getDataWatcher().addObject(LEVEL_WATCHER, 0);
		bloodStats = new BloodStats();
		autoFillBlood = false;
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * Adds blood to the vampires blood level without increasing the saturation level
	 * @param a amount
	 */
	private void addBlood(int a) {
		Logger.i(REFERENCE.MODID, "Sucked " + a + " blood!");
		ItemStack stack;
		int bloodToAdd = 0;
		
		if (isAutoFillBlood()) {
			stack = ItemBloodBottle.getBloodBottleInInventory(player.inventory);
			if (stack != null) {
				int bottleBlood = stack.getItemDamage();
				bloodToAdd = Math.min(a, ItemBloodBottle.MAX_BLOOD - bottleBlood);
				stack.setItemDamage(bottleBlood + bloodToAdd);
				Logger.i("VampirePlayer", "Added " + bloodToAdd + " blood to bottle (AUTO)!");
			}
			bloodStats.changeBlood(a-bloodToAdd);
			Logger.i("VampirePlayer", "Added " + (a - bloodToAdd) + " blood to bar (AUTO)!");
		} else {
			bloodStats.changeBlood(a);
		}
	}
	
	/**
	 * Feeds the Vampire with the given blood amount and increases his saturation
	 * @param amount
	 * @param saturation
	 */
	public void addBlood(int amount,float saturation){
		if(amount>0&&saturation>0){
			this.bloodStats.addStats(amount, saturation);
		}
	}
	
	/**
	 * Feeds the Vampire with the given blood amount and increases his saturation with a standard factor
	 * @param amount
	 */
	public void addFoodBlood(int amount){
		this.addBlood(amount, 1F);
	}
	
	
	
	/**
	 * Removes blood from the vampires blood level
	 * @param a amount
	 * @return whether the vampire had enough blood or not
	 */
	public boolean consumeBlood(int a){
		int blood=getBlood();
		if(a>blood){
			bloodStats.changeBlood(-a);
			return false;
		}
		else{
			bloodStats.changeBlood(-a);
			return true;
		}
	}

	private void applyModifiers(int level) {
		PlayerModifiers.applyModifiers(level, player);

	}

	/**
	 * @return The current blood level
	 */
	public int getBlood() {
		return this.player.getDataWatcher().getWatchableObjectInt(BLOOD_WATCHER);
	}

	public BloodStats getBloodStats() {
		return bloodStats;
	}

	/**
	 * 
	 * @return Vampire level of the player
	 */
	public int getLevel() {
		return this.player.getDataWatcher().getWatchableObjectInt(LEVEL_WATCHER);
	}

	/**
	 * @return true if auto fill of blood bottle enabled
	 */
	public boolean isAutoFillBlood() {
		return autoFillBlood;
	}
	
	private void setAutoFillBlood(boolean value) {
		autoFillBlood = value;
	}
	
	private boolean getAutoFillBlood() {
		return autoFillBlood;
	}
	
	public void toggleAutoFillBlood() {
		if (autoFillBlood) {
			Logger.i(REFERENCE.MODID, "Disabling Auto Fill Blood!");
			autoFillBlood = false;
			this.player.addChatMessage(new ChatComponentText("Auto Fill Blood Disabled"));
		} else {
			Logger.i(REFERENCE.MODID, "Enabling Auto Fill Blood!");
			autoFillBlood = true;
			this.player.addChatMessage(new ChatComponentText("Auto Fill Blood Enabled"));
		}
	}
	
	@Override
	public void init(Entity entity, World world) {

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
		setBloodData(properties.getInteger(KEY_BLOOD));
		setLevel(properties.getInteger(KEY_LEVEL));
		setAutoFillBlood(properties.getBoolean(KEY_AUTOFILL));
		this.bloodStats.readNBT(properties);

	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent e) {
		if (e.entity.equals(player)) {
			onUpdate();
		}
	}

	private void onUpdate() {
		if (getLevel() > 0) {
			this.bloodStats.onUpdate();
		}

	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setInteger(KEY_LEVEL, getLevel());
		properties.setInteger(KEY_BLOOD, getBlood());
		properties.setBoolean(KEY_AUTOFILL, getAutoFillBlood());
		this.bloodStats.writeNBT(properties);
		compound.setTag(EXT_PROP_NAME, properties);

	}

	/**
	 * DONT USE, only designed to be used at startup and by Bloodstats
	 * Try to use addBlood(int amount) or consumeBlood(int amount) instead
	 * @param b
	 */
	private synchronized void  setBloodData(int b) {
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
		if (e.worldObj.isRemote) {
			return;
		}
		if (getLevel() == 0) {
			return;
		}
		VampireMob mob = VampireMob.get(e);
		int amount = mob.bite();
		if (amount > 0) {
			this.bloodStats.addStats(amount, 1F);

			VampirismMod.modChannel.sendToAll(new SpawnParticlePacket("magicCrit", e.posX, e.posY, e.posZ, player.posX - e.posX,
					player.posY - e.posY, player.posZ - e.posZ, 10));
			VampirismMod.modChannel.sendTo(new SpawnParticlePacket("blood_eat", 0, 0, 0, 0, 0, 0, 10), (EntityPlayerMP) player);
		} else if (amount == -1) {
			player.attackEntityFrom(DamageSource.outOfWorld, 1);
			VampirismMod.modChannel.sendToAll(new SpawnParticlePacket("crit", e.posX, e.posY, e.posZ, player.posX - e.posX, player.posY - e.posY,
					player.posZ - e.posZ, 10));
		} else if (amount == -2) {
			player.addPotionEffect(new PotionEffect(19, 80, 1));
			player.addPotionEffect(new PotionEffect(9, 120, 0));
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

}
