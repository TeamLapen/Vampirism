package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.particles.ParticleTypes;

public class RegenerationEntityAction<T extends CreatureEntity & IEntityActionUser> extends VampireEntityAction<T> implements ILastingAction<T> {

    public RegenerationEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public void activate(T entity) {
    }

    @Override
    public void deactivate(T entity) {
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.REGENERATION_COOLDOWN * 20;
    }

    @Override
    public int getDuration(int level) {
        return Balance.ea.REGENERATION_DURATION * 20;
    }

    @Override
    public int getWeight(CreatureEntity entity) {
        double healthPercent = entity.getHealth() / entity.getMaxHealth();
        if (healthPercent < 0.1) {
            return 3;
        } else if (healthPercent < 0.4) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public void onUpdate(T entity, int duration) {
        entity.getRepresentingEntity().heal(entity.getMaxHealth() / 100 * Balance.ea.REGENERATION_AMOUNT / (getDuration(entity.getLevel()) * 20)); // seconds in ticks
        if (duration % 15 == 0) {
            ModParticles.spawnParticlesServer(entity.getEntityWorld(), ParticleTypes.HEART, entity.posX, entity.posY + 1, entity.posZ, 3, 0.2, 0.2, 0.2, 0);
        }

    }
}