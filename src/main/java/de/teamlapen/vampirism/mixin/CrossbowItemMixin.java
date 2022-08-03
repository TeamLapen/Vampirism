package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.items.crossbow.ArrowContainer;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {

    @Inject(method = "addChargedProjectile", at = @At("HEAD"), cancellable = true)
    private static void addProjectiles(ItemStack crossbow, ItemStack arrows, CallbackInfo ci) {
        if (arrows.getItem() instanceof ArrowContainer) {
            ((ArrowContainer) arrows.getItem()).getArrows().forEach(arrow -> {
                CrossbowItem.addChargedProjectile(crossbow, new ItemStack(arrow));
            });
            ci.cancel();
        }
    }
}
