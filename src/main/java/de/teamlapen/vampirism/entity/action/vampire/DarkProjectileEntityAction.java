package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.EntityDarkBloodProjectile;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

public class DarkProjectileEntityAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends VampireEntityAction implements IInstantAction<T> {

    public DarkProjectileEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.DARK_PROJECTILE_COOLDOWN * 20;
    }

    @Override
    public boolean activate(T entity) {
        EntityLivingBase shooter = entity.getRepresentingEntity();

        Vec3d vec3dd = new Vec3d(entity.getAttackTarget().posX - entity.posX, entity.getAttackTarget().posY - entity.posY, entity.getAttackTarget().posZ - entity.posZ);
        vec3dd.normalize();

        EntityDarkBloodProjectile projectile = new EntityDarkBloodProjectile(shooter.getEntityWorld(), shooter.posX + vec3dd.x * 1.0f, shooter.posY + shooter.getEyeHeight() * 0.9f, shooter.posZ + vec3dd.z * 1.0f, vec3dd.x, vec3dd.y, vec3dd.z);
        projectile.shootingEntity = shooter;
        projectile.setDamage((float) Balance.ea.DARK_BLOOD_PROJECTILE_DAMAGE, (float) Balance.ea.DARK_BLOOD_PROJECTILE_INDIRECT_DAMAGE);

        shooter.getEntityWorld().spawnEntity(projectile);
        return true;
    }

    @Override
    public void updatePreAction(T entity, int duration) {
    }

}