package de.teamlapen.vampirism.items;


import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

import net.minecraft.world.item.Item.Properties;
import org.jetbrains.annotations.NotNull;

public class UmbrellaItem extends Item {
    private static final UUID SPEED_MODIFIER = UUID.fromString("CB3F55D5-6A5C-4F18-A497-9C11A33DB5CF");
    private final @NotNull Multimap<Attribute, AttributeModifier> modifierMultimap;

    public UmbrellaItem() {
        super(new Properties().stacksTo(1).tab(VampirismMod.creativeTab));

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_MODIFIER, "Umbrella modifier", -0.35, AttributeModifier.Operation.MULTIPLY_BASE));
        modifierMultimap = builder.build();

    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack stack) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            return modifierMultimap;
        }
        return ImmutableMultimap.of();
    }

}
