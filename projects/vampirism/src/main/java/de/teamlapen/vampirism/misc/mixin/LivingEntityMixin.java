package de.teamlapen.vampirism.misc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IDraculaPlayer;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    public abstract boolean addEffect(MobEffectInstance newEffect);

    private LivingEntityMixin(@NotNull EntityType<? extends LivingEntity> type, @NotNull Level level) {
        super(type, level);
    }

    @Inject(method = "checkTotemDeathProtection", at = @At(value = "RETURN", ordinal = 1))
    private void handleTotemOfUndying(DamageSource killingDamage, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && Helper.isVampire(this)) {
            this.addEffect(new MobEffectInstance(ModEffects.FIRE_PROTECTION, 800, 5));
            this.addEffect(new MobEffectInstance(ModEffects.SUNSCREEN, 800, 4));
        }
    }

    @Inject(method = "canGlide", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void vampireGlide(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && vampirism$canFlyWings()) {
            cir.setReturnValue(true);
        }
    }

    @WrapOperation(method = "updateFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getRandom(Ljava/util/List;Lnet/minecraft/util/RandomSource;)Ljava/lang/Object;"))
    private <T> T falling(List<T> selections, RandomSource random, Operation<T> original, @Share("canFlyWings") LocalRef<Boolean> canFlyWings) {
        if (vampirism$canFlyWings() && selections.isEmpty()) {
            canFlyWings.set(true);
            return null;
        }
        canFlyWings.set(false);
        return original.call(selections, random);
    }

    @WrapOperation(method = "updateFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack s(LivingEntity instance, EquipmentSlot equipmentSlot, Operation<ItemStack> original, @Share("canFlyWings") LocalRef<Boolean> canFlyWings) {
        if (canFlyWings.get()) {
            return ItemStack.EMPTY;
        } else {
            return original.call(instance, equipmentSlot);
        }
    }

    @SuppressWarnings("ConstantValue")
    @Unique
    private boolean vampirism$canFlyWings() {
        return ((Object)this) instanceof Player player && IDraculaPlayer.getDracula(player).filter(IDraculaPlayer::wingsFunctionalOpen).isPresent();
    }
}
