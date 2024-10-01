package de.teamlapen.vampirism.api.items.oil;


import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IOil {

    /**
     * adds oil tooltip lines to the oil item
     */
    void getDescription(ItemStack stack, @Nullable Item.TooltipContext level, List<Component> tooltips);

    /**
     * oil color code
     */
    int getColor();
}
