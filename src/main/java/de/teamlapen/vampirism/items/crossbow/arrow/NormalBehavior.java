package de.teamlapen.vampirism.items.crossbow.arrow;

import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NormalBehavior implements IVampirismCrossbowArrow.ICrossbowArrowBehavior{
    @Override
    public int color() {
        return 0xFFFFFF;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> textComponents, TooltipFlag tooltipFlag) {

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
    public float baseDamage(@NotNull Level level, @NotNull ItemStack stack, @Nullable LivingEntity shooter) {
        return 2;
    }
}
