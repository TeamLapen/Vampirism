package de.teamlapen.vampirism.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/DigDurabilityEnchantment;shouldIgnoreDurabilityDrop(Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/util/RandomSource;)Z", shift = At.Shift.BEFORE))
    private void test(int pAmount, RandomSource pRandom, ServerPlayer pUser, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) LocalIntRef j) {
        if (Helper.isHunter(pUser) && HunterPlayer.get(pUser).getSkillHandler().isSkillEnabled(HunterSkills.EFFICIENT_TOOLING) && pRandom.nextFloat() < 0.2f)  {
            j.set(j.get()-1);
        }
    }
}
