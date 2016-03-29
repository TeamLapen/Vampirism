package de.teamlapen.vampirism.api.entity.minions;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Interface for minion commands, which can be executed by {@link IMinion}
 *
 * @author Max
 */
public interface IMinionCommand {

    /**
     * @return if the command can be activated
     */
    boolean canBeActivated();


    String getUnlocalizedName();

    /**
     * Called serverside when the command is activated. Usually used to add AI
     */
    void onActivated();

    /**
     * Called serverside when the command is deactivated. Usually used to remove added AI
     */
    void onDeactivated();

    /**
     * If this returns true, while the command is activated, minions (at least the RemoteVampireMinion) picksup such an item, if he stands on it.
     *
     * @param item
     * @return
     */
    boolean shouldPickupItem(@Nonnull ItemStack item);
}