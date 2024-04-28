package de.teamlapen.vampirism.entity.vampire.action;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;

public class IgnoreSunDamageEntityAction<T extends PathfinderMob & IEntityActionUser> extends VampireEntityAction<T> implements ILastingAction<T> {

    public IgnoreSunDamageEntityAction(@NotNull EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public void activate(@NotNull T entity) {
        entity.addEffect(new MobEffectInstance(ModEffects.SUNSCREEN, getDuration(entity.getEntityLevel()), 0));

    }

    @Override
    public void deactivate(@NotNull T entity) {
        if (entity.getEffect(ModEffects.SUNSCREEN) != null && entity.getEffect(ModEffects.SUNSCREEN).getAmplifier() == 0) {
            entity.removeEffect(ModEffects.SUNSCREEN);
        }
    }

    @Override
    public int getCooldown(int level) {
        return VampirismConfig.BALANCE.eaIgnoreSundamageCooldown.get() * 20;
    }

    @Override
    public int getDuration(int level) {
        return VampirismConfig.BALANCE.eaIgnoreSundamageDuration.get() * 20;
    }

    @Override
    public int getWeight(@NotNull PathfinderMob entity) {
        if (!entity.getCommandSenderWorld().isDay() || entity.getCommandSenderWorld().isRaining()) {//Not perfectly accurate (the actual sundamage checks for celestial angle and also might exclude certain dimensions and biomes
            return 0;
        }
        return ((IVampire) entity).isGettingSundamage(entity.level()) ? 3 : 1;
    }

    @Override
    public void onUpdate(T entity, int duration) {
    }
}
