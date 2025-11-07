package de.teamlapen.vampirism.common.items.crossbow.arrow;

import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class NormalBehavior implements IVampirismCrossbowArrow.ICrossbowArrowBehavior {

    @Override
    public int color() {
        return 0xFFFFFFFF;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> textComponents, TooltipFlag tooltipFlag) {

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
