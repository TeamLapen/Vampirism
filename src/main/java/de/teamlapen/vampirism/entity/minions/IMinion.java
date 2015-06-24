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
	
	/**
	 * 
	 * @return Whether the minion should be saved in the lords NBT data or not
	 */
	public boolean shouldBeSavedWithLord();
	
	/**
	 * @return The list of available minion commands
	 */
	public ArrayList<IMinionCommand> getAvailableCommands();
	
	/**
	 * 
	 * @param id
	 * @return The minion command represented by the given id
	 */
	public IMinionCommand getCommand(int id);
	
	/**
	 * Activates the given command
	 * @param command
	 */
	public void activateMinionCommand(IMinionCommand command);
}
