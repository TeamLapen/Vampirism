package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MobEntity.class)
public abstract class MixinMobEntityOptional extends LivingEntity {

    public MixinMobEntityOptional(EntityType<? extends LivingEntity> p_i48577_1_, World p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
    }

    @ModifyVariable(method = "doHurtTarget(Lnet/minecraft/entity/Entity;)Z", at = @At(value = "STORE", ordinal = 1), ordinal = 0)
    public float vampireSlayerEnchantment(float damage, Entity target) {
        return damage + MixinHooks.calculateVampireSlayerEnchantments(target, this.getMainHandItem());
    }
}
