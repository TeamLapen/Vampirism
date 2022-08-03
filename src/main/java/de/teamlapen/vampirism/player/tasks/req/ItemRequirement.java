package de.teamlapen.vampirism.player.tasks.req;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

@SuppressWarnings("ClassCanBeRecord")
public class ItemRequirement implements TaskRequirement.Requirement<Item> {

    @Nonnull
    private final ItemStack itemRequirement;
    @Nonnull
    private final ResourceLocation id;

    public ItemRequirement(@Nonnull ResourceLocation id, @Nonnull ItemStack itemRequirement) {
        this.id = id;
        this.itemRequirement = itemRequirement;
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

    /**
     * @return a copy of the required itemStack
     */
    @Nonnull
    public ItemStack getItemStack() {
        return itemRequirement.copy();
    }

    @Nonnull
    @Override
    public Item getStat(IFactionPlayer<?> player) {
        return itemRequirement.getItem();
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
        player.getRepresentingPlayer().getInventory().clearOrCountMatchingItems(itemStack -> ItemStack.matches(itemStack, this.itemRequirement), getAmount(player), player.getRepresentingPlayer().inventoryMenu.getCraftSlots() /*Not sure if the crafting container is correct here*/);
    }

}
