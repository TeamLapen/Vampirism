package de.teamlapen.vampirism.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import de.teamlapen.vampirism.entity.ai.IMinionLord;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

public class EntityRemoteVampireMinion extends EntityVampireMinion {

	private final static String TAG = "RVampireMinion";
	private UUID lordId;

	public EntityRemoteVampireMinion(World world) {
		super(world);
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
			lord.getMinionHandler().unregisterMinion(this);
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
			lord.getMinionHandler().registerMinion(this, true);
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

}
