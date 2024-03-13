package de.teamlapen.vampirism.api.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public interface IHunterCrossbow extends ICrossbow {

    default int getCombinedUseDuration(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        ItemStack otherItemStack = entity.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        return getCombinedUseDuration(stack, otherItemStack, entity);
    }

    default int getCombinedUseDuration(ItemStack crossbowStack, ItemStack otherStack, LivingEntity entity) {
        if (otherStack.getItem() instanceof IHunterCrossbow otherItem && !isCharged(otherStack) && canUseDoubleCrossbow(entity) && !entity.getProjectile(otherStack).isEmpty()) {
            return this.asItem().getUseDuration(crossbowStack) + otherItem.asItem().getUseDuration(otherStack);
        }
        return this.asItem().getUseDuration(crossbowStack);
    }

    default int getCombinedChargeDuration(ItemStack crossbow, LivingEntity entity, InteractionHand hand) {
        ItemStack otherItemStack = entity.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        return getCombinedChargeDuration(crossbow, otherItemStack, entity);
    }

    default int getCombinedChargeDuration(ItemStack crossbow, ItemStack otherStack, LivingEntity entity) {
        if (otherStack.getItem() instanceof IHunterCrossbow other && !isCharged(otherStack) && canUseDoubleCrossbow(entity) && !entity.getProjectile(otherStack).isEmpty()) {
            return this.getChargeDuration(crossbow) + other.getChargeDuration(otherStack);
        }
        return this.getChargeDuration(crossbow);
    }

    boolean canUseDoubleCrossbow(LivingEntity entity);
}
