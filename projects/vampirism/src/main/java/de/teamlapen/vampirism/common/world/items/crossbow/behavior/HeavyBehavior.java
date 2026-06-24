package de.teamlapen.vampirism.common.world.items.crossbow.behavior;

import de.teamlapen.vampirism.api.world.items.IVampirismQuarrel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HeavyBehavior implements IVampirismQuarrel.IQuarrelBehavior {

    @Override
    public int color() {
        return 0xFF9DA0A8;
    }

    @Override
    public Component getEffectDescription() {
        return Component.translatable("tooltip.vampirism.quarrel_heavy");
    }

    @Override
    public AbstractArrow.Pickup pickupBehavior() {
        return AbstractArrow.Pickup.ALLOWED;
    }

    @Override
    public float baseDamage(Level level, ItemStack stack, @Nullable LivingEntity shooter) {
        return 2.5f;
    }

    @Override
    public float damageMultiplier() {
        return 1.6f;
    }

    @Override
    public float velocityFactor() {
        return 0.8f;
    }

    @Override
    public float gravityFactor() {
        return 1.5f;
    }

    @Override
    public float inaccuracyFactor() {
        return 0.5f;
    }

    @Override
    public int extraPierceLevel() {
        return 2;
    }

    @Override
    public float chargeMultiplier() {
        return 1.33f;
    }

    @Override
    public void onHitEntity(ItemStack arrow, LivingEntity entity, AbstractArrow arrowEntity, Entity shootingEntity) {
        Vec3 movement = arrowEntity.getDeltaMovement();
        entity.knockback(0.5f, -movement.x, -movement.z);
    }
}