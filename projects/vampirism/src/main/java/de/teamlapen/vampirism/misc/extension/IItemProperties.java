package de.teamlapen.vampirism.misc.extension;

import de.teamlapen.vampirism.common.world.items.consume.VampireFoodProperties;
import net.minecraft.world.item.Item;

public interface IItemProperties {

    Item.Properties vampirism$vampireFood(VampireFoodProperties vampireFoodProperties);
}
