package de.teamlapen.vampirism.api.entity.minion;

import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;


public interface IMinionData {
    @Nonnull
    IMinionTask.IMinionTaskDesc<?> getCurrentTaskDesc();

    Component getFormattedName();

    float getHealth();

    IMinionInventory getInventory();

    int getMaxHealth();

    String getName();
}
