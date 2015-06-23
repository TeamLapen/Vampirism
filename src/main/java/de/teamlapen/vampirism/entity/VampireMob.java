package de.teamlapen.vampirism.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.jdt.annotation.NonNull;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
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
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.ai.EntityAIModifier;
import de.teamlapen.vampirism.entity.minions.DefendLordCommand;
import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.minions.IMinionCommand;
import de.teamlapen.vampirism.entity.minions.IMinionLord;
import de.teamlapen.vampirism.entity.minions.StayHereCommand;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.UpdateEntityPacket;
import de.teamlapen.vampirism.network.UpdateEntityPacket.ISyncableExtendedProperties;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.villages.VillageVampire;
import de.teamlapen.vampirism.villages.VillageVampireData;

public class VampireMob implements ISyncableExtendedProperties, IMinion {

	public static final VampireMob get(EntityCreature mob) {
		return (VampireMob) mob.getExtendedProperties(VampireMob.EXT_PROP_NAME);
	}

	/**
	 * Returns how much blood can be collected by biting this entity. -1 if poisonous, -2 if defending
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
		if (e instanceof EntityVampireHunter) {
			return -2;
		}
		return -1;
	}

	public static final void register(EntityCreature mob) {
		mob.registerExtendedProperties(VampireMob.EXT_PROP_NAME, new VampireMob(mob));
	}

	private final EntityCreature entity;
	public final static String EXT_PROP_NAME = "VampireMob";
	private final static String TAG = "VampireMob";

	private final String KEY_TYPE = "type";


	private final int blood;
	private byte type;

	private UUID lordId = null;
	
	private IMinionCommand activeCommand = null;
	
	@SideOnly(Side.CLIENT)
	private int activeCommandId;
	
	private final ArrayList<IMinionCommand> commands;

	public VampireMob(EntityCreature mob) {
		entity = mob;
		blood = getMaxBloodAmount(mob);
		type=(byte)0;
		commands=new ArrayList<IMinionCommand>();
		commands.add(new DefendLordCommand(0,this));
	}
	
	public void activateMinionCommand(IMinionCommand command){
		if(command==null)return;
		if(!this.isMinion()){
			Logger.w(TAG, "%s is no minions and can therby not execute this %s minion command", this,command);
			return;
		}
		if(activeCommand!=null){
			activeCommand.onDeactivated();
		}
		activeCommand=command;
		activeCommand.onActivated();
		
	}

	/**
	 * Bite the entity. Returns the retrieved blood
	 * 
	 * @return Retrieved blood, 0 if already empty, -1 if health too high, -2 if not biteable
	 */
	public int bite() {
		if (blood == -1) {
			return -2;
		}
		if (blood == -2) {
			return -1;
		}
		if (isVampire()) {
			return 0;
		}

		if (!lowEnoughHealth()) {
			// Cannot be bitten yet
			return -1;
		}
		if (entity.worldObj.rand.nextInt(2) == 0) {
			makeVampire();
		} else {
			if (entity instanceof EntityVampireHunter) {
				entity.attackEntityFrom(DamageSource.magic, 1);
			} else {
				//Type should be only changed by the dedicated methods, but since the mob will die instantly it should not cause any problems
				type = (byte) (type | 1);
				entity.attackEntityFrom(DamageSource.magic, 100);
			}

		}

		if (entity instanceof EntityVillager) {
			VillageVampire v = VillageVampireData.get(entity.worldObj).findNearestVillage(entity);
			if (v != null) {
				v.villagerBitten();
			}
		}
		// If entity is a child only give 1/3 blood
		if (entity instanceof EntityAgeable) {
			if (((EntityAgeable) entity).getGrowingAge() < 0) {
				return Math.round((float) blood / 3);
			}
		}
		return blood;

	}

	public boolean canBeBitten() {
		return blood > 0 && !isVampire();
	}

	@Override
	public IMinionLord getLord() {
		if (!isMinion()) {
			Logger.w("VampireMob", "Trying to get lord, but mob is no minion");
		}
		EntityPlayer player=(lordId == null ? null : entity.worldObj.func_152378_a(lordId));
		return (player==null?null:VampirePlayer.get(player));
	}

	@Override
	public EntityCreature getRepresentingEntity() {
		return entity;
	}

	@Override
	public void init(Entity entity, World world) {
	}

