package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "isFoil(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void hasOil(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if (OilUtils.hasAppliedOil(stack)) {
            cir.setReturnValue(true);
        }
    }
}
