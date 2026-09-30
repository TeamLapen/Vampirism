package de.teamlapen.faction.api.world.entities.minion;

import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;


public interface IMinionData {

    /**
     * @return The current executed task of the minion
     */
    IActiveTask<?> getActiveTask();

    /**
     * @return The current health of the minion
     */
    float getHealth();

    /**
     * @return The inventory of the minion
     */
    IMinionInventory getInventory();

    /**
     * @return The max health of the minion
     */
    int getMaxHealth();

    /**
     * @return The name of the minion
     */
    Component getName();

    interface IActiveTask<TState extends IMinionTask.IMinionTaskState> {

        Holder<? extends IMinionTask<?, TState>> task();

        TState data();
    }
}
