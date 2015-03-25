package de.teamlapen.vampirism.entity;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.villages.VillageVampire;
import de.teamlapen.vampirism.villages.VillageVampireData;

public class VampireMob implements IExtendedEntityProperties {

	public static final VampireMob get(EntityLiving mob) {
		return (VampireMob) mob.getExtendedProperties(VampireMob.EXT_PROP_NAME);
	}
	/**
	 * Returns how much blood can be collected by biting this entity. -1 if
	 * poisonous, -2 if defending
	 * 
	 * @param e
	 * @return
	 */
	public static int getMaxBloodAmount(EntityCreature e) {
		if (e instanceof EntityPig || e instanceof EntitySheep || e instanceof EntityOcelot || e instanceof EntityWolf) {
			return BALANCE.SMALL_BLOOD_AMOUNT;
		}
		if (e instanceof EntityCow || e instanceof EntityHorse) {
			return BALANCE.NORMAL_BLOOD_AMOUNT;
		}
		if (e instanceof EntityVillager || e instanceof EntityWitch) {
			return BALANCE.BIG_BLOOD_AMOUNT;
		}
		if(e instanceof EntityVampireHunter){
			return -2;
		}
		return -1;
	}
	public static final void register(EntityCreature mob) {
		mob.registerExtendedProperties(VampireMob.EXT_PROP_NAME, new VampireMob(mob));
	}
	private final EntityCreature entity;
	public final static String EXT_PROP_NAME = "VampireMob";

	private final String KEY_VAMPIRE = "vampire";

	private static final int VAMPIRE_WATCHER = 25;

	private final int blood;

	public VampireMob(EntityCreature mob) {
		entity = mob;
		blood = getMaxBloodAmount(mob);
		entity.getDataWatcher().addObject(VAMPIRE_WATCHER, (short) 0);
	}

	private void addAITasks() {
		EntityAITasks tasks = (EntityAITasks) Helper.Reflection.getPrivateFinalField(EntityLiving.class, (EntityLiving)entity, Helper.Obfuscation.getPosNames("EntityLiving/tasks"));
		// Attack player
		tasks.addTask(1, new EntityAIAttackOnCollide(entity, EntityPlayer.class, 1.0D, false));
		// Attack vampire hunter
		tasks.addTask(1, new EntityAIAttackOnCollide(entity, EntityVampireHunter.class, 1.0D, true));

		EntityAITasks targetTasks = (EntityAITasks) Helper.Reflection.getPrivateFinalField(EntityLiving.class, entity, Helper.Obfuscation.getPosNames("EntityLiving/targetTasks"));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget(entity, EntityPlayer.class, 0, true, false, new IEntitySelector() {

			@Override
			public boolean isEntityApplicable(Entity entity) {
				if (entity instanceof EntityPlayer) {
					return VampirePlayer.get((EntityPlayer) entity).getLevel() <= 0;
				}
				return false;
			}

		}));
		// Search for vampire hunters
		targetTasks.addTask(3, new EntityAINearestAttackableTarget(entity, EntityVampireHunter.class, 0, true));
	}

	/**
	 * Bite the entity. Returns the retrieved blood
	 * 
	 * @return Retrieved blood, 0 if already empty, -1 if health too high, -2 if
	 *         not biteable
	 */
	public int bite() {
		if (blood == -1) {
			return -2;
		}
		if(blood ==-2){
			return -1;
		}
		if (isVampire()) {
			return 0;
		}
		
		if (!lowEnoughHealth()) {
			// Cannot be bitten yet
			return -1;
		}
		if(entity.worldObj.rand.nextInt(2)==0){
			makeVampire();
		}
		else{
			if(entity instanceof EntityVampireHunter){
				entity.attackEntityFrom(DamageSource.magic, 1);
			}
			else{
				entity.attackEntityFrom(DamageSource.magic, 100);
			}

		}
		
		if(entity instanceof EntityVillager){
			VillageVampire v=VillageVampireData.get(entity.worldObj).findNearestVillage(entity);
			if(v!=null){
				v.villagerBitten();
			}
		}
		//If entity is a child only give 1/3 blood
		if(entity instanceof EntityAgeable){
			if(((EntityAgeable)entity).getGrowingAge()<0){
				return Math.round((float)blood/3);
			}
		}
		return blood;

	}
	
	public boolean lowEnoughHealth(){
		return (entity.getHealth() / entity.getMaxHealth()) <= BALANCE.SUCK_BLOOD_HEALTH_REQUIREMENT;
	}

	public boolean canBeBitten() {
		return blood > 0 && !isVampire();
	}

	@Override
	public void init(Entity entity, World world) {
		// TODO Auto-generated method stub

	}

	public boolean isVampire() {
		return this.entity.getDataWatcher().getWatchableObjectShort(VAMPIRE_WATCHER) == (short) 1;
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		if (properties != null) {
			if (properties.getShort(KEY_VAMPIRE) == 1) {
				makeVampire();
			}
		}

	}

	private boolean makeVampire() {
		if (blood < 0) {
			return false;
		}
		entity.getDataWatcher().updateObject(VAMPIRE_WATCHER, (short) 1);
		addAITasks();
		entity.addPotionEffect(new PotionEffect(Potion.weakness.id,200));
		entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id,100));
		return true;
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setShort(KEY_VAMPIRE, this.entity.getDataWatcher().getWatchableObjectShort(VAMPIRE_WATCHER));
		compound.setTag(EXT_PROP_NAME, properties);

	}

}
