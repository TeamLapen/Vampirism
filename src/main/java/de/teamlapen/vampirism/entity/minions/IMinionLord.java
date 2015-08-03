package de.teamlapen.vampirism.entity.minions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.eclipse.jdt.annotation.NonNull;

import java.util.UUID;

public interface IMinionLord {

	long getLastComebackCall();

	int getMaxMinionCount();

	SaveableMinionHandler getMinionHandler();

	/**
	 * @return The target the lord's minions should attack, can be null
	 */
	EntityLivingBase getMinionTarget();

	/**
	 * The Entity representing this lord. Can be the same as this object (e.g. VampireLord) or something else (e.g. VampirePlayer)
	 * 
	 * @return
	 */
	@NonNull
	EntityLivingBase getRepresentingEntity();

	double getTheDistanceSquared(Entity e);

	/**
	 * Entity's uuid
	 * 
	 * @return
	 */
	UUID getThePersistentID();

	boolean isTheEntityAlive();
}
