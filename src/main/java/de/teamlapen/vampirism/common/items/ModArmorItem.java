package de.teamlapen.vampirism.common.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

public class ModArmorItem extends Item {

    public ModArmorItem(ArmorMaterial armorMaterial, ArmorType armorType, Properties properties) {
        super(addAttributes(properties.humanoidArmor(armorMaterial, armorType), ItemAttributeModifiers.EMPTY));
    }

    public ModArmorItem(ArmorMaterial armorMaterial, ArmorType armorType, Properties properties, ItemAttributeModifiers customModifiers) {
        super(addAttributes(properties.humanoidArmor(armorMaterial, armorType), customModifiers));
    }

    private static Properties addAttributes(Properties properties, ItemAttributeModifiers customModifiers) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        copyTo((ItemAttributeModifiers) properties.components.map.get(DataComponents.ATTRIBUTE_MODIFIERS), builder);
        copyTo(customModifiers, builder);
        properties.attributes(builder.build());
        return properties;
    }

    private static void copyTo(ItemAttributeModifiers from, ItemAttributeModifiers.Builder to) {
        from.modifiers().forEach(entry -> to.add(entry.attribute(), entry.modifier(), entry.slot()));
    }
}
