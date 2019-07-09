package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class SpeedEntityAction<T extends CreatureEntity & IEntityActionUser> extends VampireEntityAction<T> implements ILastingAction<T> {
    public static final UUID UUIDS = UUID.fromString("2b49cf70-b634-4e85-8c3e-0147919eaf54");

    public SpeedEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public int getDuration(int level) {
        return Balance.ea.SPEED_DURATION * 20;
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.SPEED_COOLDOWN * 20;
    }

    @Override
    public void deactivate(T entity) {
        entity.getRepresentingEntity().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(UUIDS);
    }

    @Override
    public void onUpdate(T entity, int duration) {
        if (!entity.getRepresentingEntity().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(new AttributeModifier(UUIDS, "speedaction", Balance.ea.SPEED_AMOUNT, 2))) {
            entity.getRepresentingEntity().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier(UUIDS, "speedaction", Balance.ea.SPEED_AMOUNT, 2));
        }
        if (duration % 5 == 0) {
            VampLib.proxy.getParticleHandler().spawnParticles(entity.getEntityWorld(), ModParticles.CLOUD, entity.posX, entity.posY, entity.posZ, 5, 0.5, entity.getRNG(), -entity.motionX, 0.0D, -entity.motionZ);
        }
    }

    @Override
    public void activate(T entity) {
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
}
