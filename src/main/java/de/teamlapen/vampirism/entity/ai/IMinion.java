package de.teamlapen.vampirism.entity.ai;

import net.minecraft.entity.EntityLiving;

public interface IMinion {

	/**
	 * @return The boss or null if none exist
	 */
	public EntityLiving getBoss();
}
