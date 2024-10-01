package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Oil implements IOil {

    private final int color;

    public Oil(int color) {
        this.color = color;
    }

    @Override
    public void getDescription(ItemStack stack, @Nullable Item.TooltipContext context, List<Component> tooltips) {
    }

    @Override
    public int getColor() {
        return this.color;
    }
}
