package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.items.component.FactionRestriction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Equippable.class)
public class EquippableMixin {

    @Inject(method = "swapWithEquipmentSlot" , at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private void checkFactionUsability(ItemStack newItem, Player player, CallbackInfoReturnable<InteractionResult> cir) {
        if (FactionRestriction.canUse(player, newItem, true)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
