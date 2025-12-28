package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.DependantName;
import net.minecraft.world.item.Item;

@Deprecated
public interface IItemPropertiesVampirismMock extends IItemProperties {
    @Override
    default Item.Properties vampirism$description(DependantName<Item, String> dependant) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Item.Properties vampirism$withShiftDescription() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Item.Properties vampirism$withShiftDescriptionParameter() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Item.Properties vampirism$withShiftDescriptionParameter(Object[] parameters) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Item.Properties vampirism$withShiftDescription(Component component) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
