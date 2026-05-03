package de.teamlapen.vampirism.misc.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {

    @ModifyReturnValue(method = "getChargeDuration", at = @At("RETURN"))
    private static int modifyCharge(int original, ItemStack crossbowStack, LivingEntity user) {
        if (crossbowStack.getItem() instanceof IHunterCrossbow crossbow) {
            return crossbow.getChargeDurationMod(crossbowStack, user.level());
        }
        return original;
    }
}
