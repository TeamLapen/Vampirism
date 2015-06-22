package de.teamlapen.vampirism.entity.minions;


import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.minecraft.entity.EntityCreature;

public interface IMinion {

	/**
	 * The returned EntityLiving has to implement {@link IMinionLord}
	 * 
	 * @return The boss or null if none exist
	 */
	public @Nullable IMinionLord getLord();

	public @NonNull EntityCreature getRepresentingEntity();

	/**
	 * Sets the boss
	 * 
	 * @param b
	 *            Has to implement {@link IMinionLord}
	 */
	public void setLord(IMinionLord b);
	
	public boolean shouldBeSavedWithLord();
	
	public ArrayList<IMinionCommand> getAvailableCommands();
	
	public IMinionCommand getCommand(int id);
	
	public void activateMinionCommand(IMinionCommand command);
}
