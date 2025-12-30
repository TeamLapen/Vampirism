package de.teamlapen.faction.misc.extensions;

import de.teamlapen.faction.api.factions.IFaction;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public interface IItemProperties {

    Item.Properties factions$restrictFaction(@NotNull TagKey<IFaction<?>> factions);

    Item.Properties factions$restrictFaction(@NotNull Holder<? extends IFaction<?>> faction);

}
