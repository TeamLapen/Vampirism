package de.teamlapen.vampirism.items;


import com.google.common.base.Suppliers;
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
    private static final UUID SPEED_MODIFIER = UUID.fromString("CB3F55D5-6A5C-4F18-A497-9C11A33DB5CF");

    private final Supplier<ItemAttributeModifiers> mainHandAttributes;

    public UmbrellaItem() {
        super(new Properties().stacksTo(1));

        this.mainHandAttributes = Suppliers.memoize(() -> {
            ItemAttributeModifiers.Builder builder1 = ItemAttributeModifiers.builder();
            builder1.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_MODIFIER, "Umbrella modifier", -0.35, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND);
            return builder1.build();
        });
    }

    @Override
    public @NotNull ItemAttributeModifiers getAttributeModifiers(@NotNull ItemStack stack) {
        return mainHandAttributes.get();
    }

}
