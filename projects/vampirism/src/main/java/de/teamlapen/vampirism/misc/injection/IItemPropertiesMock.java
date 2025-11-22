package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IItemProperties;
import net.minecraft.resources.DependantName;
import net.minecraft.world.item.Item;

public interface IItemPropertiesMock extends IItemProperties {
    @Override
    default Item.Properties vampirism$description(DependantName<Item, String> dependant) {
        return null;
    }
}
