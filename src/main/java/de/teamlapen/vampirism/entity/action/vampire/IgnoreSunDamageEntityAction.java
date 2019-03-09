package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import net.minecraft.entity.EntityCreature;
import net.minecraft.potion.PotionEffect;

public class IgnoreSunDamageEntityAction<T extends EntityCreature & IEntityActionUser> extends VampireEntityAction<T> implements ILastingAction<T> {

    public IgnoreSunDamageEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.IGNORE_SUNDAMAGE_COOLDOWN * 20;
    }

    @Override
    public int getDuration(int level) {
        return Balance.ea.IGNORE_SUNDAMAGE_DURATION * 20;
    }

    @Override
    public void deactivate(T entity) {
        if (entity.getActivePotionEffect(ModPotions.sunscreen) != null && entity.getActivePotionEffect(ModPotions.sunscreen).getAmplifier() == 0) {
            entity.removePotionEffect(ModPotions.sunscreen);
        }
    }

    @Override
    public void onUpdate(T entity, int duration) {
    }

    @Override
    public void activate(T entity) {
        entity.addPotionEffect(new PotionEffect(ModPotions.sunscreen, getDuration(entity.getLevel()), 0));

    }

    @Override
    public void updatePreAction(T entity, int duration) {
    }

    @Override
    public int getWeight(T entity) {
        if (!entity.getEntityWorld().isDaytime() || entity.getEntityWorld().isRaining()) {//Not perfectly accurate (the actual sundamage checks for celestial angle and also might exclude certain dimensions and biomes
            return 0;
        }
        return ((IVampire) entity).isGettingSundamage() ? 3 : 1;
    }
}
