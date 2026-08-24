package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.common.world.items.ShatteredArmorItem;
import de.teamlapen.vampirism.misc.extension.IItemStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "applyDamage(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    private void vampirism$shatterInsteadOfBreaking(int newDamage, LivingEntity player, Consumer<Item> onBreak, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (newDamage < stack.getMaxDamage()) return;

        if (ShatteredArmorItem.shatterOnBreak(stack, player)) {
            stack.setCount(0);
            ci.cancel();
        }
    }
}
