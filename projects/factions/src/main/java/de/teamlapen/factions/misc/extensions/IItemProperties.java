package de.teamlapen.factions.misc.extensions;

import de.teamlapen.factions.api.factions.IFaction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public interface IItemProperties {

    Item.Properties factions$restrictFaction(@NotNull TagKey<IFaction<?>> faction);

}
