package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArmorOfSwiftnessItem extends HunterArmorItem implements IItemWithTier {

    private final @NotNull TIER tier;

    private static float getSpeedReduction(TIER tier) {
        return switch (tier) {
            case NORMAL -> 0.035f;
            case ENHANCED -> 0.075f;
            case ULTIMATE -> 0.1f;
        };
    }

    public ArmorOfSwiftnessItem(@NotNull ArmorMaterial material, @NotNull ArmorType type, @NotNull TIER tier, Properties properties) {
        super(material, type, properties, ItemAttributeModifiers.builder().add(Attributes.MOVEMENT_SPEED, new AttributeModifier(VResourceLocation.mod("armor_modifier_" + type.getSerializedName()), getSpeedReduction(tier), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.bySlot(type.getSlot())).build());
        this.tier = tier;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        addTierInformation(tooltip);
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (pEntity.tickCount % 45 == 3 && pSlotId >= 36 && pSlotId <= 39 && pEntity instanceof Player player) {
            Equippable equippable = components().get(DataComponents.EQUIPPABLE);
            if (equippable != null && equippable.slot() == EquipmentSlot.CHEST) {
                boolean flag = true;
                int boost = Integer.MAX_VALUE;
                for (ItemStack stack : player.getInventory().armor) {
                    if (!stack.isEmpty() && stack.getItem() instanceof ArmorOfSwiftnessItem) {
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
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 50, boost, false, false));
                }
            }
        }
    }

    /**
     * Applied if complete armor is worn
     *
     * @return -1 if none
     */
    private int getJumpBoost(@NotNull TIER tier) {
        return switch (tier) {
            case ULTIMATE -> 1;
            case ENHANCED -> 0;
            default -> -1;
        };
    }

}
