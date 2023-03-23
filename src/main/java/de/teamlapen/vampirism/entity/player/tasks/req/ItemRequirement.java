package de.teamlapen.vampirism.entity.player.tasks.req;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.core.ModTasks;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record ItemRequirement(@NotNull ResourceLocation id, @NotNull ItemStack stack, @NotNull Component description) implements TaskRequirement.Requirement<Item> {

    public static final Codec<ItemRequirement> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(
                ResourceLocation.CODEC.optionalFieldOf("id").forGetter(i -> java.util.Optional.of(i.id)),
                ItemStack.CODEC.fieldOf("item").forGetter(i -> i.stack),
                ExtraCodecs.COMPONENT.fieldOf("description").forGetter(i -> i.description)
        ).apply(inst, (id, item, desc) -> new ItemRequirement(id.orElseGet(() -> RegUtil.id(item.getItem())), item, desc));
    });

    public ItemRequirement(ItemStack itemRequirement, Component description) {
        this(RegUtil.id(itemRequirement.getItem()), itemRequirement, description);
    }

    @Override
    public int getAmount(IFactionPlayer<?> player) {
        return this.stack.getCount();
    }

    /**
     * @return a copy of the required itemStack
     */
    @NotNull
    public ItemStack getItemStack() {
        return this.stack;
    }

    @NotNull
    @Override
    public Item getStat(IFactionPlayer<?> player) {
        return this.stack.getItem();
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
        player.getRepresentingPlayer().getInventory().clearOrCountMatchingItems(itemStack -> ItemStackUtil.areStacksEqualIgnoreAmount(itemStack, this.stack), getAmount(player), player.getRepresentingPlayer().inventoryMenu.getCraftSlots() /*Not sure if the crafting container is correct here*/);
    }

    @Override
    public Codec<? extends TaskRequirement.Requirement<?>> codec() {
        return ModTasks.ITEM_REQUIREMENT.get();
    }
}
