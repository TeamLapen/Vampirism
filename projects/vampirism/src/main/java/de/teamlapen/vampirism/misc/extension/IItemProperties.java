package de.teamlapen.vampirism.misc.extension;

import net.minecraft.Util;
import net.minecraft.resources.DependantName;
import net.minecraft.world.item.Item;

public interface IItemProperties {

    Item.Properties vampirism$description(DependantName<Item, String> dependant);

    default Item.Properties vampirism$descriptionWithout(String regexPathReplace) {
        return vampirism$description(item -> Util.makeDescriptionId("item", item.location().withPath(item.location().getPath().replaceAll(regexPathReplace, ""))));
    }
}
