package de.teamlapen.vampirism.entity;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.Configs;
import de.teamlapen.vampirism.entity.ai.EntityAIModifier;
import de.teamlapen.vampirism.entity.minions.DefendLordCommand;
import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.minions.IMinionCommand;
import de.teamlapen.vampirism.entity.minions.IMinionLord;
import de.teamlapen.vampirism.entity.minions.JustFollowCommand;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.UpdateEntityPacket;
import de.teamlapen.vampirism.network.UpdateEntityPacket.ISyncableExtendedProperties;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.villages.VillageVampire;
import de.teamlapen.vampirism.villages.VillageVampireData;

public class VampireMob implements ISyncableExtendedProperties, IMinion {

	public final static String EXT_PROP_NAME = "VampireMob";

	private final static String TAG = "VampireMob";

	public static VampireMob get(EntityCreature mob) {
		return (VampireMob) mob.getExtendedProperties(VampireMob.EXT_PROP_NAME);
	}

	public static void register(EntityCreature mob) {
		mob.registerExtendedProperties(VampireMob.EXT_PROP_NAME, new VampireMob(mob));
	}
	private final EntityCreature entity;

	private final String KEY_TYPE = "type";

	private int blood;
	public final int max_blood;
	/**
	 * Determines if this mob can become a vampire or gets instantly killed after sucking blood from it
	 */
	private final boolean canBecomeVampire;
	private byte type;

	private UUID lordId = null;

	private IMinionCommand activeCommand = null;

	@SideOnly(Side.CLIENT)
	private int activeCommandId = -1;

	private final ArrayList<IMinionCommand> commands;

	public VampireMob(EntityCreature mob) {
		entity = mob;
		boolean flag1=false;
		if(mob instanceof EntityVampireHunter){
			max_blood=-2;
		}
		else if(Configs.bloodValuesRead){
			Integer i=Configs.bloodValues.get(mob.getClass().getName());
			if(i==null||i==0){
				max_blood=-1;
			}
			else{
				max_blood=Math.round(((float)Math.abs(i))*Configs.bloodValueMultiplier);
				if(Integer.signum(i)==1){
					flag1=true;
				}
			}
		}
		else{
			max_blood=5;
		}
		canBecomeVampire=flag1;
		blood = max_blood;
		type = (byte) 0;
		commands = new ArrayList<IMinionCommand>();
		commands.add(new DefendLordCommand(0, this));
		commands.add(new JustFollowCommand(1));
	}

	@Override
	public void activateMinionCommand(@Nullable IMinionCommand command) {
		if (command == null)
			return;
		if (!this.isMinion()) {
			Logger.w(TAG, "%s is no minions and can therby not execute this %s minion command", this, command);
			return;
		}
		if (activeCommand != null) {
			activeCommand.onDeactivated();
		}
		activeCommand = command;
		activeCommand.onActivated();
		this.sync();
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
		if (canBecomeVampire&&entity.worldObj.rand.nextInt(2) == 0) {
			makeVampire();
		} else {
			if (entity instanceof EntityVampireHunter) {
				entity.attackEntityFrom(DamageSource.magic, 1);
			} else {
				// Type should be only changed by the dedicated methods, but since the mob will die instantly it should not cause any problems
				type = (byte) (type | 1);
				entity.attackEntityFrom(DamageSource.magic, 1000);
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
	public int getActiveCommandId() {
		return this.activeCommandId;
	}

	@Override
	public ArrayList<IMinionCommand> getAvailableCommands() {
		return commands;
	}

	/**
	 * If the mob can be bitten, returns its blood amount, otherwise returns -1
	 * 
	 * @return
	 */
	public int getBlood() {
		if (canBeBitten()) {
			return blood;
		}
		return -1;
	}

	@Override
	public IMinionCommand getCommand(int id) {
		if (id < commands.size())
			return commands.get(id);
		return null;
	}

	@Override
	public IMinionLord getLord() {
		if (!isMinion()) {
			Logger.w("VampireMob", "Trying to get lord, but mob is no minion");
		}
		EntityPlayer player = (lordId == null ? null : entity.worldObj.func_152378_a(lordId));
		return (player == null ? null : VampirePlayer.get(player));
	}

	@Override
	public @NonNull EntityCreature getRepresentingEntity() {
		return entity;
	}

	@Override
	public int getTheEntityID() {
		return entity.getEntityId();
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
			if (properties.hasKey(KEY_TYPE)) {
				type = properties.getByte(KEY_TYPE);
			}
			IMinionCommand command = null;
			if (isMinion()) {
				if (properties.hasKey("BossUUIDMost")) {
					this.lordId = new UUID(properties.getLong("BossUUIDMost"), properties.getLong("BossUUIDLeast"));
					Logger.d(TAG, "Mob %s is minion with lord %s", entity, lordId);
					command = this.getCommand(properties.getInteger("command_id"));
					if (command == null) {
						command = this.getCommand(0);
					}
				} else {
					Logger.w(TAG, "Mob %s is a minion but does not have a lord uuid saved (%s). This should only happen once", entity, properties);
					if (isVampire()) {
						type = 2;
					} else {
						type = 0;
					}
				}
			}

			if (isMinion()) {
				this.setMinion();
				this.activateMinionCommand(command);
			}
			if (isVampire()) {
				this.setVampire();
			}
		}

	}

	@SideOnly(Side.CLIENT)
	@Override
	public void loadUpdateFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey(KEY_TYPE)) {
			type = nbt.getByte(KEY_TYPE);
		}
		if (nbt.hasKey("BossUUIDMost")) {
			this.lordId = new UUID(nbt.getLong("BossUUIDMost"), nbt.getLong("BossUUIDLeast"));
		}
		if (nbt.hasKey("active_command_id")) {
			this.activeCommandId = nbt.getInteger("active_command_id");
		}
	}

