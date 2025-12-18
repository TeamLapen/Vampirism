package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IItemProperties;
import net.minecraft.resources.DependantName;
import net.minecraft.world.item.Item;

@Deprecated
public interface IItemPropertiesVampirismMock extends IItemProperties {
    @Override
    default Item.Properties vampirism$description(DependantName<Item, String> dependant) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
