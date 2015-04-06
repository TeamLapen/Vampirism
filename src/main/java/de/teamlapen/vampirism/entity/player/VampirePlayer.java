package de.teamlapen.vampirism.entity.player;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.Configs;
import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.ModPotion;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.block.BlockCoffin;
import de.teamlapen.vampirism.entity.DefaultVampire;
import de.teamlapen.vampirism.entity.EntityDracula;
import de.teamlapen.vampirism.entity.EntityVampire;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.ai.IMinion;
import de.teamlapen.vampirism.entity.ai.IMinionLord;
import de.teamlapen.vampirism.entity.player.skills.ILastingSkill;
import de.teamlapen.vampirism.entity.player.skills.ISkill;
import de.teamlapen.vampirism.entity.player.skills.Skills;
import de.teamlapen.vampirism.entity.player.skills.VampireRageSkill;
import de.teamlapen.vampirism.item.ItemBloodBottle;
import de.teamlapen.vampirism.network.SpawnParticlePacket;
import de.teamlapen.vampirism.network.UpdateVampirePlayerPacket;
import de.teamlapen.vampirism.proxy.CommonProxy;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * IExtendedEntityPropertiesClass which extends the EntityPlayer with vampire
 * properties
 * 
 * @author Maxanier
 */
public class VampirePlayer implements IExtendedEntityProperties, IMinionLord {
	public boolean sleepingCoffin = false;
	public int sleeptimerCoffin = 0;

	public class BloodStats {
		private float bloodExhaustionLevel;
		private float bloodSaturationLevel;
		private int bloodTimer;
		private int prevBloodLevel;
		private int bloodToAdd;

		private final float maxExhaustion = 40F;

		/**
		 * Adds blood to the players bar
		 * 
		 * @param amount
		 * @return The amount which could not be added
		 */
		public int addBlood(int amount) {
			int oldBlood = getBlood();

			// Adds the blood
			int bloodToAdd = Math.min(amount, MAXBLOOD - oldBlood);
			changeBlood(bloodToAdd);
			// Add saturation effect
			this.bloodSaturationLevel = (float) Math.min(bloodSaturationLevel
					+ bloodToAdd * BALANCE.BLOOD_SATURATION * 2.0F, oldBlood
					+ bloodToAdd);

			// Calculate the amount of left blood and handles it
			int bloodLeft = amount - bloodToAdd;

			return bloodLeft;
		}

		public void addExhaustion(float amount) {
			if (isSkillActive(VampireRageSkill.ID)) {
				amount = amount * 1.5F;
			}
			this.bloodExhaustionLevel = Math.min(bloodExhaustionLevel + amount,
					maxExhaustion);
		}

		/**
		 * Changes the blood amount on next update
		 * 
		 * @param amount
		 *            Amount to add or remove (+/-)
		 * @return
		 */
		private void changeBlood(int amount) {
			bloodToAdd += amount;
		}

		/**
		 * Removes blood from the vampires blood level
		 * 
		 * @param a
		 *            amount
		 * @return whether the vampire had enough blood or not
		 */
		public boolean consumeBlood(int a) {
			int blood = getBlood();
			int bloodToRemove = Math.min(a, blood);

			changeBlood(-bloodToRemove);
			if (bloodToRemove > blood) {
				return false;
			}
			return true;
		}

		public int getBloodLevel() {
			return getBlood();
		}

		@SideOnly(Side.CLIENT)
		public int getPrevBloodLevel() {
			return prevBloodLevel;
		}

