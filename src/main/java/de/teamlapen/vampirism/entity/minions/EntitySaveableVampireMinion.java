package de.teamlapen.vampirism.entity.minions;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

public class EntitySaveableVampireMinion extends EntityVampireMinion {

	private final static String TAG = "SVampireMinion";

	protected IMinionLord lord;
	
	private final ArrayList<IMinionCommand> commands;
	

	public EntitySaveableVampireMinion(World world) {
		super(world);
		commands=new ArrayList<IMinionCommand>();
		commands.add(getActiveCommand());
		commands.add(new StayHereCommand(1));
	}

	@Override
	public boolean attackEntityFrom(DamageSource src, float value) {
		if (DamageSource.inWall.equals(src)) {
			return false;
		} else {
			return super.attackEntityFrom(src, value);
		}
	}

	/**
	 * Converts this minion to a remote minion
	 */
	public void convertToRemote() {
		EntityRemoteVampireMinion remote = (EntityRemoteVampireMinion) EntityList.createEntityByName(REFERENCE.ENTITY.VAMPIRE_MINION_REMOTE_NAME, worldObj);
		remote.copyDataFromMinion(this);
		remote.setHealth(this.getHealth());
		remote.copyLocationAndAnglesFrom(this);
		IMinionLord lord = getLord();
		if (lord != null) {
			if (lord instanceof VampirePlayer) {
				lord.getMinionHandler().unregisterMinion(this);
				remote.setLord(lord);
			} else {
				Logger.w(TAG, "The converted minion %s cannot be controlled by this lord %s", remote, lord);
			}

		}
		worldObj.spawnEntityInWorld(remote);
		this.setDead();
	}

	@Override
	public void copyDataFrom(Entity from, boolean p) {
		super.copyDataFrom(from, p);
		if (from instanceof EntitySaveableVampireMinion) {
			EntitySaveableVampireMinion m = (EntitySaveableVampireMinion) from;
			this.setLord(m.getLord());
			this.activateMinionCommand(m.getActiveCommand());
		}

	}

	@Override
	public @Nullable IMinionLord getLord() {
		return lord;
	}

	@Override
	protected void loadPartialUpdateFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("eid")) {
			Entity e = worldObj.getEntityByID(nbt.getInteger("eid"));
			if (e instanceof EntityPlayer) {
				this.lord = VampirePlayer.get((EntityPlayer) e);
			} else if (e instanceof IMinionLord) {
				this.lord = (IMinionLord) e;
			} else {
				Logger.w("EntityVampireMinion", "PartialUpdate: The given id(" + nbt.getInteger("eid") + ")[" + e + "] is no Minion Lord");
				return;
			}
		}

	}

	@Override
	public void onLivingUpdate() {
		if (!this.worldObj.isRemote) {
			if (lord == null) {

			} else if (!lord.isTheEntityAlive()) {
				lord = null;
				this.attackEntityFrom(DamageSource.magic, 1000);
			}

		}
		super.onLivingUpdate();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
	}

	/**
	 * Makes sure minions which are saved with their lord do not interact with portals
	 */
	@Override
	public void setInPortal() {
	}

	@Override
	public void setLord(IMinionLord lord) {
		if (!lord.equals(this.lord)) {
			lord.getMinionHandler().registerMinion(this, true);
			this.lord = lord;
		}
	}

	@Override
	public boolean shouldBeSavedWithLord() {
		return true;
	}

	@Override
	protected void writeUpdateToNBT(NBTTagCompound nbt) {
		if (lord != null) {
			nbt.setInteger("eid", lord.getRepresentingEntity().getEntityId());
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

	@Override
	protected IMinionCommand getDefaultCommand() {
		return new DefendLordCommand(0);
	}

}
