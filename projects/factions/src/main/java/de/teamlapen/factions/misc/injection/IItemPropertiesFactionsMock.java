package de.teamlapen.factions.misc.injection;

import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.misc.extensions.IItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.DependantName;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

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
    default Item.Properties factions$withShiftDescriptionParameter() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Item.Properties factions$withShiftDescriptionParameter(Object[] parameters) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Item.Properties factions$withShiftDescription(Component component) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
