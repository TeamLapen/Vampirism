package de.teamlapen.vampirism.entity.minions;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.entity.EntityHunterBase;
import de.teamlapen.vampirism.entity.ai.VampireAIFleeSun;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.item.ItemBloodBottle;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.eclipse.jdt.annotation.NonNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Remote vampire minion, which acts independent from the lord, but can try to find him via his UUID Not designed to fight for the player
 * 
 * @author Maxanier
 *
 */
public class EntityRemoteVampireMinion extends EntityVampireMinion {

	/**
	 * Command which converts this remote minion into a saveable one
	 * 
	 * @author Maxanier
	 *
	 */
	private static class ConvertToSaveableCommand extends DefaultMinionCommand {

		private final EntityRemoteVampireMinion entity;

		public ConvertToSaveableCommand(int id, EntityRemoteVampireMinion minion) {
			super(id);
			this.entity = minion;
		}

		@Override
		public int getMinU() {
			return 144;
		}

		@Override
		public int getMinV() {
			return 0;
		}

		@Override
		public String getUnlocalizedName() {
			return "minioncommand.vampirism.converttosaveable";
		}

		@Override
		public void onActivated() {
			entity.convertToSaveable();
			EntityItem gem = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(ModItems.gemOfBinding, 1));
			entity.worldObj.spawnEntityInWorld(gem);
		}

		@Override
		public void onDeactivated() {
		}

	}
	private final static String TAG = "RVampireMinion";

	private final static String KEY_COMEBACK = "l_cbc";
	private UUID lordId;
	private final ArrayList<IMinionCommand> commands;
	private final IMinionCommand comeBack;

	private long lastComeBackCall;

	public EntityRemoteVampireMinion(World world) {
		super(world);
		this.tasks.addTask(2, new EntityAIAvoidEntity(this, EntityHunterBase.class, MathHelper.floor_float(BALANCE.MOBPROP.VAMPIRE_DISTANCE_HUNTER * 1.5F), 1.1, 1.4));
		this.tasks.addTask(2, new EntityAIRestrictSun(this));
		this.tasks.addTask(7, new VampireAIFleeSun(this, 1.1F, true));
		commands = new ArrayList<IMinionCommand>();
		commands.add(getActiveCommand());
		commands.add(new ConvertToSaveableCommand(1, this));
		commands.add(new CollectBloodCommand(2, this));
		commands.add(new DefendAreaCommand(3, this));
		comeBack = new ComeBackToPlayerCommand(-1, this);
	}

	/**
	 * Converts this minion to a saveable minion
	 */
	public void convertToSaveable() {
		EntitySaveableVampireMinion save = (EntitySaveableVampireMinion) EntityList.createEntityByName(REFERENCE.ENTITY.VAMPIRE_MINION_SAVEABLE_NAME, worldObj);
		save.copyDataFromMinion(this);
		save.copyLocationAndAnglesFrom(this);
		save.setHealth(this.getHealth());
		IMinionLord lord = getLord();
		if (lord != null) {
			save.setLord(lord);
		}
		worldObj.spawnEntityInWorld(save);
		this.dropEquipment(true, 100);
		this.setDead();
	}

	@Override
	public void copyDataFrom(Entity from, boolean p) {
		super.copyDataFrom(from, p);
		if (from instanceof EntityRemoteVampireMinion) {
			EntityRemoteVampireMinion m = (EntityRemoteVampireMinion) from;
			lordId = m.lordId;
			this.activateMinionCommand(m.getActiveCommand());

		}
	}

	@Override
	public ArrayList<IMinionCommand> getAvailableCommands() {
		return commands;
	}

	@Override
	public IMinionCommand getCommand(int id) {
		if (id == -1) return comeBack;
		if (id < commands.size())
			return commands.get(id);
		return null;
	}

	@Override
	protected @NonNull IMinionCommand getDefaultCommand() {
		return new StayHereCommand(0, this);
	}

	@Override
	public @Nullable IMinionLord getLord() {
		EntityPlayer player = (lordId == null ? null : worldObj.func_152378_a(lordId));
		return (player == null ? null : VampirePlayer.get(player));
	}

	@Override
	protected void loadPartialUpdateFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("LordUUIDMost")) {
			this.lordId = new UUID(nbt.getLong("LordUUIDMost"), nbt.getLong("LordUUIDLeast"));
		}
	}

	@Override
	public void onDeath(DamageSource src) {
		MinionHelper.sendMessageToLord(this, "text.vampirism.sorry_i_died_while_doing", "\\: ", getActiveCommand().getUnlocalizedName());
		this.dropItem(ModItems.gemOfBinding,1);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		IMinionLord l = this.getLord();
		if (l != null && l instanceof VampirePlayer) {
			long lc = l.getLastComebackCall();
			if (lc > lastComeBackCall) {
				this.activateMinionCommand(comeBack);
				this.lastComeBackCall = lc;
				MinionHelper.sendMessageToLord(this, "text.vampirism.coming_back");
			}
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if (nbt.hasKey("LordUUIDMost")) {
			this.lordId = new UUID(nbt.getLong("LordUUIDMost"), nbt.getLong("LordUUIDLeast"));
		}
		this.lastComeBackCall = nbt.getLong(KEY_COMEBACK);
	}

	@Override
	public void setLord(IMinionLord lord) {
		if (lord != null && lord instanceof VampirePlayer) {
			this.lastComeBackCall = lord.getLastComebackCall();
			this.lordId = lord.getThePersistentID();
		} else {
			Logger.w(TAG, "Only players can have non saveable minion. This(%s) cannot be controlled by %s", this, lord);
			throw new IllegalArgumentException("Only players can have non saveable minion");
		}
	}

	@Override
	public boolean shouldBeSavedWithLord() {
		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		if (lordId != null) {
			nbt.setLong("LordUUIDMost", this.lordId.getMostSignificantBits());
			nbt.setLong("LordUUIDLeast", this.lordId.getLeastSignificantBits());
		}
		nbt.setLong(KEY_COMEBACK, lastComeBackCall);
	}

	@Override
	protected void writeUpdateToNBT(NBTTagCompound nbt) {
		if (lordId != null) {
			nbt.setLong("LordUUIDMost", this.lordId.getMostSignificantBits());
			nbt.setLong("LordUUIDLeast", this.lordId.getLeastSignificantBits());
		}
	}

	@Override
	public void addBlood(int amt) {
		ItemStack item = getEquipmentInSlot(0);
		if (item != null) {
			if (item.getItem().equals(Items.glass_bottle)) {
				ItemStack stack1 = new ItemStack(ModItems.bloodBottle, 1, 0);
				ItemBloodBottle.addBlood(stack1, amt);
				setCurrentItemOrArmor(0, stack1);
				return;
			}
			if (item.getItem().equals(ModItems.bloodBottle)) {
				ItemBloodBottle.addBlood(item, amt);
				return;
			}
		}
	}
}
