package de.teamlapen.faction.misc.extensions;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.registries.factions.DeferredFaction;
import de.teamlapen.faction.common.world.items.consume.FactionFoodProperties;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.DependantName;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public interface IItemProperties {

    Item.Properties factions$restrictFaction(@NotNull TagKey<IFaction<?>> factions);

    Item.Properties factions$restrictFaction(@NotNull Holder<? extends IFaction<?>> faction);

    Item.Properties factions$description(DependantName<Item, String> dependant);

    default Item.Properties factions$descriptionWithout(String regexPathReplace) {
        return factions$description(item -> Util.makeDescriptionId("item", item.identifier().withPath(item.identifier().getPath().replaceAll(regexPathReplace, ""))));
    }

    Item.Properties factions$withShiftDescription();

    Item.Properties factions$withShiftDescriptionParameter();

    Item.Properties factions$withShiftDescriptionParameter(Object[] parameters);

    Item.Properties factions$withShiftDescription(Component component);

    Item.Properties factions$defaultFood(FoodProperties foodProperties);

    Item.Properties factions$factionFood(FactionFoodProperties foodProperties);
}
