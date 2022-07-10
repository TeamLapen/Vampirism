package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;

public class SanguinareEffectInstance extends EffectInstance {
    public SanguinareEffectInstance(int effectDuration) {
        super(ModEffects.SANGUINARE.get(), effectDuration, 0, false, true);
    }

    @Override
    public boolean update(EffectInstance other) {
        //Sanguinare cannot be combined
        return false;
    }

    @Override
    public boolean tick(LivingEntity entityIn, Runnable runnable) {
        if (this.getDuration() % 10 == 0 && entityIn instanceof PlayerEntity) {
            if (!Helper.canBecomeVampire((PlayerEntity) entityIn)) {
                return false;
            }
        }
        return super.tick(entityIn, runnable);
    }
}
