package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SanguinareEffectInstance extends MobEffectInstance {

    public SanguinareEffectInstance(int duration) {
        super(ModEffects.SANGUINARE, duration, 0, false, true);
    }

    @Override
    public boolean update(MobEffectInstance other) {
        //Sanguinare cannot be combined
        return false;
    }

    @Override
    public boolean tick(LivingEntity entity, Runnable onExpirationRunnable) {
        if (this.getDuration() % 10 == 0 && entity instanceof Player) {
            if (!Helper.canBecomeVampire((Player) entity)) {
                return false;
            }
        }
        return super.tick(entity, onExpirationRunnable);
    }
}
