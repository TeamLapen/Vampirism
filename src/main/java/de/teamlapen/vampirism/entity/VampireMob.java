package de.teamlapen.vampirism.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.Configs;
import de.teamlapen.vampirism.ModPotion;
import de.teamlapen.vampirism.entity.ai.VanillaAIModifier;
import de.teamlapen.vampirism.entity.convertible.BiteableEntry;
import de.teamlapen.vampirism.entity.convertible.BiteableRegistry;
import de.teamlapen.vampirism.entity.convertible.EntityConvertedCreature;
import de.teamlapen.vampirism.entity.minions.*;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.UpdateEntityPacket;
import de.teamlapen.vampirism.network.UpdateEntityPacket.ISyncableExtendedProperties;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.villages.VillageVampire;
import de.teamlapen.vampirism.villages.VillageVampireData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.UUID;

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

	private final String KEY_BLOOD = "blood";

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
		BiteableEntry entry = BiteableRegistry.getEntry(mob);
		if (entry != null) {
			max_blood = entry.blood;
			canBecomeVampire = entry.convertible;
		}
		else{
			max_blood = -1;
			canBecomeVampire = false;
		}
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
	 */
	public int bite(boolean canTurn) {
		if (blood <= 0) {
			return 0;
		}
		int amt = Math.min(blood, (int) (max_blood / 2F));
		blood -= amt;
		if (blood < max_blood / 2) {
			if (blood == 0 || entity.getRNG().nextInt(blood) == 0) {

				if (canBecomeVampire && canTurn && entity.getRNG().nextBoolean()) {
					if (Configs.realismMode) {
						entity.addPotionEffect(new PotionEffect(ModPotion.sanguinare.id, BALANCE.VAMPIRE_MOB_SANGUINARE_DURATION * 20));
					} else {
						makeVampire();
					}

				} else {
					entity.attackEntityFrom(DamageSource.magic, 1000);
					if (entity instanceof EntityVillager) {
						VillageVampire v = VillageVampireData.get(entity.worldObj).findNearestVillage(entity);
						if (v != null) {
							v.villagerBitten();
						}
					}
				}
			}

		}

		// If entity is a child only give 1/3 blood
		if (entity instanceof EntityAgeable) {
			if (((EntityAgeable) entity).getGrowingAge() < 0) {
				return Math.round((float) amt / 3);
			}
		}
		this.sync();
		return amt;

	}

	@Override
	public int getActiveCommandId() {
		return this.activeCommandId;
	}

	@Override
	public ArrayList<IMinionCommand> getAvailableCommands() {
		return commands;
	}

	public int getBlood() {
		return blood;
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
			if (properties.hasKey(KEY_BLOOD)) {
				blood = properties.getInteger(KEY_BLOOD);
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
		if (nbt.hasKey(KEY_BLOOD)) {
			blood = nbt.getInteger(KEY_BLOOD);
		}
		if (nbt.hasKey("BossUUIDMost")) {
			this.lordId = new UUID(nbt.getLong("BossUUIDMost"), nbt.getLong("BossUUIDLeast"));
		}
		if (nbt.hasKey("active_command_id")) {
			this.activeCommandId = nbt.getInteger("active_command_id");
		}
	}

//	public boolean lowEnoughHealth() {
//		return (entity.getHealth() / entity.getMaxHealth()) <= BALANCE.SUCK_BLOOD_HEALTH_REQUIREMENT;
//	}

	public void makeMinion(IMinionLord lord) {
		this.setLord(lord);
		setMinion();
		this.activateMinionCommand(this.getCommand(0));
		entity.func_110163_bv();
		this.sync();
	}

	public boolean makeVampire() {
		if (!canBecomeVampire || blood < 0) {
			return false;
		}
		blood = 0;
		setVampire();
		Entity e = BiteableRegistry.convert(entity);
		entity.setDead();
		entity.worldObj.spawnEntityInWorld(e);
//		entity.addPotionEffect(new PotionEffect(Potion.weakness.id, 200, 2));
//		entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 2));
//		this.sync();
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

		if (!entity.worldObj.isRemote) {
			if (blood > 0 && blood < max_blood && entity.ticksExisted % 40 == 0) {
				entity.addPotionEffect(new PotionEffect(Potion.weakness.id, 40));
				entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 40, 2));
				if (entity.getRNG().nextInt(8) == 0) {
					blood++;
				}
			}
			if (entity.isPotionActive(ModPotion.sanguinare.id) && entity.getActivePotionEffect(ModPotion.sanguinare).getDuration() == 1) {
				this.makeVampire();
			}

			/*
			* Compat code to convert old mobs
			 */
			if (isVampire()) {
				EntityConvertedCreature convertedCreature = BiteableRegistry.convert(entity);
				entity.setDead();
				entity.worldObj.spawnEntityInWorld(convertedCreature);
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
		if (blood >= 0) {
			properties.setInteger(KEY_BLOOD, getBlood());
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
		VanillaAIModifier.makeMinion(this, entity);
	}

	/**
	 * Sets the vampire bit and adds vampire specific tasks. Does not sync.
	 */
	private void setVampire() {
		type = (byte) (type | 1);
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
		nbt.setInteger(KEY_BLOOD, blood);
		if (activeCommand != null) {
			nbt.setInteger("active_command_id", activeCommand.getId());
		}
		if (isMinion()) {
			nbt.setLong("BossUUIDMost", this.lordId.getMostSignificantBits());
			nbt.setLong("BossUUIDLeast", this.lordId.getLeastSignificantBits());
		}
	}

}
