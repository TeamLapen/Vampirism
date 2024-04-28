package de.teamlapen.vampirism.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MixinMobEntity extends LivingEntity {

    public MixinMobEntity(@NotNull EntityType<? extends LivingEntity> p_i48577_1_, @NotNull Level p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
    }
    @ModifyExpressionValue(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageBonus(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EntityType;)F", ordinal = 0))
    private float addVampireSlayeerDamageBonus(float damageBonus, Entity target) {
        return damageBonus + MixinHooks.calculateVampireSlayerEnchantments(target, this.getMainHandItem());
    }
}
