package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.potion.EffectInstance;

public class SunscreenEntityAction<T extends CreatureEntity & IEntityActionUser> extends VampireEntityAction<T> implements ILastingAction<T> {

    public SunscreenEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public void activate(T entity) {
        entity.getRepresentingEntity().addPotionEffect(new EffectInstance(ModEffects.sunscreen, getDuration(entity.getLevel()), 3, false, false));
    }

    @Override
    public void deactivate(T entity) {
        entity.getRepresentingEntity().removePotionEffect(ModEffects.sunscreen);
    }

    @Override
    public int getCooldown(int level) {
        return VampirismConfig.BALANCE.eaSunscreenCooldown.get() * 20;
    }

    @Override
    public int getDuration(int level) {
        return VampirismConfig.BALANCE.eaSunscreenDuration.get() * 20;
    }

    @Override
    public int getWeight(CreatureEntity entity) {
        if (!entity.getEntityWorld().isDaytime() || entity.getEntityWorld().isRaining()) {//Not perfectly accurate (the actual sundamage checks for celestial angle and also might exclude certain dimensions and biomes
            return 0;
        }
        return ((IVampire) entity).isGettingSundamage() ? 3 : 1;
    }

    @Override
    public void onUpdate(T entity, int duration) {
    }
}