	public boolean isMinion() {
		return ((type & 2) == 2);
	}

	public boolean isVampire() {
		return ((type & 1) == 1);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		if (properties != null) {
			if(properties.hasKey(KEY_TYPE)){
				type=properties.getByte(KEY_TYPE);
			}
			if (isMinion()) {
				if (properties.hasKey("BossUUIDMost")) {
					this.lordId = new UUID(properties.getLong("BossUUIDMost"), properties.getLong("BossUUIDLeast"));
				}
				IMinionCommand command=this.getCommand(properties.getInteger("command_id"));
				if(command!=null){
					this.activateMinionCommand(command);
				}
			}
		}

	}


	public boolean lowEnoughHealth() {
		return (entity.getHealth() / entity.getMaxHealth()) <= BALANCE.SUCK_BLOOD_HEALTH_REQUIREMENT;
	}

	public void makeMinion(IMinionLord lord) {
		setMinion();
		this.setLord(lord);
		this.activateMinionCommand(this.getCommand(0));
		entity.func_110163_bv();
	}

	private boolean makeVampire() {
		if (blood < 0) {
			return false;
		}
		setVampire();

		entity.addPotionEffect(new PotionEffect(Potion.weakness.id, 200));
		entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100));
		return true;
	}

	public void onUpdate() {
		if (isMinion() && !entity.worldObj.isRemote) {
			IMinionLord lord=getLord();
			if (lord != null) {
				if (lord.getRepresentingEntity().equals(entity.getAttackTarget())) {
					entity.setAttackTarget(lord.getMinionTarget());
				}
				if(entity.equals(entity.getAttackTarget())){
					entity.setAttackTarget(null);
				}
			}
		}
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setByte(KEY_TYPE, type);
		if (isMinion()) {
			if (this.lordId != null) {
				properties.setLong("BossUUIDMost", this.lordId.getMostSignificantBits());
				properties.setLong("BossUUIDLeast", this.lordId.getLeastSignificantBits());
			}
			if(activeCommand!=null){
				properties.setInteger("command_id", activeCommand.getId());
			}
		}
		compound.setTag(EXT_PROP_NAME, properties);

	}

	@Override
	public void setLord(IMinionLord b) {
		if(b!=null){
			if(b.getRepresentingEntity() instanceof EntityPlayer){
				this.setLordId(b.getThePersistentID());
			}
			else{
				Logger.w("VampireMob", "Only players can have non saveable minion. This(%s) cannot be controlled by %s", this,b);
			}

		}

	}

	private void setLordId(UUID id) {
		if (!id.equals(lordId)) {
			lordId = id;
		}

	}

	private void setMinion() {
		type = (byte) (type | 2);
		EntityAIModifier.makeMinion(this, entity);
		this.sync();
	}

	private void setVampire() {
		type = (byte) (type | 1);
		EntityAIModifier.addVampireMobTasks(entity);
		this.sync();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void loadUpdateFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey(KEY_TYPE)){
			type=nbt.getByte(KEY_TYPE);
		}
		if(nbt.hasKey("active_command_id")){
			this.activeCommandId=nbt.getInteger("active_command_id");
		}
		
	}

	@Override
	public void writeFullUpdateToNBT(NBTTagCompound nbt) {
		nbt.setByte(KEY_TYPE, type);
		if(activeCommand!=null){
			nbt.setInteger("active_command_id", activeCommand.getId());
		}
	}

	@Override
	public int getTheEntityID() {
		return entity.getEntityId();
	}

	public void sync() {
		if(!entity.worldObj.isRemote){
			Helper.sendPacketToPlayersAround(new UpdateEntityPacket(this), entity);
		}
		
	}
	
	/**
	 * If the mob can be bitten, returns its blood amount, otherwise returns -1
	 * @return
	 */
	public int getBlood(){
		if(canBeBitten()){
			return blood;
		}
		return -1;
	}

	@Override
	public boolean shouldBeSavedWithLord() {
		return false;
	}
	
	@Override
	public String toString(){
		return String.format(TAG+" of %s minion(%b) vampire(%b)", entity,isMinion(),isVampire());
	}

	@Override
	public ArrayList<IMinionCommand> getAvailableCommands() {
		return commands;
	}

	@Override
	public IMinionCommand getCommand(int id) {
		if(id<commands.size())return commands.get(id);
		return null;
	}

}
