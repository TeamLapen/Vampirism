package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.Vec3d;

public class InvisibleEntityAction<T extends CreatureEntity & IEntityActionUser> extends VampireEntityAction<T> implements ILastingAction<T> {

    public InvisibleEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.INVISIBLE_COOLDOWN * 20; // seconds into ticks
    }

    @Override
    public int getDuration(int level) {
        return Balance.ea.INVISIBLE_DURATION * 20;
    }

    @Override
    public void deactivate(T entity) {
        entity.getRepresentingEntity().setInvisible(false);

    }

    @Override
    public void onUpdate(T entity, int duration) {
        if (!entity.getRepresentingEntity().isInvisible()) {
            entity.getRepresentingEntity().setInvisible(true);
        }
    }

    @Override
    public void activate(T entity) {
        ModParticles.spawnParticles(entity.getEntityWorld(), ParticleTypes.EXPLOSION, entity.posX, entity.posY, entity.posZ, 60, 1, entity.getRNG());

    }

    @Override
    public void updatePreAction(T entity, int duration) {
        if (duration % 5 == 0) {
            ModParticles.spawnParticles(entity.getEntityWorld(), ParticleTypes.EXPLOSION, entity.posX, entity.posY, entity.posZ, 10, 1, entity.getRNG());
        }
    }

    @Override
    public int getWeight(CreatureEntity entity) {
        double distanceToTarget = new Vec3d(entity.posX, entity.posY, entity.posZ).subtract(entity.getAttackTarget().posX, entity.getAttackTarget().posY, entity.getAttackTarget().posZ).length();
        if (distanceToTarget > 4) {
            return 3;
        } else {
            return 1;
        }
    }
}
