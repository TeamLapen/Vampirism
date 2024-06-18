package de.teamlapen.vampirism.entity.vampire.action;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.DarkBloodProjectileEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DarkProjectileEntityAction<T extends PathfinderMob & IEntityActionUser> extends VampireEntityAction<T> implements IInstantAction<T> {

    public DarkProjectileEntityAction(@NotNull EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public boolean activate(@NotNull T entity) {
        LivingEntity shooter = entity.asEntity();

        Vec3 vec3dd = entity.getTarget() != null ? new Vec3(entity.getTarget().getX() - entity.getX(), entity.getTarget().getY() - entity.getY(), entity.getTarget().getZ() - entity.getZ()) : Vec3.ZERO;
        vec3dd.normalize();

        DarkBloodProjectileEntity projectile = new DarkBloodProjectileEntity(shooter.getCommandSenderWorld(), shooter.getX() + vec3dd.x * 1.0f, shooter.getY() + shooter.getEyeHeight() * 0.9f, shooter.getZ() + vec3dd.z * 1.0f, vec3dd);
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
    public int getWeight(@NotNull PathfinderMob entity) {
        double distanceToTarget = new Vec3(entity.getX(), entity.getY(), entity.getZ()).subtract(entity.getTarget().getX(), entity.getTarget().getY(), entity.getTarget().getZ()).length();
        if (distanceToTarget > 20) {
            return 3;
        } else if (distanceToTarget > 12) {
            return 2;
        } else {
            return 1;
        }
    }
}