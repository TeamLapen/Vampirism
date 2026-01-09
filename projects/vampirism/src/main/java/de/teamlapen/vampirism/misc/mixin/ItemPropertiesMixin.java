package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.world.items.consume.VampireFoodProperties;
import de.teamlapen.vampirism.misc.extension.IItemProperties;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Item.Properties.class)
public abstract class ItemPropertiesMixin implements IItemProperties {

    @Shadow
    public abstract <T> Item.Properties component(DataComponentType<T> component, T value);

    @Override
    public Item.Properties vampirism$vampireFood(VampireFoodProperties vampireFoodProperties) {
        this.component(ModDataComponents.VAMPIRE_FOOD.get(), vampireFoodProperties);
        return (Item.Properties) (Object) this;
    }
}
