package de.teamlapen.vampirism.player.tasks.req;

import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemRequirement implements TaskRequirement<Item> {

    private final ItemStack itemRequirement;

    public ItemRequirement(ItemStack itemRequirement) {
        this.itemRequirement = itemRequirement;
    }

    public ItemStack getItemStack() {
        return itemRequirement.copy();
    }

    @Nonnull
    @Override
    public Type getType() {
        return Type.ITEMS;
    }

    @Nonnull
    @Override
    public Item getStat() {
        return itemRequirement.getItem();
    }

    @Override
    public int getAmount() {
        return itemRequirement.getCount();
    }
}
