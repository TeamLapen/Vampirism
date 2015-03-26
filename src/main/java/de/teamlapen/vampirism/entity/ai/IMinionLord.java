package de.teamlapen.vampirism.entity.ai;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public interface IMinionLord {
	
	/**
	 * @return The target the lord's minions should attack, can be null
	 */
	public EntityLivingBase getMinionTarget();
	
	/**
	 * Entity's uuid
	 * @return
	 */
	public UUID getPersistentID();
	
	public boolean isEntityAlive();
	
	public double getDistanceSquared(Entity e);
	
	/**
	 * The Entity representing this lord. Can be the same as this object (e.g. VampireLord) or something else (e.g. VampirePlayer)
	 * @return
	 */
	public Entity getRepresentingEntity();
}
