package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.common.items.component.FactionRestriction;
import de.teamlapen.vampirism.misc.extension.IItemProperties;
import net.minecraft.resources.DependantName;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.Properties.class)
public class ItemPropertiesMixin implements IItemProperties, de.teamlapen.vampirism.api.misc.extensions.IItemProperties {

    @Shadow private DependantName<Item, String> descriptionId;

    @Unique
    @Override
    public Item.@NotNull Properties vampirism$description(@NotNull DependantName<Item, String> dependant) {
        this.descriptionId = dependant;
        return (Item.Properties) (Object) this;
    }

    @Override
    @Unique
    public Item.@NotNull Properties vampirism$restrictFaction(@NotNull TagKey<IFaction<?>> faction) {
        return FactionRestriction.apply(faction, (Item.Properties) (Object)this);
    }
}