	public boolean lowEnoughHealth() {
		return (entity.getHealth() / entity.getMaxHealth()) <= BALANCE.SUCK_BLOOD_HEALTH_REQUIREMENT;
	}

	public void makeMinion(IMinionLord lord) {
		this.setLord(lord);
		setMinion();
		this.activateMinionCommand(this.getCommand(0));
		entity.func_110163_bv();
		this.sync();
	}

	private boolean makeVampire() {
		if (blood < 0||!canBecomeVampire) {
			return false;
		}
		setVampire();

		entity.addPotionEffect(new PotionEffect(Potion.weakness.id, 200));
		entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100));
		this.sync();
		return true;
	}

	public void onUpdate() {
		if (isMinion() && !entity.worldObj.isRemote) {
			IMinionLord lord = getLord();
			if (lord != null) {
				if (lord.getRepresentingEntity().equals(entity.getAttackTarget())) {
					entity.setAttackTarget(lord.getMinionTarget());
				}
				if (entity.equals(entity.getAttackTarget())) {
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
			properties.setLong("BossUUIDMost", this.lordId.getMostSignificantBits());
			properties.setLong("BossUUIDLeast", this.lordId.getLeastSignificantBits());
			if (activeCommand != null) {
				properties.setInteger("command_id", activeCommand.getId());
			}
		}
		compound.setTag(EXT_PROP_NAME, properties);

	}

	@Override
	public void setLord(IMinionLord b) {
		if (b != null) {
			if (b.getRepresentingEntity() instanceof EntityPlayer) {
				this.setLordId(b.getThePersistentID());
			} else {
				Logger.w("VampireMob", "Only players can have non saveable minion. This(%s) cannot be controlled by %s", this, b);
			}

		}

	}

	private void setLordId(UUID id) {
		if (!id.equals(lordId)) {
			lordId = id;
		}

	}

	/**
	 * Sets the minion bit and adds minion specific tasks. Does not sync. This clears all AI tasks, so make sure to add any new tasks after this.
	 */
	private void setMinion() {
		type = (byte) (type | 2);
		EntityAIModifier.makeMinion(this, entity);
	}

	/**
	 * Sets the vampire bit and adds vampire specific tasks. Does not sync.
	 */
	private void setVampire() {
		type = (byte) (type | 1);
		EntityAIModifier.addVampireMobTasks(entity);
	}

	@Override
	public boolean shouldBeSavedWithLord() {
		return false;
	}

	public void sync() {
		if (!entity.worldObj.isRemote) {
			Helper.sendPacketToPlayersAround(new UpdateEntityPacket(this), entity);
		}

	}

	@Override
	public String toString() {
		return String.format(TAG + " of %s minion(%b) vampire(%b)", entity, isMinion(), isVampire());
	}

	@Override
	public void writeFullUpdateToNBT(NBTTagCompound nbt) {
		nbt.setByte(KEY_TYPE, type);
		if (activeCommand != null) {
			nbt.setInteger("active_command_id", activeCommand.getId());
		}
		if (isMinion()) {
			nbt.setLong("BossUUIDMost", this.lordId.getMostSignificantBits());
			nbt.setLong("BossUUIDLeast", this.lordId.getLeastSignificantBits());
		}
	}

}
