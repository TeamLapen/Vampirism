package de.teamlapen.vampirism.common.world.items.crossbow.behavior;

import de.teamlapen.vampirism.api.world.items.IVampirismQuarrel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class HeavyBehavior implements IVampirismQuarrel.IQuarrelBehavior {

    @Override
    public int color() {
        return 0xFF9DA0A8;
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
    public float knockbackMultiplier() {
        return 1.4f;
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
}