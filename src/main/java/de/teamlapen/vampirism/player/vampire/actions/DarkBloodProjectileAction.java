package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.DarkBloodProjectileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class DarkBloodProjectileAction extends DefaultVampireAction {


    public DarkBloodProjectileAction() {
        super();
    }

    @Override
    public int getCooldown() {
        return Balance.vpa.DARK_BLOOD_PROJECTILE_COOLDOWN * 20;
    }

    @Override
    public boolean isEnabled() {
        return Balance.vpa.DARK_BLOOD_PROJECTILE_ENABLE;
    }

    @Override
    protected boolean activate(IVampirePlayer player) {
        PlayerEntity shooter = player.getRepresentingPlayer();

        Vec3d vec3d = shooter.getLook(1.0F);

        DarkBloodProjectileEntity projectile = new DarkBloodProjectileEntity(shooter.getEntityWorld(), shooter.posX + vec3d.x * 1.0f, shooter.posY + shooter.getEyeHeight() * 0.9f, shooter.posZ + vec3d.z * 1.0f, vec3d.x, vec3d.y, vec3d.z);
        projectile.shootingEntity = shooter;
        projectile.setDamage((float) Balance.vpa.DARK_BLOOD_PROJECTILE_DAMAGE, (float) Balance.vpa.DARK_BLOOD_PROJECTILE_INDIRECT_DAMAGE);

        shooter.getEntityWorld().addEntity(projectile);
        return true;
    }
}
