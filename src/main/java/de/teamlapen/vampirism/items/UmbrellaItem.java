package de.teamlapen.vampirism.items;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

import net.minecraft.world.item.Item.Properties;

public class UmbrellaItem extends VampirismItem {
    private final static String regName = "umbrella";
    private static final UUID SPEED_MODIFIER = UUID.fromString("CB3F55D5-6A5C-4F18-A497-9C11A33DB5CF");

    public UmbrellaItem() {
        super(regName, new Properties().stacksTo(1).tab(VampirismMod.creativeTab));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            multimap.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_MODIFIER, "Umbrella modifier", -0.35, AttributeModifier.Operation.MULTIPLY_BASE));
        }
        return multimap;
    }

}
