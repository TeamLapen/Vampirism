package de.teamlapen.vampirism.api.misc.extensions;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public interface IItemProperties {

    Item.Properties vampirism$restrictFaction(@NotNull TagKey<IFaction<?>> faction);
}
