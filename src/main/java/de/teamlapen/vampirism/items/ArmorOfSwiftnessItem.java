package de.teamlapen.vampirism.items;

import com.google.common.base.Suppliers;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.mixin.accessor.ArmorItemAccessor;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ArmorOfSwiftnessItem extends HunterArmorItem implements IItemWithTier {

    private final @NotNull TIER tier;

    private static float getSpeedReduction(TIER tier) {
        return switch (tier) {
            case NORMAL -> 0.035f;
            case ENHANCED -> 0.075f;
            case ULTIMATE -> 0.1f;
        };
    }

    public ArmorOfSwiftnessItem(@NotNull Holder<net.minecraft.world.item.ArmorMaterial> material, @NotNull ArmorItem.Type type, @NotNull TIER tier) {
        super(material, type, new Item.Properties());
        this.tier = tier;
        Supplier<ItemAttributeModifiers> defaultModifiers = ((ArmorItemAccessor) this).getDefaultModifiers();
        defaultModifiers = Suppliers.compose((ItemAttributeModifiers modifiers) -> modifiers.withModifierAdded(Attributes.MOVEMENT_SPEED, new AttributeModifier(VResourceLocation.mod("armor_modifier_" + type.getSerializedName()), getSpeedReduction(tier), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.bySlot(type.getSlot())), defaultModifiers::get);
        ((ArmorItemAccessor) this).setDefaultModifiers(defaultModifiers);
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
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (pEntity.tickCount % 45 == 3 && pSlotId >= 36 && pSlotId <= 39 && pEntity instanceof Player player) {
            if (this.getType() == Type.CHESTPLATE) {
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
