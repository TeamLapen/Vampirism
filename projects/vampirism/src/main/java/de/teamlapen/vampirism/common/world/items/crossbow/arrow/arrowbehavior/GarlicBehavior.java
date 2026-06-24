package de.teamlapen.vampirism.common.world.items.crossbow.arrow.arrowbehavior;

import de.teamlapen.vampirism.api.world.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GarlicBehavior implements IVampirismCrossbowArrow.ICrossbowArrowBehavior {

    @Override
    public int color() {
        return 0xFFFFFFFF;
    }

    @Override
    public void onHitEntity(ItemStack arrow, LivingEntity entity, AbstractArrow arrowEntity, Entity shootingEntity) {
        entity.addEffect(new MobEffectInstance(ModEffects.GARLIC, 100, 1));
    }

    @Override
    public Component getEffectDescription() {
        return Component.translatable("tooltip.vampirism.quarrel_garlic");
    }

    @Override
    public boolean canBeInfinite() {
        return ModConfig.balance().allowInfiniteSpecialArrows.get();
    }

    @Override
    public float baseDamage(Level level, ItemStack stack, @Nullable LivingEntity shooter) {
        return 1;
    }
}
