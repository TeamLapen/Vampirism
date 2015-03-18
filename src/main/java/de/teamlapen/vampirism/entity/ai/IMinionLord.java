package de.teamlapen.vampirism.entity.ai;

import net.minecraft.entity.EntityLivingBase;

public interface IMinionLord {
	
	/**
	 * @return The target the lord's minions should attack
	 */
	public EntityLivingBase getMinionTarget();
}
