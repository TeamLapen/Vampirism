package de.teamlapen.vampirism.items;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class UmbrellaItem extends VampirismItem {
    private final static String regName = "umbrella";
    private static final UUID SPEED_MODIFIER = UUID.fromString("CB3F55D5-6A5C-4F18-A497-9C11A33DB5CF");

    public UmbrellaItem() {
        super(regName, new Properties().maxStackSize(1).group(VampirismMod.creativeTab));
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = HashMultimap.create();
        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(SPEED_MODIFIER, "Umbrella modifier", -0.35, AttributeModifier.Operation.MULTIPLY_BASE));
        }
        return multimap;
    }

}
