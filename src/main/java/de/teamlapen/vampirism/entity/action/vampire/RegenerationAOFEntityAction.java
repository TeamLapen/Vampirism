package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class RegenerationAOFEntityAction<T extends CreatureEntity & IEntityActionUser> extends VampireEntityAction<T> implements ILastingAction<T> {

    public RegenerationAOFEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public int getDuration(int level) {
        return Balance.ea.REGENERATION_DURATION * 20;
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.REGENERATION_COOLDOWN * 20;
    }

    @Override
    public void deactivate(T entity) {
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
    public void activate(T entity) {
    }

    @Override
    public void onUpdate(T entity, int duration) {
        List<MobEntity> entities = entity.getEntityWorld().getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(entity.posX - 4, entity.posY - 1, entity.posZ - 4, entity.posX + 4, entity.posY + 3, entity.posZ + 4));
        for (MobEntity e : entities) {
            if (VampirismAPI.factionRegistry().getFaction(entity) == VampirismAPI.factionRegistry().getFaction(e)) {
                e.heal(entity.getMaxHealth() / 100 * Balance.ea.REGENERATION_AMOUNT / (getDuration(entity.getLevel()) * 20));
                if (duration % 20 == 0) {
                    ModParticles.spawnParticles(entity.getEntityWorld(), ParticleTypes.HEART, e.posX, e.posY, e.posZ, 3, 0.01D, e.getRNG());//TODO test with Vanilla particles before changing to mod particle
                }
            }
        }

    }
}
