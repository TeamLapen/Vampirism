package de.teamlapen.faction.misc.mixin;

import de.teamlapen.faction.common.core.FactionDataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import squeek.appleskin.helpers.FoodHelper;

@Mixin(FoodHelper.class)
public class FoodHelperMixin {

    @Inject(method = "isFood", at = @At("RETURN"), cancellable = true)
    private static void considerFactionFood(ItemStack itemStack, Player player, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || itemStack.has(FactionDataComponents.FACTION_FOOD));
    }
}
