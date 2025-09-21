package de.teamlapen.vampirism.common.mixin;

import de.teamlapen.vampirism.api.ItemPropertiesExtension;
import net.minecraft.resources.DependantName;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.Properties.class)
public class ItemPropertiesMixin implements ItemPropertiesExtension {

    @Shadow private DependantName<Item, String> descriptionId;

    @Unique
    @Override
    public Item.@NotNull Properties vampirism$description(@NotNull DependantName<Item, String> dependant) {
        this.descriptionId = dependant;
        return (Item.Properties) (Object) this;
    }
}
