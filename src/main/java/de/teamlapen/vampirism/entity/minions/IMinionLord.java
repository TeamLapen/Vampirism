package de.teamlapen.vampirism.entity.minions;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public interface IMinionLord {

	/**
	 * @return The target the lord's minions should attack, can be null
	 */
	public EntityLivingBase getMinionTarget();

	/**
	 * The Entity representing this lord. Can be the same as this object (e.g. VampireLord) or something else (e.g. VampirePlayer)
	 * 
	 * @return
	 */
	public EntityLivingBase getRepresentingEntity();

	public double getTheDistanceSquared(Entity e);

	/**
	 * Entity's uuid
	 * 
	 * @return
	 */
	public UUID getThePersistentID();

	public boolean isTheEntityAlive();
	
	public int getMaxMinionCount();
	
	public SaveableMinionHandler getMinionHandler();
	
	public long getLastComebackCall();
}
