package de.teamlapen.faction.misc.injection;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.common.util.ShiftDescription;
import de.teamlapen.faction.common.world.items.consume.FactionFoodList;
import de.teamlapen.faction.misc.extensions.IItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.DependantName;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Unique;

@Deprecated
public interface IItemPropertiesFactionsMock extends IItemProperties {

    @Override
    default Item.Properties factions$restrictFaction(@NotNull TagKey<IFaction<?>> faction) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Item.Properties factions$restrictFaction(@NotNull Holder<? extends IFaction<?>> faction) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
    
    @Override
    default Item.Properties factions$description(DependantName<Item, String> dependant) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Item.Properties factions$withShiftDescription() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Item.Properties factions$withShiftDescription(Component component) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Item.Properties factions$withShiftDescription(ShiftDescription description) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Item.Properties factions$factionFood(FactionFoodList factionFoodList) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Item.Properties factions$factionFood(FactionFoodList factionFoodList, Consumable consumable) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Item.Properties factions$addAttributes(ItemAttributeModifiers modifiers) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
