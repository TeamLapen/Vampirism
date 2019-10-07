package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class SpeedEntityAction<T extends CreatureEntity & IEntityActionUser> extends VampireEntityAction<T> implements ILastingAction<T> {
    public static final UUID UUIDS = UUID.fromString("2b49cf70-b634-4e85-8c3e-0147919eaf54");

    public SpeedEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public void activate(T entity) {
    }

    @Override
    public void deactivate(T entity) {
        entity.getRepresentingEntity().getAttributes().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(UUIDS);
    }

    @Override
    public int getCooldown(int level) {
        return VampirismConfig.BALANCE.eaSpeedCooldown.get() * 20;
    }

    @Override
    public int getDuration(int level) {
        return VampirismConfig.BALANCE.eaSpeedDuration.get() * 20;
    }

    @Override
    public int getWeight(CreatureEntity entity) {
        double distanceToTarget = new Vec3d(entity.posX, entity.posY, entity.posZ).subtract(entity.getAttackTarget().posX, entity.getAttackTarget().posY, entity.getAttackTarget().posZ).length();
        if (distanceToTarget > 10) {
            return 3;
        } else if (distanceToTarget > 5) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public void onUpdate(T entity, int duration) {
        if (entity.getRepresentingEntity().getAttributes().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(UUIDS) == null) {
            entity.getRepresentingEntity().getAttributes().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier(UUIDS, "speedaction", VampirismConfig.BALANCE.eaSpeedAmount.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        if (duration % 5 == 0) {
            double maxDist = 0.5D;
            ModParticles.spawnParticlesServer(entity.getEntityWorld(), ParticleTypes.CLOUD, entity.posX + (entity.getRNG().nextDouble() * maxDist) - maxDist / 2, entity.posY + 0.1, entity.posZ + (entity.getRNG().nextDouble() * maxDist) - maxDist / 2, 3, 0.3f, 0.3f, 0.3f, 0.02f);
        }
    }
}