		/**
		 * Updates players bloodlevel. Working similar to player foodstats
		 */
		private synchronized void onUpdate() {
			if (player.worldObj.isRemote) {
				return;
			}
			if (player.getFoodStats().getFoodLevel() < 10) {
				player.getFoodStats().addStats(5, 1);
			}

			EnumDifficulty enumdifficulty = player.worldObj.difficultySetting;

			int newBloodLevel = getBlood();
			newBloodLevel = Math.min(newBloodLevel + bloodToAdd, MAXBLOOD);
			if (newBloodLevel < 0)
				newBloodLevel = 0;
			bloodToAdd = 0;

			if (this.bloodExhaustionLevel > BALANCE.BLOOD_EXH_PER_BL) {
				this.bloodExhaustionLevel -= BALANCE.BLOOD_EXH_PER_BL;

				if (this.bloodSaturationLevel > 0.0F) {
					this.bloodSaturationLevel = Math.max(
							bloodSaturationLevel - 1.0F, 0F);
				} else if (enumdifficulty != EnumDifficulty.PEACEFUL) {

					newBloodLevel = (Math.max(newBloodLevel - 1, 0));
				}
			}

			if (player.worldObj.getGameRules().getGameRuleBooleanValue(
					"naturalRegeneration")
					&& newBloodLevel >= 0.9 * MAXBLOOD && player.shouldHeal()) {
				++this.bloodTimer;
				if (this.bloodTimer >= 80) {
					player.heal(1.0F);
					this.addExhaustion(3.0F);
					this.bloodTimer = 0;
				}
			} else if (newBloodLevel <= 0) {
				++this.bloodTimer;
				if (this.bloodTimer >= 80) {
					if (player.getHealth() > 10.0F
							|| enumdifficulty == EnumDifficulty.HARD
							|| player.getHealth() > 1.0F
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

	public final static String EXT_PROP_NAME = "VampirePlayer";

	public final static String TAG = "VampirePlayer";

	public final static int MAXBLOOD = 20;
	private static final String KEY_VAMPIRE_LORD = "vampire_lord";

	/**
	 * 
	 * @param player
	 * @return VampirePlayer property of player
	 */
	public static final VampirePlayer get(EntityPlayer player) {
		return (VampirePlayer) player
				.getExtendedProperties(VampirePlayer.EXT_PROP_NAME);
	}

	private static final String getSaveKey(EntityPlayer player) {
		// no longer a username field, so use the command sender name instead:
		return player.getCommandSenderName() + ":" + EXT_PROP_NAME;
	}

	public static final void loadProxyData(EntityPlayer player) {
		VampirePlayer playerData = VampirePlayer.get(player);
		NBTTagCompound savedData = CommonProxy
				.getEntityData(getSaveKey(player));
		if (savedData != null) {
			playerData.loadNBTData(savedData);

		}
		playerData.sync(true);
	}

	/**
	 * Registers vampire property to player
	 * 
	 * @param player
	 */
	public static final void register(EntityPlayer player) {
		player.registerExtendedProperties(VampirePlayer.EXT_PROP_NAME,
				new VampirePlayer(player));
	}

	public static void saveProxyData(EntityPlayer player, boolean resetBlood) {
		VampirePlayer playerData = VampirePlayer.get(player);
		if (resetBlood) {
			playerData.setBloodData(MAXBLOOD);
		}
		NBTTagCompound savedData = new NBTTagCompound();
		playerData.saveNBTData(savedData);
		CommonProxy.storeEntityData(getSaveKey(player), savedData);
	}

	private final EntityPlayer player;

	private final String KEY_LEVEL = "level";

	private final String KEY_BLOOD = "blood";

	private final String KEY_AUTOFILL = "autofill";

	private final String KEY_SKILLS = "skills";

	private BloodStats bloodStats;

	private int level;

	private int[] skillTimer;

	private boolean dirty = false;

	private boolean autoFillBlood;
	
	private EntityLivingBase minionTarget;
	
	private boolean skipFallDamageReduction=false;
	
	private boolean vampireLord=false;

	public VampirePlayer(EntityPlayer player) {
		this.player = player;
		this.player.getDataWatcher().addObject(Configs.player_blood_watcher,
				MAXBLOOD);
		bloodStats = new BloodStats();
		autoFillBlood = true;
		skillTimer = new int[Skills.getSkillCount()];
	}

	/**
	 * Adds blood to the vampires blood level level without increasing the
	 * saturation level
	 * 
	 * @param a
	 *            amount
	 */

	private boolean getAutoFillBlood() {
		return autoFillBlood;
	}

	/**
	 * @return The current blood level
	 */
	public int getBlood() {
		return this.player.getDataWatcher().getWatchableObjectInt(
				Configs.player_blood_watcher);
	}

	public BloodStats getBloodStats() {
		return bloodStats;
	}

	/**
	 * 
	 * @return Vampire level of the player
	 */
	public int getLevel() {
		return this.level;
	}

	/**
	 * Returns the skill time for rendering
	 * 
	 * @param id
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public int getSkillTime(int id) {
		if (id >= 0) {
			return this.skillTimer[id];
		}
		return 0;
	}

	@Override
	public void init(Entity entity, World world) {

	}

	/**
	 * @return true if auto fill of blood bottle enabled
	 */
	public boolean isAutoFillBlood() {
		return autoFillBlood;
	}

	private boolean isRemote() {
		return player.worldObj.isRemote;
	}

	public boolean isSkillActive(int id) {
		if (id >= skillTimer.length) {
			Logger.w(TAG, "The skill with id " + id + " doesn't exist");
			return false;
		}
		return (skillTimer[id] > 0);
	}

	public void levelUp() {
		int level = getLevel();
		level++;
		setLevel(level);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound
				.getTag(EXT_PROP_NAME);
		setBloodData(properties.getInteger(KEY_BLOOD));
		level = properties.getInteger(KEY_LEVEL);
		int[] temp = properties.getIntArray(KEY_SKILLS);
		if (temp.length == Skills.getSkillCount()) {
			skillTimer = temp;
		} else {
			Logger.w(TAG,
					"Loaded skill timers have a different size than the existing skills");
			skillTimer = new int[Skills.getSkillCount()];
		}
		vampireLord=properties.getBoolean(KEY_VAMPIRE_LORD);
		if(properties.hasKey(KEY_AUTOFILL)){
			setAutoFillBlood(properties.getBoolean(KEY_AUTOFILL));
		}

		this.bloodStats.readNBT(properties);
		PlayerModifiers.applyModifiers(level, player);

	}

	/**
	 * Ment to be used by UpdateVampirePlayerPacket on client side, to load
	 * updates.
	 * 
	 * @param level
	 * @param skill timer
	 * @param Whether the player is a vampire lord or not
	 */
	@SideOnly(Side.CLIENT)
	public void loadSyncUpdate(int level, int[] timers,boolean lord) {
		this.setLevel(level);
		this.skillTimer = timers;
		this.vampireLord=lord;
	}

	private void looseLevel() {
		int level = getLevel();
		if (level > 1) {
			setLevel(level - 1);
		}
	}
	
	public boolean onEntityAttacked(DamageSource source, float amount){
		if(source.getEntity() instanceof DefaultVampire && getLevel()==0){
			//Since the method seems to be called 4 times probability is decreased by the factor 4
			if(player.worldObj.rand.nextInt(BALANCE.VAMPIRE_PLAYER_SANGUINARE_PROB*4)==0){
				if(!player.isPotionActive(ModPotion.sanguinare)){
					player.addPotionEffect(new PotionEffect(ModPotion.sanguinare.id,BALANCE.VAMPIRE_PLAYER_SANGUINARE_DURATION*20));
				}

			}
			else{
				Logger.i("tes", "no");
			}
		}
			if(source.getEntity() instanceof EntityLivingBase &&getLevel()>0){
				this.minionTarget=(EntityLivingBase) source.getEntity();
				return false;
			}
			if(DamageSource.fall.equals(source)&&!this.skipFallDamageReduction){

				float i=amount-(getLevel()/3)-1;
				if(i>0){
					this.skipFallDamageReduction=true;
					player.attackEntityFrom(DamageSource.fall, i);
					this.skipFallDamageReduction=false;
				}
				return true;
				
			}
			return false;
	}

	public void onDeath(DamageSource source) {
		if (BALANCE.VAMPIRE_PLAYER_LOOSE_LEVEL
				&& source.damageType.equals("mob")
				&& source instanceof EntityDamageSource) {
			Entity src=source.getEntity();
			if (src instanceof EntityVampireHunter) {
				looseLevel();
				this.setVampireLord(false);
			}
			
			if(src instanceof EntityVampire||(src instanceof IMinion && ((IMinion)src).getLord() instanceof EntityLiving)){
				EntityLiving old;
				if(src instanceof IMinion){
					old=(EntityLiving) ((IMinion)src).getLord();
				}
				else{
					old=(EntityLiving) src;
				}
				EntityDracula dracula=(EntityDracula) EntityList.createEntityByName(REFERENCE.ENTITY.DRACULA_NAME, old.worldObj);
				dracula.copyLocationAndAnglesFrom(old);
				dracula.makeDisappear();
				old.worldObj.spawnEntityInWorld(dracula);
				old.setDead();
				this.setVampireLord(false);
				//TODO if other player is the killer he can become the lord
			}
		}
		for (int i = 0; i < skillTimer.length; i++) {
			if (skillTimer[i] > 0) {
				skillTimer[i] = -Skills.getSkill(i).getCooldown();
				((ILastingSkill) Skills.getSkill(i))
						.onDeactivated(this, player);

			}
		}
	}

	public void onSkillToggled(int i) {
		ISkill s = Skills.getSkill(i);
		if (s == null)
			return;
		int t = skillTimer[i];
		if (t > 0) {// Running, only for lasting skills
			skillTimer[i] = (-s.getCooldown()) + t;
			((ILastingSkill) s).onDeactivated(this, player);
		} else if (t == 0) {// Ready
			if(s.getMinLevel()==-1){
				player.addChatMessage(new ChatComponentTranslation("text.vampirism:skill.deactivated_by_serveradmin"));
			}
			else if(getLevel() < s.getMinLevel()){
				player.addChatMessage(new ChatComponentTranslation("text.vampirism:skill.level_to_low"));
			}
			else{
				if (s instanceof ILastingSkill) {
					ILastingSkill ls = (ILastingSkill) s;
					skillTimer[i] = ls.getDuration(getLevel());
					ls.onActivated(this, player);
				} else {
					s.onActivated(this, player);
					skillTimer[i] = -s.getCooldown();
				}
			}
		} else {// In cooldown
			player.addChatMessage(new ChatComponentTranslation(
					"text.vampirism:skill.cooldown_not_over"));
		}
		dirty = true;
	}

	public void onToggleAutoFillBlood() {
		if (autoFillBlood) {
			autoFillBlood = false;
			this.player.addChatMessage(new ChatComponentTranslation(
					"text.vampirism:auto_fill_disabled"));
		} else {
			autoFillBlood = true;
			this.player.addChatMessage(new ChatComponentTranslation(
					"text.vampirism:auto_fill_enabled"));
		}
	}

	/**
	 * Called every LivingEntityUpdate, returns immediately if level =0;
	 */
	public void onUpdate() {
		if (getLevel() <= 0) {
			PotionEffect sang=player.getActivePotionEffect(ModPotion.sanguinare);
			if(sang!=null){
				if(sang.getDuration()==1){
					this.levelUp();
					player.addPotionEffect(new PotionEffect(ModPotion.saturation.id,300));
					player.addPotionEffect(new PotionEffect(Potion.resistance.id,300));
				}
			}
			return;
		}
		this.bloodStats.onUpdate();
		if (!player.worldObj.isRemote
				&& player.worldObj.canBlockSeeTheSky(
						MathHelper.floor_double(player.posX),
						MathHelper.floor_double(player.posY),
						MathHelper.floor_double(player.posZ))) {
			if (player.worldObj.isDaytime()
					&& player.getBrightness(1.0F) > 0.5F
					&& player.worldObj.rand.nextInt(40) == 10) {
				float dmg = BALANCE.getVampireSunDamage(getLevel());
				if (player.isPotionActive(ModPotion.sunscreen)) {
					dmg = dmg / 2;
				}
				player.attackEntityFrom(VampirismMod.sunDamage, dmg);
			}
			if(player.isPotionActive(ModPotion.sanguinare.id)){
				player.removePotionEffect(ModPotion.sanguinare.id);
			}
		}

		/**
		 * Loop through all skill timers and update them and their tick time
		 */
		for (int i = 0; i < skillTimer.length; i++) {
			int t = skillTimer[i];
			if (t != 0) {// If timer equals 0, there is nothing to do
				if (t < 0) {
					skillTimer[i] = ++t;
				} else {
					skillTimer[i] = --t;
					ILastingSkill s = (ILastingSkill) Skills.getSkill(i);
					if (t == 0) {
						skillTimer[i] = -s.getCooldown();
						if (!isRemote()) {
							s.onDeactivated(this, player);
							dirty = true;
						}
					} else {
						s.onUpdate(this, player);
					}
				}

			}
		}
		
		/**
		 * Check minion target
		 */
		if(minionTarget!=null&&!minionTarget.isEntityAlive()){
			minionTarget=null;
		}
		if (dirty == true) {
			this.sync(true);
			dirty = false;
		}
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setInteger(KEY_LEVEL, getLevel());
		properties.setInteger(KEY_BLOOD, getBlood());
		properties.setIntArray(KEY_SKILLS, skillTimer);
		properties.setBoolean(KEY_AUTOFILL, getAutoFillBlood());
		properties.setBoolean(KEY_VAMPIRE_LORD, isVampireLord());
		this.bloodStats.writeNBT(properties);
		compound.setTag(EXT_PROP_NAME, properties);

	}

	public boolean isVampireLord() {
		return vampireLord;
	}

	private void setAutoFillBlood(boolean value) {
		autoFillBlood = value;
	}

	/**
	 * DONT USE, only designed to be used at startup and by Bloodstats Try to
	 * use addBlood(int amount) or consumeBlood(int amount) instead
	 * 
	 * @param b
	 */
	private synchronized void setBloodData(int b) {
		this.player.getDataWatcher().updateObject(Configs.player_blood_watcher,
				b);

	}

	/**
	 * For testing only, make private later. This is the only method which
	 * should change the level. This method should execute all level related
	 * changes e.g. player modifiers Its syncs it with the client
	 * 
	 * @param l
	 */
	public void setLevel(int l) {
		if (l >= 0) {
			level = l;
			PlayerModifiers.applyModifiers(l, player);
			this.sync(true);
		}
	}
	
	/**
	 * Sets if the player is a vampire lord or not
	 * @param state
	 * @return will return false if the player cannot become a vampire lord
	 */
	public boolean setVampireLord(boolean state){
		if(state&&getLevel()<REFERENCE.HIGHEST_REACHABLE_LEVEL){
			Logger.w(TAG, "Cannot become a vampire lord since the player has not reached the highest level");
			return false;
		}
		this.vampireLord=state;
		return true;
	}

	/**
	 * Suck blood from an EntityLiving. Only sucks blood if health is low enough
	 * and if the entity has blood
	 * 
	 * @param e
	 *            Entity to suck blood from
	 */
	public void suckBlood(EntityCreature e) {
		if (e.worldObj.isRemote) {
			return;
		}
		if (getLevel() == 0) {
			return;
		}
		VampireMob mob = VampireMob.get(e);
		int amount = mob.bite();
		if (amount > 0) {
			int amt=this.bloodStats.addBlood(amount);
			if(amt>0&&isAutoFillBlood()){
				fillBloodIntoInventory(amt);
			}
			VampirismMod.modChannel.sendToAll(new SpawnParticlePacket(
					"magicCrit", e.posX, e.posY, e.posZ, player.posX - e.posX,
					player.posY - e.posY, player.posZ - e.posZ, 10));
			VampirismMod.modChannel.sendTo(new SpawnParticlePacket("blood_eat",
					0, 0, 0, 0, 0, 0, 10), (EntityPlayerMP) player);
			
		} else if (amount == -1) {
			player.attackEntityFrom(DamageSource.outOfWorld, 1);
			VampirismMod.modChannel.sendToAll(new SpawnParticlePacket("crit",
					e.posX, e.posY, e.posZ, player.posX - e.posX, player.posY
							- e.posY, player.posZ - e.posZ, 10));
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
		if (e != null && e instanceof EntityCreature) {
			suckBlood((EntityCreature) e);
		}
	}

	/**
	 * Sends updates to the client
	 */
	public void sync(boolean all) {
		if (!player.worldObj.isRemote) {
			if(all){
				Helper.sendPacketToPlayersAround(createUpdatePacket(), player);
			}
			else{
				VampirismMod.modChannel.sendTo(createUpdatePacket(), (EntityPlayerMP) player);
			}
			
		}
	}
	
	public IMessage createUpdatePacket(){
		return new UpdateVampirePlayerPacket(player.getEntityId(),
				getLevel(), skillTimer,isVampireLord());
	}

	@Override
	public EntityLivingBase getMinionTarget() {
		return this.minionTarget;
	}

	@Override
	public UUID getThePersistentID() {
		return player.getPersistentID();
	}

	@Override
	public boolean isTheEntityAlive() {
		return player.isEntityAlive();
	}

	@Override
	public double getTheDistanceSquared(Entity e) {
		return player.getDistanceSqToEntity(e);
	}

	@Override
	public Entity getRepresentingEntity() {
		return player;
	}
	
	/**
	 * Tries to fill blood into blood bottles in the hotbar or tries to convert glas bottles from the hotbar to blood bottles
	 * @param amt
	 */
	protected void fillBloodIntoInventory(int amt){
		if(amt<=0)return;
		ItemStack stack = ItemBloodBottle
				.getBloodBottleInInventory(player.inventory,true);
		if (stack != null) {
			fillBloodIntoInventory(ItemBloodBottle.addBlood(stack, amt));
		}
		else{
			ItemStack glas=ItemBloodBottle.getGlasBottleInInventory(player.inventory);
			if(glas!=null){
				ItemStack bloodBottle=new ItemStack(ModItems.bloodBottle,1,0);
				amt=ItemBloodBottle.addBlood(bloodBottle, amt);
				player.inventory.consumeInventoryItem(Items.glass_bottle);
				if (!player.inventory.addItemStackToInventory(bloodBottle))
                {
                    player.dropPlayerItemWithRandomChoice(bloodBottle, false);
                }
				if(amt>0)fillBloodIntoInventory(amt);
			}
		}
	}

	/**
	 * puts player to sleep on specified coffin if possible
	 */
	public EntityPlayer.EnumStatus sleepInCoffinAt(int x,
			int y, int z) {
		//TODO Understand the event stuff
		PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(this.player,
				x, y, z);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.result != null) {
			return event.result;
		}
		
		
		if (!this.player.worldObj.isRemote) {
			if (this.sleepingCoffin || !this.isTheEntityAlive()) {
				return EntityPlayer.EnumStatus.OTHER_PROBLEM;
			}

			if (!this.player.worldObj.provider.isSurfaceWorld()) {
				return EntityPlayer.EnumStatus.NOT_POSSIBLE_HERE;
			}

			if (!this.player.worldObj.isDaytime()) {
				return EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW;
			}

			if (Math.abs(this.player.posX - (double) x) > 3.0D
					|| Math.abs(this.player.posY - (double) y) > 2.0D
					|| Math.abs(this.player.posZ - (double) z) > 3.0D) {
				return EntityPlayer.EnumStatus.TOO_FAR_AWAY;
			}

			double d0 = 8.0D;
			double d1 = 5.0D;
			List list = this.player.worldObj
					.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB
							.getBoundingBox((double) x - d0,
									(double) y - d1,
									(double) z - d0,
									(double) x + d0,
									(double) y + d1,
									(double) z + d0));

			if (!list.isEmpty()) {
				return EntityPlayer.EnumStatus.NOT_SAFE;
			}
		}

		if (this.player.isRiding()) {
			this.player.mountEntity((Entity) null);
		}

		Helper.Reflection.callMethod(Entity.class, this.player,
				Helper.Obfuscation.getPosNames("EntityPlayer/setSize"),
				new Class[] { float.class, float.class }, new Object[] { 0.2F,
						0.2F });
		// this.player.setSize(0.2F, 0.2F);
		this.player.yOffset = 0.2F;

		if (this.player.worldObj
				.blockExists(x, y, z)) {
			int direction = ((BlockCoffin) player.worldObj
					.getBlock(x, y, z))
					.getDirection(player.worldObj, x, y,
							z);
			float f1 = 0.5F;
			float f = 0.5F;

			switch (direction) {
			case 0:
				f = 0.9F;
				break;
			case 1:
				f1 = 0.1F;
				break;
			case 2:
				f = 0.1F;
				break;
			case 3:
				f1 = 0.9F;
			}

			this.func_71013_b(direction);
			this.player.setPosition((double) ((float) x + f1),
					(double) ((float) y + 0.9375F),
					(double) ((float) z + f));
		} else {
			this.player.setPosition((double) ((float) x + 0.5F),
					(double) ((float) y + 0.9375F),
					(double) ((float) z + 0.5F));
		}

		//Following method will replace: this.player.sleeping = true;
//		Helper.Reflection.setPrivateField(EntityPlayer.class, this.player,
//				true, Helper.Obfuscation.getPosNames("EntityPlayer/sleeping"));
		this.sleepingCoffin = true;
		//Following method will replace: this.player.sleepTimer = 0;
//		Helper.Reflection.setPrivateField(EntityPlayer.class, this.player, 0,
//				Helper.Obfuscation.getPosNames("EntityPlayer/sleepTimer"));
		this.sleeptimerCoffin = 0;

		this.player.playerLocation = new ChunkCoordinates(x,
				y, z);
		this.player.motionX = this.player.motionZ = this.player.motionY = 0.0D;

		if (!this.player.worldObj.isRemote) {
			//TODO this.player.worldObj.updateAllPlayersSleepingFlag();
		}

		return EntityPlayer.EnumStatus.OK;
	}

	private void func_71013_b(int direction) {
		this.player.field_71079_bU = 0.0F;
		this.player.field_71089_bV = 0.0F;

		switch (direction) {
		case 0:
			this.player.field_71089_bV = -1.8F;
			break;
		case 1:
			this.player.field_71079_bU = 1.8F;
			break;
		case 2:
			this.player.field_71089_bV = 1.8F;
			break;
		case 3:
			this.player.field_71079_bU = -1.8F;
		}
	}

	public boolean isPlayerFullyAsleepCoffin() {
		return this.sleepingCoffin && this.sleeptimerCoffin >= 100;
	}
	
	
//    public void wakeUpPlayer(boolean par1, boolean par2, boolean par3)
//    {
//        MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerWakeUpEvent(this.player, par1, par2, par3));
//        Helper.Reflection.callMethod(Entity.class, this.player,
//				Helper.Obfuscation.getPosNames("EntityPlayer/setSize"),
//				new Class[] { float.class, float.class }, new Object[] { 0.6F,
//						1.8F });
//        //this.setSize(0.6F, 1.8F);
//        this.player.yOffset = 1.62F;  //this.player.resetHeight();
//        
//        ChunkCoordinates chunkcoordinates = this.player.playerLocation;
//        ChunkCoordinates chunkcoordinates1 = this.player.playerLocation;
//        Block block = (chunkcoordinates == null ? null : player.worldObj.getBlock(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ));
//        //Convert the block to type coffin
//        BlockCoffin coffin = (block instanceof BlockCoffin) ? (BlockCoffin) block : null;
//
//        if (chunkcoordinates != null && coffin != null) //block.isBed(player.worldObj, chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, this.player)
//        {
//            coffin.setCoffinOccupied(this.player.worldObj, chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, this.player, false);
//            chunkcoordinates1 = block.getBedSpawnPosition(this.player.worldObj, chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, this.player);
//
//            if (chunkcoordinates1 == null)
//            {
//                chunkcoordinates1 = new ChunkCoordinates(chunkcoordinates.posX, chunkcoordinates.posY + 1, chunkcoordinates.posZ);
//            }
//
//            this.player.setPosition((double)((float)chunkcoordinates1.posX + 0.5F), (double)((float)chunkcoordinates1.posY + this.player.yOffset + 0.1F), (double)((float)chunkcoordinates1.posZ + 0.5F));
//        }
//
//        this.sleepingCoffin = false;
//
//        if (!this.player.worldObj.isRemote && par2)
//        {
//        	VampirismMod.proxy.updateAllPlayersSleepingFlagCoffin();
//            //this.player.worldObj.updateAllPlayersSleepingFlagCoffin();
//        }
//
//        if (par1)
//        {
//            this.sleeptimerCoffin = 0;
//        }
//        else
//        {
//            this.sleeptimerCoffin = 100;
//        }
//
//        if (par3)
//        {
//            this.player.setSpawnChunk(this.player.playerLocation, false);
//        }
//    }
}
