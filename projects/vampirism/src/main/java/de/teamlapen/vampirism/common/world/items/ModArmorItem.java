package de.teamlapen.vampirism.common.world.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

public class ModArmorItem extends Item {

    public ModArmorItem(ArmorMaterial armorMaterial, ArmorType armorType, Properties properties) {
        super(properties.humanoidArmor(armorMaterial, armorType));
    }

    public ModArmorItem(ArmorMaterial armorMaterial, ArmorType armorType, Properties properties, ItemAttributeModifiers customModifiers) {
        super(properties.humanoidArmor(armorMaterial, armorType).factions$addAttributes(customModifiers));
    }
}
