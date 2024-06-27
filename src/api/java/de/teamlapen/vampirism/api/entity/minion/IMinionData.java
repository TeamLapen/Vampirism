package de.teamlapen.vampirism.api.entity.minion;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;


public interface IMinionData {

    /**
     * @return The current executed task of the minion
     */
    @NotNull
    IMinionTask.IMinionTaskDesc<?> getCurrentTaskDesc();

    /**
     * @return The component variant of the minion's name
     */
    Component getFormattedName();

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
}
