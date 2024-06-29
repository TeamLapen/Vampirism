package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.skills.IRefinementHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.entity.DarkBloodProjectileEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DarkBloodProjectileAction extends DefaultVampireAction {


    public DarkBloodProjectileAction() {
        super();
    }

    @Override
    public int getCooldown(@NotNull IVampirePlayer player) {
        int cooldown = VampirismConfig.BALANCE.vaDarkBloodProjectileCooldown.get() * 20;
        if (player.getRefinementHandler().isRefinementEquipped(ModRefinements.DARK_BLOOD_PROJECTILE_AOE)) {
            cooldown *= VampirismConfig.BALANCE.vrDarkBloodProjectileAOECooldownMod.get();
        }
        return cooldown;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaDarkBloodProjectileEnabled.get();
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }

    @Override
    protected boolean activate(@NotNull IVampirePlayer player, ActivationContext context) {
        Player shooter = player.asEntity();
        IRefinementHandler<IVampirePlayer> skillHandler = player.getRefinementHandler();

        float directDamage = VampirismConfig.BALANCE.vaDarkBloodProjectileDamage.get().floatValue();
        float indirectDamage = directDamage * 0.5f;
        float speed = 0.95f;
        if (skillHandler.isRefinementEquipped(ModRefinements.DARK_BLOOD_PROJECTILE_DAMAGE)) {
            float modifier = VampirismConfig.BALANCE.vrDarkBloodProjectileDamageMod.get().floatValue();
            directDamage *= modifier;
            indirectDamage *= modifier;
        }
        if (skillHandler.isRefinementEquipped(ModRefinements.DARK_BLOOD_PROJECTILE_SPEED)) {
            speed = 1.4f;
        }

        if (skillHandler.isRefinementEquipped(ModRefinements.DARK_BLOOD_PROJECTILE_AOE)) {
            for (int i = 0; i < 32; i++) {
                Vec3 vec3d = getRotationVector(shooter.getViewXRot(1.0f), shooter.getViewYRot(1.0f) + i * 11.25f);
                DarkBloodProjectileEntity entity = createProjectile(shooter, shooter.position(), 0, vec3d, false, 0, 0, 0.95f);
                entity.setMaxTicks(7);
                entity.excludeShooter();
                if (i == 0) {
                    entity.setDamage(0, directDamage);
                    entity.explode(VampirismConfig.BALANCE.vrDarkBloodProjectileAOERange.get(), null);
                }
            }
        } else {
            boolean goThrough = skillHandler.isRefinementEquipped(ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION);
            createProjectile(shooter, shooter.position(), shooter.getEyeHeight() * 0.9f, shooter.getViewVector(1.0F), goThrough, directDamage, indirectDamage, speed);
            if (skillHandler.isRefinementEquipped(ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT)) {
                createProjectile(shooter, shooter.position(), shooter.getEyeHeight() * 0.9f, getVectorForRotation(shooter.getViewXRot(1.0f), shooter.getViewYRot(1.0f) + 30f), goThrough, directDamage, indirectDamage, speed);
                createProjectile(shooter, shooter.position(), shooter.getEyeHeight() * 0.9f, getVectorForRotation(shooter.getViewXRot(1.0f), shooter.getViewYRot(1.0f) - 30f), goThrough, directDamage, indirectDamage, speed);
            }
        }
        return true;
    }

    private @NotNull DarkBloodProjectileEntity createProjectile(@NotNull Player shooter, @NotNull Vec3 position, double height, @NotNull Vec3 direction, boolean goThrough, float directDamage, float indirectDamage, float speed) {
        DarkBloodProjectileEntity entity = new DarkBloodProjectileEntity(shooter.getCommandSenderWorld(), position.x + direction.x, position.y + height, position.z + direction.z, direction);
        entity.setMotionFactor(speed);
        entity.setOwner(shooter);
        entity.setDamage(directDamage, indirectDamage);
        if (goThrough) {
            entity.setGothrough(true);
        }
        shooter.getCommandSenderWorld().addFreshEntity(entity);
        return entity;
    }

    private @NotNull Vec3 getRotationVector(float pitch, float yaw) {
        float f = pitch * ((float) Math.PI / 180F);
        float f1 = -yaw * ((float) Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        return new Vec3(f3 * f4, 0, f2 * f4);
    }

    private @NotNull Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = pitch * ((float) Math.PI / 180F);
        float f1 = -yaw * ((float) Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }
}
