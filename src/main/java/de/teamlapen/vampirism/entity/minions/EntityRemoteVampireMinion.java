package de.teamlapen.vampirism.entity.minions;

import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Nullable;

import org.eclipse.jdt.annotation.NonNull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.entity.ai.EntityAIFleeSun;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * Remote vampire minion, which acts independent from the lord, but can try to find him via his UUID
 * Not designed to fight for the player
 * @author Maxanier
 *
 */
public class EntityRemoteVampireMinion extends EntityVampireMinion {

	private final static String TAG = "RVampireMinion";
	private UUID lordId;
	
	private final ArrayList<IMinionCommand> commands;
	

	public EntityRemoteVampireMinion(World world) {
		super(world);
		this.tasks.addTask(2, new EntityAIAvoidEntity(this,EntityVampireHunter.class,MathHelper.floor_float(BALANCE.MOBPROP.VAMPIRE_DISTANCE_HUNTER*1.5F),1.1,1.4));
		this.tasks.addTask(2, new EntityAIRestrictSun(this));
		this.tasks.addTask(7, new EntityAIFleeSun(this, 1.1F,true));
		commands=new ArrayList<IMinionCommand>();
		commands.add(getActiveCommand());
		commands.add(new ConvertToSaveableCommand(1,this));
		commands.add(new CollectBloodCommand(2,this));
		commands.add(new DefendAreaCommand(3,this));
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
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if (nbt.hasKey("LordUUIDMost")) {
			this.lordId = new UUID(nbt.getLong("LordUUIDMost"), nbt.getLong("LordUUIDLeast"));
		}
	}

	@Override
	public void setLord(IMinionLord lord) {
		if (lord != null && lord.getRepresentingEntity() instanceof EntityPlayer) {
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
	}

	@Override
	protected void writeUpdateToNBT(NBTTagCompound nbt) {
		if (lordId != null) {
			nbt.setLong("LordUUIDMost", this.lordId.getMostSignificantBits());
			nbt.setLong("LordUUIDLeast", this.lordId.getLeastSignificantBits());
		}
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
	
	/**
	 * Command which converts this remote minion into a saveable one
	 * @author Maxanier
	 *
	 */
	private static class ConvertToSaveableCommand extends DefaultMinionCommand{

		private final EntityRemoteVampireMinion entity;
		public ConvertToSaveableCommand(int id,EntityRemoteVampireMinion minion) {
			super(id);
			this.entity=minion;
		}

		@Override
		public String getUnlocalizedName() {
			return "minioncommand.vampirism.converttosaveable";
		}

		@Override
		public void onActivated() {
			entity.convertToSaveable();
			EntityItem gem = new EntityItem(entity.worldObj, entity.posX,entity.posY,entity.posZ,new ItemStack(ModItems.gemOfBinding,1));
			entity.worldObj.spawnEntityInWorld(gem);
		}

		@Override
		public void onDeactivated() {		
		}

		@Override
		public int getMinU() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getMinV() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}

	@Override
	protected @NonNull IMinionCommand getDefaultCommand() {
		return new StayHereCommand(0,this);
	}

}
