package de.teamlapen.vampirism.entity.ai;

import net.minecraft.entity.EntityCreature;

public interface IMinion {

	/**
	 * The returned EntityLiving has to implement {@link IMinionLord}
	 * 
	 * @return The boss or null if none exist
	 */
	public IMinionLord getLord();

	public EntityCreature getRepresentingEntity();

	/**
	 * Sets the boss
	 * 
	 * @param b
	 *            Has to implement {@link IMinionLord}
	 */
	public void setLord(IMinionLord b);
	
	public boolean shouldBeSavedWithLord();
}
