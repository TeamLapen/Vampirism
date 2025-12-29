package de.teamlapen.factions.misc.mixin;

import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.common.components.FactionRestriction;
import de.teamlapen.factions.misc.extensions.IItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.Properties.class)
public class ItemPropertiesMixin implements IItemProperties {

    @Override
    public Item.Properties factions$restrictFaction(@NotNull TagKey<IFaction<?>> faction) {
        return FactionRestriction.apply((Item.Properties) (Object) this, faction);
    }

    @Override
    public final Item.Properties factions$restrictFaction(@NotNull Holder<? extends IFaction<?>> faction) {
        return FactionRestriction.apply((Item.Properties) (Object) this, faction);
    }
}
