package de.teamlapen.vampirism.entity.minions;

import de.teamlapen.vampirism.util.IPieElement;
import net.minecraft.item.ItemStack;
import org.eclipse.jdt.annotation.NonNull;

/**
 * Interface for minion commands, which can be executed by minions and activated via an {@link de.teamlapen.vampirism.client.gui.GUIMinionControl}
 * 
 * @author Max
 *
 */
public interface IMinionCommand extends IPieElement {

	/**
	 * @return if the command can be activated
	 */
	public boolean canBeActivated();

	@Override
	public String getUnlocalizedName();

	/**
	 * Called serverside when the command is activated. Usually used to add AI
	 */
	public void onActivated();

	/**
	 * Called serverside when the command is deactivated. Usually used to remove added AI
	 */
	public void onDeactivated();

	/**
	 * If this returns true, while the command is activated, minions (at least the RemoteVampireMinion) picksup such an item, if he stands on it.
	 * 
	 * @param item
	 * @return
	 */
	public boolean shouldPickupItem(@NonNull ItemStack item);
}
