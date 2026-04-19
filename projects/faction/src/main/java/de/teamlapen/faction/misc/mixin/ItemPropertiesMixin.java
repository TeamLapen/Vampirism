package de.teamlapen.faction.misc.mixin;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.util.ShiftDescription;
import de.teamlapen.faction.common.world.items.consume.FactionFoodList;
import de.teamlapen.faction.misc.extensions.IItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.Properties.class)
public abstract class ItemPropertiesMixin implements IItemProperties {

    @Shadow
    private DependantName<Item, String> descriptionId;

    @Shadow
    public abstract <T> Item.Properties component(DataComponentType<T> component, T value);

    @Shadow
    private @Nullable ResourceKey<Item> id;

    @Shadow
    private DataComponentInitializers.Initializer<Item> componentInitializer;

    @Override
    public Item.Properties factions$restrictFaction(@NotNull TagKey<IFaction<?>> faction) {
        return FactionRestriction.apply((Item.Properties) (Object) this, faction);
    }

    @Override
    public final Item.Properties factions$restrictFaction(@NotNull Holder<? extends IFaction<?>> faction) {
        return FactionRestriction.apply((Item.Properties) (Object) this, faction);
    }

    @Unique
    @Override
    public Item.@NotNull Properties factions$description(@NotNull DependantName<Item, String> dependant) {
        this.descriptionId = dependant;
        return (Item.Properties) (Object) this;
    }

    @Override
    public Item.Properties factions$withShiftDescription() {
        return this.factions$withShiftDescription(Component.translatable(this.id.identifier().toLanguageKey("tooltip")));
    }

    @Override
    public Item.Properties factions$withShiftDescriptionParameter() {
        this.component(FactionDataComponents.SHIFT_DESCRIPTION.get(), new ShiftDescription(this.id.identifier().toLanguageKey("tooltip")));
        return (Item.Properties) (Object) this;
    }

    @Override
    public Item.Properties factions$withShiftDescriptionParameter(Object[] parameters) {
        return this.factions$withShiftDescription(Component.translatable(this.id.identifier().toLanguageKey("tooltip"), parameters));
    }

    @Override
    public Item.Properties factions$withShiftDescription(Component component) {
        this.component(FactionDataComponents.SHIFT_DESCRIPTION.get(), new ShiftDescription(component));
        return (Item.Properties) (Object) this;
    }

    @Override
    public Item.Properties factions$factionFood(FactionFoodList factionFoodList) {
        this.component(FactionDataComponents.FACTION_FOOD.get(), factionFoodList);
        return (Item.Properties) (Object) this;
    }

    @Override
    public Item.Properties factions$factionFood(FactionFoodList factionFoodList, Consumable consumable) {
        this.component(FactionDataComponents.FACTION_FOOD.get(), factionFoodList);
        this.component(DataComponents.CONSUMABLE, consumable);
        return (Item.Properties) (Object) this;
    }

    @Unique
    public Item.Properties factions$addAttributes(ItemAttributeModifiers modifiers) {
        var previousInitializer = this.componentInitializer;
        this.componentInitializer = previousInitializer.andThen((components, context, key) -> {
            previousInitializer.run(components, context, key);
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
            components.build().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers().forEach(entry -> builder.add(entry.attribute(), entry.modifier(), entry.slot()));
            modifiers.modifiers().forEach(entry -> builder.add(entry.attribute(), entry.modifier(), entry.slot()));
            components.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
        });

        return (Item.Properties) (Object) this;
    }
}
