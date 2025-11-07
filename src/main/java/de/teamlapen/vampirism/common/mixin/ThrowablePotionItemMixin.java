package de.teamlapen.vampirism.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.teamlapen.vampirism.common.core.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrowablePotionItem.class)
public abstract class ThrowablePotionItemMixin extends PotionItem implements ProjectileItem {

    protected ThrowablePotionItemMixin(Item.Properties properties) {
        super(properties);
    }

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectileFromRotation(Lnet/minecraft/world/entity/projectile/Projectile$ProjectileFactory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;FFF)Lnet/minecraft/world/entity/projectile/Projectile;"))
    private <T extends Projectile> T s(Projectile.ProjectileFactory<T> factory, ServerLevel level, ItemStack spawnedFrom, LivingEntity owner, float z, float velocity, float inaccuracy, Operation<T> original) {
        return original.call(factory, level, spawnedFrom, owner, z, owner instanceof LivingEntity living && living.hasEffect(ModEffects.FREEZE) ? velocity / 3 : velocity, inaccuracy);
    }
}
