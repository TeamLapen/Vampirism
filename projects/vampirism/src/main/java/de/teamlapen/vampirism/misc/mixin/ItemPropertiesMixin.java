package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.util.ShiftDescription;
import de.teamlapen.vampirism.misc.extension.IItemProperties;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.Properties.class)
public abstract class ItemPropertiesMixin implements IItemProperties {

    @Shadow private DependantName<Item, String> descriptionId;

    @Shadow
    public abstract <T> Item.Properties component(DataComponentType<T> component, T value);

    @Shadow
    private @Nullable ResourceKey<Item> id;

    @Unique
    @Override
    public Item.@NotNull Properties vampirism$description(@NotNull DependantName<Item, String> dependant) {
        this.descriptionId = dependant;
        return (Item.Properties) (Object) this;
    }

    @Override
    public Item.Properties vampirism$withShiftDescription() {
        return this.vampirism$withShiftDescription(Component.translatable(this.id.identifier().toLanguageKey("tooltip")));
    }

    @Override
    public Item.Properties vampirism$withShiftDescriptionParameter() {
        this.component(ModDataComponents.SHIFT_DESCRIPTION.get(), new ShiftDescription(this.id.identifier().toLanguageKey("tooltip")));
        return (Item.Properties) (Object) this;
    }

    @Override
    public Item.Properties vampirism$withShiftDescriptionParameter(Object[] parameters) {
        return this.vampirism$withShiftDescription(Component.translatable(this.id.identifier().toLanguageKey("tooltip"), parameters));
    }

    @Override
    public Item.Properties vampirism$withShiftDescription(Component component) {
        this.component(ModDataComponents.SHIFT_DESCRIPTION.get(), new ShiftDescription(component));
        return (Item.Properties) (Object) this;
    }
}
