package de.teamlapen.vampirism.items;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;

import java.util.UUID;

public class UmbrellaItem extends Item {
    private static final UUID SPEED_MODIFIER = UUID.fromString("CB3F55D5-6A5C-4F18-A497-9C11A33DB5CF");

    public UmbrellaItem() {
        super(new Properties().stacksTo(1).tab(VampirismMod.creativeTab));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            multimap.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_MODIFIER, "Umbrella modifier", -0.35, AttributeModifier.Operation.MULTIPLY_BASE));
        }
        return multimap;
    }

}
