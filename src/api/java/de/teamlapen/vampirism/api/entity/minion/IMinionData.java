package de.teamlapen.vampirism.api.entity.minion;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;


public interface IMinionData {
    @NotNull
    IMinionTask.IMinionTaskDesc<?> getCurrentTaskDesc();

    Component getFormattedName();

    float getHealth();

    IMinionInventory getInventory();

    int getMaxHealth();

    String getName();
}
