package de.teamlapen.faction.misc.injection;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.misc.extensions.IItemProperties;
import net.minecraft.core.Holder;
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
}
