package de.teamlapen.vampirism.common.world.items.crossbow.behavior;

import de.teamlapen.vampirism.api.world.items.IVampirismQuarrel;
import de.teamlapen.vampirism.common.core.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BleedingBehavior implements IVampirismQuarrel.IQuarrelBehavior {

    @Override
    public int color() {
        return 11141120 | 0xFF000000;
    }

    @Override
    public void onHitEntity(ItemStack arrow, LivingEntity hitEntity, AbstractArrow arrowEntity, Entity shootingEntity) {
        hitEntity.addEffect(new MobEffectInstance(ModEffects.BLEEDING, 40, 0, false, false));
    }

    @Override
    public Component getEffectDescription() {
        return Component.translatable("tooltip.vampirism.quarrel_bleeding");
    }

    @Override
    public float baseDamage(Level level, ItemStack stack, @Nullable LivingEntity shooter) {
        return 0.5f;
    }
}
