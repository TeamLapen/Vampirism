package de.teamlapen.factions.misc.injection;

import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.misc.extensions.IItemProperties;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public interface IItemPropertiesFactionsMock extends IItemProperties {

    @Override
    default Item.Properties factions$restrictFaction(@NotNull TagKey<IFaction<?>> faction) {
        return null;
    }
}
