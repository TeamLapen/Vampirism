package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.DarkBloodProjectileEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public class DarkProjectileEntityAction<T extends CreatureEntity & IEntityActionUser> extends VampireEntityAction<T> implements IInstantAction<T> {

    public DarkProjectileEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public boolean activate(T entity) {
        LivingEntity shooter = entity.getRepresentingEntity();

        Vector3d vec3dd = entity.getTarget() != null ? new Vector3d(entity.getTarget().getX() - entity.getX(), entity.getTarget().getY() - entity.getY(), entity.getTarget().getZ() - entity.getZ()) : Vector3d.ZERO;
        vec3dd.normalize();

        DarkBloodProjectileEntity projectile = new DarkBloodProjectileEntity(shooter.getCommandSenderWorld(), shooter.getX() + vec3dd.x * 1.0f, shooter.getY() + shooter.getEyeHeight() * 0.9f, shooter.getZ() + vec3dd.z * 1.0f, vec3dd.x, vec3dd.y, vec3dd.z);
        projectile.setOwner(shooter);
        projectile.setDamage(VampirismConfig.BALANCE.eaDarkProjectileDamage.get().floatValue(), VampirismConfig.BALANCE.eaDarkProjectileIndirectDamage.get().floatValue());

        shooter.getCommandSenderWorld().addFreshEntity(projectile);
        return true;
    }

    @Override
    public int getCooldown(int level) {
        return VampirismConfig.BALANCE.eaDarkProjectileCooldown.get() * 20;
    }

    @Override
    public int getWeight(CreatureEntity entity) {
        double distanceToTarget = new Vector3d(entity.getX(), entity.getY(), entity.getZ()).subtract(entity.getTarget().getX(), entity.getTarget().getY(), entity.getTarget().getZ()).length();
        if (distanceToTarget > 20) {
            return 3;
        } else if (distanceToTarget > 12) {
            return 2;
        } else {
            return 1;
        }
    }
}