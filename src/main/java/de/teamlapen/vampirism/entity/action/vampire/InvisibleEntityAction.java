package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;

public class InvisibleEntityAction<T extends CreatureEntity & IEntityActionUser> extends VampireEntityAction<T> implements ILastingAction<T> {

    public InvisibleEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public void activate(T entity) {
        ModParticles.spawnParticlesServer(entity.getEntityWorld(), ParticleTypes.EXPLOSION, entity.getPosX(), entity.getPosY(), entity.getPosZ(), 60, 1, 1, 1, 0);

    }

    @Override
    public void deactivate(T entity) {
        entity.getRepresentingEntity().setInvisible(false);

    }

    @Override
    public int getCooldown(int level) {
        return VampirismConfig.BALANCE.eaInvisibilityCooldown.get() * 20; // seconds into ticks
    }

    @Override
    public int getDuration(int level) {
        return VampirismConfig.BALANCE.eaInvisibilityDuration.get() * 20;
    }

    @Override
    public int getWeight(CreatureEntity entity) {
        if (entity.getAttackTarget() == null) return 0;
        double distanceToTarget = new Vector3d(entity.getPosX(), entity.getPosY(), entity.getPosZ()).subtract(entity.getAttackTarget().getPosX(), entity.getAttackTarget().getPosY(), entity.getAttackTarget().getPosZ()).length();
        if (distanceToTarget > 4) {
            return 3;
        } else {
            return 1;
        }
    }

    @Override
    public void onUpdate(T entity, int duration) {
        if (!entity.getRepresentingEntity().isInvisible()) {
            entity.getRepresentingEntity().setInvisible(true);
        }
    }

    @Override
    public void updatePreAction(T entity, int duration) {
        if (duration % 5 == 0) {
            ModParticles.spawnParticlesServer(entity.getEntityWorld(), ParticleTypes.EXPLOSION, entity.getPosX(), entity.getPosY(), entity.getPosZ(), 10, 1, 1, 1, 0);
        }
    }
}
