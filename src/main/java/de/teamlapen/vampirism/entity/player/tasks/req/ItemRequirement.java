package de.teamlapen.vampirism.entity.player.tasks.req;

import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.api.util.NonnullSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemRequirement implements TaskRequirement.Requirement<Item> {

    private final NonnullSupplier<ItemStack> itemRequirement;
    @NotNull
    private final ResourceLocation id;

    public ItemRequirement(@NotNull ResourceLocation id, NonnullSupplier<ItemStack> itemRequirement) {
        this.id = id;
        this.itemRequirement = itemRequirement;
    }

    @Override
    public int getAmount(IFactionPlayer<?> player) {
        return itemRequirement.get().getCount();
    }

    @Override
    @NotNull
    public ResourceLocation getId() {
        return id;
    }

    /**
     * @return a copy of the required itemStack
     */
    @NotNull
    public ItemStack getItemStack() {
        return itemRequirement.get();
    }

    @NotNull
    @Override
    public Item getStat(IFactionPlayer<?> player) {
        return itemRequirement.get().getItem();
    }

    @NotNull
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
    public void removeRequirement(@NotNull IFactionPlayer<?> player) {
        final ItemStack stack = itemRequirement.get();
        player.getRepresentingPlayer().getInventory().clearOrCountMatchingItems(itemStack -> ItemStackUtil.areStacksEqualIgnoreAmount(itemStack, stack), getAmount(player), player.getRepresentingPlayer().inventoryMenu.getCraftSlots() /*Not sure if the crafting container is correct here*/);
    }

}
