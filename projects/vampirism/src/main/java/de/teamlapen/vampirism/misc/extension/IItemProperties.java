package de.teamlapen.vampirism.misc.extension;

import net.minecraft.resources.DependantName;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;

public interface IItemProperties {

    Item.Properties vampirism$description(DependantName<Item, String> dependant);

    default Item.Properties vampirism$descriptionWithout(String regexPathReplace) {
        return vampirism$description(item -> Util.makeDescriptionId("item", item.identifier().withPath(item.identifier().getPath().replaceAll(regexPathReplace, ""))));
    }
}
