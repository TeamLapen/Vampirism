package de.teamlapen.faction.common.factions.tasks.requirements;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.tasks.TaskRequirement;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.core.FactionTasks;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ItemRequirement(Identifier id, ItemStackTemplate stack, Optional<Component> customDescription) implements TaskRequirement.Requirement<Item> {

    public static final MapCodec<ItemRequirement> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Identifier.CODEC.fieldOf("id").forGetter(ItemRequirement::id),
            ItemStackTemplate.CODEC.fieldOf("item").forGetter(ItemRequirement::stack),
            ComponentSerialization.CODEC.optionalFieldOf("description").forGetter(ItemRequirement::customDescription)
    ).apply(inst, ItemRequirement::new));

    public ItemRequirement(ItemStackTemplate stack, @Nullable Component customDescription) {
        this(stack.item().unwrapKey().orElseThrow().identifier(), stack, Optional.ofNullable(customDescription));
    }

    public ItemRequirement(ItemStackTemplate stack) {
        this(stack, null);
    }

    /**
     * @return the custom description if present, otherwise the name of the required item
     */
    @Override
    public Component description() {
        return this.customDescription.orElseGet(() -> this.stack.create().getItemName());
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
