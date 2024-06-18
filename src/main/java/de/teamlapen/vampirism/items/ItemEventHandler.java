package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

@EventBusSubscriber(modid = REFERENCE.MODID)
public class ItemEventHandler {

    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof VampireSwordItem sword && event.getSlotType() == EquipmentSlot.MAINHAND) {
            event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(BuiltInRegistries.ITEM.getKey(sword), sword.getAttackDamageModifier(stack), AttributeModifier.Operation.ADD_VALUE));
            event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(BuiltInRegistries.ITEM.getKey(sword), sword.getSpeedModifier(stack), AttributeModifier.Operation.ADD_VALUE));
        }
    }
}
