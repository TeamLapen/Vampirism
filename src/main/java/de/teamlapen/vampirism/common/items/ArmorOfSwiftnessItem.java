package de.teamlapen.vampirism.common.items;

import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;

import java.util.Arrays;
import java.util.function.Consumer;

public class ArmorOfSwiftnessItem extends HunterArmorItem implements IItemWithTier {

    private final Tier tier;

    private static float getSpeedReduction(Tier tier) {
        return switch (tier) {
            case NORMAL -> 0.035f;
            case ENHANCED -> 0.075f;
            case ULTIMATE -> 0.1f;
        };
    }

    public ArmorOfSwiftnessItem(ArmorMaterial material, ArmorType type, Tier tier, Properties properties) {
        super(material, type, properties, ItemAttributeModifiers.builder().add(Attributes.MOVEMENT_SPEED, new AttributeModifier(VResourceLocation.mod("armor_modifier_" + type.getSerializedName()), getSpeedReduction(tier), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.bySlot(type.getSlot())).build());
        this.tier = tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        addTierInformation(tooltipComponents);
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
    }

    @Override
    public Tier getVampirismTier() {
        return tier;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);

        if (entity.tickCount % 45 == 3 && slot.isArmor() && entity instanceof Player player) {
            Equippable equippable = components().get(DataComponents.EQUIPPABLE);
            if (equippable != null && equippable.slot() == EquipmentSlot.CHEST) {
                boolean flag = true;
                int boost = Integer.MAX_VALUE;

                for (var armorStack : Arrays.stream(EquipmentSlot.values()).filter(x -> x.getType() == EquipmentSlot.Type.HUMANOID_ARMOR).map(player::getItemBySlot).toList()) {
                    if (!armorStack.isEmpty() && armorStack.getItem() instanceof ArmorOfSwiftnessItem) {
                        int b = getJumpBoost(getVampirismTier());
                        if (b < boost) {
                            boost = b;
                        }
                    } else {
                        flag = false;
                        break;
                    }
                }
                if (flag && boost > -1) {
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 50, boost, false, false));
                }
            }
        }
    }

    /**
     * Applied if complete armor is worn
     *
     * @return -1 if none
     */
    private int getJumpBoost(Tier tier) {
        return switch (tier) {
            case ULTIMATE -> 1;
            case ENHANCED -> 0;
            default -> -1;
        };
    }

}
