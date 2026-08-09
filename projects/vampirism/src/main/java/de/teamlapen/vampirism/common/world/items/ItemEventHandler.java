package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.common.core.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class ItemEventHandler {

    @SubscribeEvent
    public void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof VampireSwordItem sword) {
            event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(BuiltInRegistries.ITEM.getKey(sword), sword.getAttackDamageModifier(stack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(BuiltInRegistries.ITEM.getKey(sword), sword.getSpeedModifier(stack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            event.addModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(BuiltInRegistries.ITEM.getKey(sword).withSuffix("_purity"), sword.getPurityKnockbackResistanceModifier(stack) , AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            event.addModifier(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(BuiltInRegistries.ITEM.getKey(sword).withSuffix("_purity"), sword.getPurityInteractionRangeModifier(stack) , AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            event.addModifier(Attributes.SWEEPING_DAMAGE_RATIO, new AttributeModifier(BuiltInRegistries.ITEM.getKey(sword).withSuffix("_purity"), sword.getSweepingDamageModifier(stack) , AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        }
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack shattered = event.getLeft();
        if (!shattered.is(ModItems.SHATTERED_ARMOR)) return;

        ItemStack armor = ShatteredArmorItem.contained(shattered);
        if (armor.isEmpty() || !ShatteredArmorItem.getValidRecoveryItems(shattered).contains(event.getRight().getItem())) return;

        ItemStack repaired = ShatteredArmorItem.repair(shattered);
        if (repaired.isEmpty()) return;

        event.setOutput(repaired);
        event.setMaterialCost(1);
        event.setXpCost(1);
    }
}
