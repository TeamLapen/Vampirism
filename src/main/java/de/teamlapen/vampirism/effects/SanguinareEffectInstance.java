package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;

public class SanguinareEffectInstance extends MobEffectInstance {
    public SanguinareEffectInstance(int effectDuration) {
        super(ModEffects.SANGUINARE.get(), effectDuration, 0, false, true);
    }

    @Override
    public boolean update(@Nonnull MobEffectInstance other) {
        //Sanguinare cannot be combined
        return false;
    }

    @Override
    public boolean tick(@Nonnull LivingEntity entityIn, @Nonnull Runnable runnable) {
        if (this.getDuration() % 10 == 0 && entityIn instanceof Player) {
            if (!Helper.canBecomeVampire((Player) entityIn)) {
                return false;
            }
        }
        return super.tick(entityIn, runnable);
    }
}
