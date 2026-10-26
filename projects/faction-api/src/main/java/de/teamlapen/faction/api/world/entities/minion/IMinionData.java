package de.teamlapen.faction.api.world.entities.minion;

import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask;
import de.teamlapen.sync.api.MutableDataComponentMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;


public interface IMinionData extends MutableDataComponentMap {

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
    String getName();

    int getLevel();

    boolean isTaskLocked();

    boolean hasUsedSkillPoints();

    int getMaxLevel();

    int getRemainingStatPoints();

    IMinionEntry<?> getMinionEntry();

    interface IActiveTask<TState extends IMinionTask.IMinionTaskState> {

        Holder<? extends IMinionTask<TState>> task();

        TState data();
    }
}
