package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.common.world.items.consume.VampireFoodProperties;
import de.teamlapen.vampirism.misc.extension.IItemProperties;
import net.minecraft.world.item.Item;

@Deprecated
public interface IItemPropertiesVampirismMock extends IItemProperties {

    @Override
    default Item.Properties vampirism$vampireFood(VampireFoodProperties vampireFoodProperties) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
