package de.teamlapen.vampirism.common.mixin;

import de.teamlapen.vampirism.common.potions.BasePotion;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {

    @Inject(method = "hasContainerMix", at = @At("HEAD"), cancellable = true)
    private void handleItemConversionHunterPotion(ItemStack reagent, ItemStack potionItem, CallbackInfoReturnable<Boolean> cir) {
        if (vampirism$shouldBlockBrewing(potionItem)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "mix", at = @At("HEAD"), cancellable = true)
    private void handleDoReactionHunterPotion(ItemStack potion, ItemStack potionItem, CallbackInfoReturnable<ItemStack> cir) {
        if (vampirism$shouldBlockBrewing(potionItem)) {
            cir.setReturnValue(potionItem);
            cir.cancel();
        }
    }

    @Unique
    private static boolean vampirism$shouldBlockBrewing(ItemStack input) {
        return BasePotion.isHunterPotion(input, true).map(Potion::getEffects).flatMap(effects -> effects.stream().map(MobEffectInstance::getEffect).map(Holder::value).filter(MobEffect::isBeneficial).findAny()).isPresent();
    }
}
