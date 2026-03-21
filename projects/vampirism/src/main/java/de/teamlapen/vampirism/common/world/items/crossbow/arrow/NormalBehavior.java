package de.teamlapen.vampirism.common.world.items.crossbow.arrow;

import de.teamlapen.vampirism.api.world.items.IVampirismCrossbowArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class NormalBehavior implements IVampirismCrossbowArrow.ICrossbowArrowBehavior {

    @Override
    public int color() {
        return 0xFFFFFFFF;
    }

    @Override
    public AbstractArrow.Pickup pickupBehavior() {
        return AbstractArrow.Pickup.ALLOWED;
    }

    @Override
    public boolean canBeInfinite() {
        return true;
    }

    @Override
    public float baseDamage(Level level, ItemStack stack, @Nullable LivingEntity shooter) {
        return 2;
    }
}
