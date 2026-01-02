package de.teamlapen.factions.misc.mixin;

import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.common.components.FactionRestriction;
import de.teamlapen.factions.common.core.FactionDataComponents;
import de.teamlapen.factions.common.util.ShiftDescription;
import de.teamlapen.factions.misc.extensions.IItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.Properties.class)
public abstract class ItemPropertiesMixin implements IItemProperties {

    @Shadow
    private DependantName<Item, String> descriptionId;

    @Shadow
    public abstract <T> Item.Properties component(DataComponentType<T> component, T value);

    @Shadow
    private @Nullable ResourceKey<Item> id;

    @Override
    public Item.Properties factions$restrictFaction(@NotNull TagKey<IFaction<?>> faction) {
        return FactionRestriction.apply((Item.Properties) (Object) this, faction);
    }

    @Override
    public final Item.Properties factions$restrictFaction(@NotNull Holder<? extends IFaction<?>> faction) {
        return FactionRestriction.apply((Item.Properties) (Object) this, faction);
    }

    @Unique
    @Override
    public Item.@NotNull Properties factions$description(@NotNull DependantName<Item, String> dependant) {
        this.descriptionId = dependant;
        return (Item.Properties) (Object) this;
    }

    @Override
    public Item.Properties factions$withShiftDescription() {
        return this.factions$withShiftDescription(Component.translatable(this.id.identifier().toLanguageKey("tooltip")));
    }

    @Override
    public Item.Properties factions$withShiftDescriptionParameter() {
        this.component(FactionDataComponents.SHIFT_DESCRIPTION.get(), new ShiftDescription(this.id.identifier().toLanguageKey("tooltip")));
        return (Item.Properties) (Object) this;
    }

    @Override
    public Item.Properties factions$withShiftDescriptionParameter(Object[] parameters) {
        return this.factions$withShiftDescription(Component.translatable(this.id.identifier().toLanguageKey("tooltip"), parameters));
    }

    @Override
    public Item.Properties factions$withShiftDescription(Component component) {
        this.component(FactionDataComponents.SHIFT_DESCRIPTION.get(), new ShiftDescription(component));
        return (Item.Properties) (Object) this;
    }
}
