package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.DarkBloodProjectileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class DarkBloodProjectileAction extends DefaultVampireAction {


    public DarkBloodProjectileAction() {
        super();
    }

    @Override
    public int getCooldown() {
        return VampirismConfig.BALANCE.vaDarkBloodCooldown.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaDarkBloodEnabled.get();
    }

    @Override
    protected boolean activate(IVampirePlayer player) {
        PlayerEntity shooter = player.getRepresentingPlayer();

        Vec3d vec3d = shooter.getLook(1.0F);

        DarkBloodProjectileEntity projectile = new DarkBloodProjectileEntity(shooter.getEntityWorld(), shooter.getPosX() + vec3d.x * 1.0f, shooter.getPosY() + shooter.getEyeHeight() * 0.9f, shooter.getPosZ() + vec3d.z * 1.0f, vec3d.x, vec3d.y, vec3d.z);
        projectile.shootingEntity = shooter;
        projectile.setDamage(VampirismConfig.BALANCE.vaDarkBloodDamage.get().floatValue(), VampirismConfig.BALANCE.vaDarkBloodDamage.get().floatValue() * 0.5f);

        shooter.getEntityWorld().addEntity(projectile);
        return true;
    }
}
