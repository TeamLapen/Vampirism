package de.teamlapen.vampirism.api.entity.minion;

import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;


public interface IMinionData {
    @Nonnull
    IMinionTask.IMinionTaskDesc<?> getCurrentTaskDesc();

    ITextComponent getFormattedName();

    float getHealth();

    IMinionInventory getInventory();

    int getMaxHealth();

    String getName();
}
