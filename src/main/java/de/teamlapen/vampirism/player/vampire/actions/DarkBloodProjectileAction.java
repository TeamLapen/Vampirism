package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.entity.DarkBloodProjectileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class DarkBloodProjectileAction extends DefaultVampireAction {


    public DarkBloodProjectileAction() {
        super();
    }

    @Override
    public int getCooldown() {
        return VampirismConfig.BALANCE.vaDarkBloodProjectileCooldown.get() * 20;
    }

    @Override
    public int getCooldown(IFactionPlayer player) {
        int cooldown = VampirismConfig.BALANCE.vaDarkBloodProjectileCooldown.get() * 20;
        if (player.getSkillHandler().isRefinementEquipped(ModRefinements.DARK_BLOOD_PROJECTILE_AOE.get())) {
            cooldown *= VampirismConfig.BALANCE.vrDarkBloodProjectileAOECooldownMod.get();
        }
        return cooldown;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaDarkBloodProjectileEnabled.get();
    }

    @Override
    public boolean showHudCooldown(PlayerEntity player) {
        return true;
    }

    @Override
    protected boolean activate(IVampirePlayer player) {
        PlayerEntity shooter = player.getRepresentingPlayer();

        float directDamage = VampirismConfig.BALANCE.vaDarkBloodProjectileDamage.get().floatValue();
        float indirectDamage = directDamage * 0.5f;
        float speed = 0.95f;
        if (player.getSkillHandler().isRefinementEquipped(ModRefinements.DARK_BLOOD_PROJECTILE_DAMAGE.get())) {
            float modifier = VampirismConfig.BALANCE.vrDarkBloodProjectileDamageMod.get().floatValue();
            directDamage *= modifier;
            indirectDamage *= modifier;
        }
        if (player.getSkillHandler().isRefinementEquipped(ModRefinements.DARK_BLOOD_PROJECTILE_SPEED.get())) {
            speed = 1.4f;
        }

        if (player.getSkillHandler().isRefinementEquipped(ModRefinements.DARK_BLOOD_PROJECTILE_AOE.get())) {
            for (int i = 0; i < 32; i++) {
                Vector3d vec3d = getRotationVector(shooter.getViewXRot(1.0f), shooter.getViewYRot(1.0f) + i * 11.25f);
                DarkBloodProjectileEntity entity = createProjectile(shooter, shooter.position(), 0, vec3d, false, 0, 0, 0.95f);
                entity.setMaxTicks(7);
                entity.excludeShooter();
                if (i == 0) {
                    entity.setDamage(0, directDamage);
                    entity.explode(VampirismConfig.BALANCE.vrDarkBloodProjectileAOERange.get(), null);
                }
            }
        } else {
            boolean goThrough = player.getSkillHandler().isRefinementEquipped(ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION.get());
            createProjectile(shooter, shooter.position(), shooter.getEyeHeight() * 0.9f, shooter.getViewVector(1.0F), goThrough, directDamage, indirectDamage, speed);
            if (player.getSkillHandler().isRefinementEquipped(ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT.get())) {
                createProjectile(shooter, shooter.position(), shooter.getEyeHeight() * 0.9f, getVectorForRotation(shooter.getViewXRot(1.0f), shooter.getViewYRot(1.0f) + 30f), goThrough, directDamage, indirectDamage, speed);
                createProjectile(shooter, shooter.position(), shooter.getEyeHeight() * 0.9f, getVectorForRotation(shooter.getViewXRot(1.0f), shooter.getViewYRot(1.0f) - 30f), goThrough, directDamage, indirectDamage, speed);
            }
        }
        return true;
    }

    private DarkBloodProjectileEntity createProjectile(PlayerEntity shooter, Vector3d position, double height, Vector3d direction, boolean goThrough, float directDamage, float indirectDamage, float speed) {
        DarkBloodProjectileEntity entity = new DarkBloodProjectileEntity(shooter.getCommandSenderWorld(), position.x + direction.x, position.y + height, position.z + direction.z, direction.x, direction.y, direction.z);
        entity.setMotionFactor(speed);
        entity.setOwner(shooter);
        entity.setDamage(directDamage, indirectDamage);
        if (goThrough) {
            entity.setGothrough(true);
        }
        shooter.getCommandSenderWorld().addFreshEntity(entity);
        return entity;
    }

    private Vector3d getRotationVector(float pitch, float yaw) {
        float f = pitch * ((float) Math.PI / 180F);
        float f1 = -yaw * ((float) Math.PI / 180F);
        float f2 = MathHelper.cos(f1);
        float f3 = MathHelper.sin(f1);
        float f4 = MathHelper.cos(f);
        return new Vector3d(f3 * f4, 0, f2 * f4);
    }

    private Vector3d getVectorForRotation(float pitch, float yaw) {
        float f = pitch * ((float) Math.PI / 180F);
        float f1 = -yaw * ((float) Math.PI / 180F);
        float f2 = MathHelper.cos(f1);
        float f3 = MathHelper.sin(f1);
        float f4 = MathHelper.cos(f);
        float f5 = MathHelper.sin(f);
        return new Vector3d(f3 * f4, -f5, f2 * f4);
    }
}
