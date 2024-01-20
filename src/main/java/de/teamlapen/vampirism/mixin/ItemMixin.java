package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "isFoil", at = @At("RETURN"), cancellable = true)
    private void hasOil(@NotNull ItemStack stack, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (OilUtils.hasAppliedOil(stack)) {
            cir.setReturnValue(true);
        }
    }
}
