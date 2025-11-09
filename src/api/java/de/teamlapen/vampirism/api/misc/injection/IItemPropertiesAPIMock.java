package de.teamlapen.vampirism.api.misc.injection;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.misc.extensions.IItemProperties;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public interface IItemPropertiesAPIMock extends IItemProperties {
    @Override
    default Item.Properties vampirism$restrictFaction(@NotNull TagKey<IFaction<?>> faction) {
        return null;
    }
}
