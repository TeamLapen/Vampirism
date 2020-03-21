package de.teamlapen.vampirism.api.entity.player.task;

import net.minecraft.item.ItemStack;

public class ItemRequirement extends TaskRequirement {

    private final ItemStack itemRequirement;

    public ItemRequirement(ItemStack itemRequirement) {
        super(Type.ITEMS);
        this.itemRequirement = itemRequirement;
    }

    public ItemStack getItemRequirement() {
        return itemRequirement.copy();
    }
}
