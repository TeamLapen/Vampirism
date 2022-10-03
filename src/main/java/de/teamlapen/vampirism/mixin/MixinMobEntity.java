package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Mob.class)
public abstract class MixinMobEntity extends LivingEntity {

    public MixinMobEntity(@NotNull EntityType<? extends LivingEntity> p_i48577_1_, @NotNull Level p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
    }

    @ModifyVariable(method = "doHurtTarget", at = @At(value = "STORE", ordinal = 1), ordinal = 0)
    public float vampireSlayerEnchantment(float damage, Entity target) {
        return damage + MixinHooks.calculateVampireSlayerEnchantments(target, this.getMainHandItem());
    }
}
