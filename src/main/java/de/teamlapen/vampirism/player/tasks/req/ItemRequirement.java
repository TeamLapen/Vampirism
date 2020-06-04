package de.teamlapen.vampirism.player.tasks.req;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ItemRequirement implements TaskRequirement.Requirement<Item> {

    @Nonnull
    private final ItemStack itemRequirement;
    @Nonnull
    private final ResourceLocation id;

    public ItemRequirement(@Nonnull ResourceLocation id, @Nonnull ItemStack itemRequirement) {
        this.id = id;
        this.itemRequirement = itemRequirement;
    }

    /**
     * @return a copy of the required itemStack
     */
    @Nonnull
    public ItemStack getItemStack() {
        return itemRequirement.copy();
    }

    @Nonnull
    @Override
    public TaskRequirement.Type getType() {
        return TaskRequirement.Type.ITEMS;
    }

    /**
     * removes the required items from the players inventory
     *
     * @param player the player which completed the task
     */
    @Override
    public void removeRequirement(IFactionPlayer<?> player) {
        player.getRepresentingPlayer().inventory.clearMatchingItems(itemStack -> itemStack.getItem() == this.itemRequirement.getItem(), getAmount(player));
    }

    @Nonnull
    @Override
    public Item getStat(IFactionPlayer<?> player) {
        return itemRequirement.getItem();
    }

    @Override
    public int getAmount(IFactionPlayer<?> player) {
        return itemRequirement.getCount();
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return id;
    }

}
