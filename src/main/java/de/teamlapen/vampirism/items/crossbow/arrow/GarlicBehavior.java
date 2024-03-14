package de.teamlapen.vampirism.items.crossbow.arrow;

import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GarlicBehavior implements IVampirismCrossbowArrow.ICrossbowArrowBehavior {
    @Override
    public int color() {
        return 0xffffff;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> textComponents, TooltipFlag tooltipFlag) {

    }

    @Override
    public void onHitEntity(ItemStack arrow, LivingEntity entity, AbstractArrow arrowEntity, Entity shootingEntity) {
        entity.addEffect(new MobEffectInstance(ModEffects.GARLIC.get(), 40, 1));
    }

    @Override
    public boolean canBeInfinite() {
        return VampirismConfig.BALANCE.allowInfiniteSpecialArrows.get();
    }

    @Override
    public float baseDamage(@NotNull Level level, @NotNull ItemStack stack, @Nullable LivingEntity shooter) {
        return 1;
    }
}
