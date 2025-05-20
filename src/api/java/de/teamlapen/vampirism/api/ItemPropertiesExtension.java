package de.teamlapen.vampirism.api;

import net.minecraft.Util;
import net.minecraft.resources.DependantName;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public interface ItemPropertiesExtension {

    @NotNull
    Item.Properties vampirism$description(@NotNull DependantName<Item, String> dependant);

    static Item.Properties withDescription(Item.Properties properties, DependantName<Item, String> dependant) {
        return ((ItemPropertiesExtension) properties).vampirism$description(dependant);
    }

    static Item.Properties descriptionWithout(Item.Properties properties, String regexPathReplace) {
        return ((ItemPropertiesExtension) properties).vampirism$description(item -> Util.makeDescriptionId("item", item.location().withPath(item.location().getPath().replaceAll(regexPathReplace, ""))));
    }
}
