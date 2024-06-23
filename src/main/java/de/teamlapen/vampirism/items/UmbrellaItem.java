package de.teamlapen.vampirism.items;


import com.google.common.base.Suppliers;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Supplier;

public class UmbrellaItem extends Item {

    private final Supplier<ItemAttributeModifiers> mainHandAttributes;

    public UmbrellaItem() {
        super(new Properties().stacksTo(1));

        this.mainHandAttributes = Suppliers.memoize(() -> {
            ItemAttributeModifiers.Builder builder1 = ItemAttributeModifiers.builder();
            builder1.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(BuiltInRegistries.ITEM.getKey(this), -0.35, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND);
            return builder1.build();
        });
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return mainHandAttributes.get();
    }
}
