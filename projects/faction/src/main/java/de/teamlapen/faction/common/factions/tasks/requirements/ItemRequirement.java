package de.teamlapen.faction.common.factions.tasks.requirements;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.tasks.TaskRequirement;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.core.FactionTasks;
import de.teamlapen.faction.common.util.RegUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public record ItemRequirement(Identifier id, ItemStackTemplate stack, Component description) implements TaskRequirement.Requirement<Item> {

    public static final MapCodec<ItemRequirement> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        return inst.group(
                Identifier.CODEC.optionalFieldOf("id").forGetter(i -> java.util.Optional.of(i.id)),
                ItemStackTemplate.CODEC.fieldOf("item").forGetter(i -> i.stack),
                ComponentSerialization.CODEC.fieldOf("description").forGetter(i -> i.description)
        ).apply(inst, (id, item, desc) -> new ItemRequirement(id.orElseGet(() -> RegUtil.id(item.item())), item, desc));
    });

    public ItemRequirement(ItemStackTemplate itemRequirement, Component description) {
        this(RegUtil.id(itemRequirement.item()), itemRequirement, description);
    }

    @Override
    public int getAmount(IFactionPlayer<?> player) {
        return this.stack.count();
    }

    /**
     * @return a copy of the required itemStack
     */
    public ItemStack getItemStack() {
        return this.stack.create();
    }

    @Override
    public Item getStat(IFactionPlayer<?> player) {
        return this.stack.item().value();
    }

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
        player.asEntity().getInventory().clearOrCountMatchingItems(itemStack -> ItemStack.isSameItemSameComponents(itemStack, this.stack), getAmount(player), player.asEntity().inventoryMenu.getCraftSlots() /*Not sure if the crafting container is correct here*/);
    }

    @Override
    public MapCodec<? extends TaskRequirement.Requirement<?>> codec() {
        return FactionTasks.ITEM_REQUIREMENT.get();
    }
}
