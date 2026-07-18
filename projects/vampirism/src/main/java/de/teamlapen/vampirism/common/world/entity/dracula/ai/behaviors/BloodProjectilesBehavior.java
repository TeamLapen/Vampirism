package de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.world.entity.ai.activities.actions.ActionBuilder;
import de.teamlapen.vampirism.common.world.entity.dracula.BloodProjectileEntity;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BloodProjectilesBehavior extends Behavior<Dracula> {

    public static void configure(ActionBuilder<Dracula> builder) {
        builder.activeMemory(ModMemoryTypes.BLOOD_PROJECTILES_ACTIVE)
                .cooldown(ModMemoryTypes.BLOOD_PROJECTILES_COOLDOWN, () -> 30 * 20)
                .add(BloodProjectilesBehavior.create(), Set.of(), Set.of(ModMemoryTypes.BLOOD_PROJECTILES_ACTIVE.get(), ModMemoryTypes.BLOOD_PROJECTILES_COOLDOWN.get()))
                .canActivate((level, dracula) -> {
                    float healthPercent = dracula.getHealth() / dracula.getMaxHealth();
                    return healthPercent >= 0.4f && healthPercent <= 0.8f;
                });
    }

    private int ticks = 0;
    private final List<BloodProjectileEntity> spawnedProjectiles = new ArrayList<>();

    public static BloodProjectilesBehavior create() {
        return new BloodProjectilesBehavior();
    }

    public BloodProjectilesBehavior() {
        super(Map.of(
                ModMemoryTypes.BLOOD_PROJECTILES_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT,
                ModMemoryTypes.BLOOD_PROJECTILES_ACTIVE.get(), MemoryStatus.VALUE_PRESENT
        ), 120);
    }

    @Override
    protected void start(ServerLevel level, Dracula entity, long gameTime) {
        this.ticks = 0;
        this.spawnedProjectiles.clear();
        entity.hurtServer(level, level.damageSources().magic(), entity.getMaxHealth() * 0.05f);
        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    @Override
    protected void tick(ServerLevel level, Dracula entity, long gameTime) {
        this.ticks++;
        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        entity.getNavigation().stop();

        if (ticks <= 100 && ticks % 10 == 0) {
            double angle = (ticks / 10.0) * (Math.PI * 2);
            double x = entity.getX() + Math.cos(angle) * 2;
            double z = entity.getZ() + Math.sin(angle) * 2;
            double y = entity.getY() + 1.5;

            BloodProjectileEntity projectile = new BloodProjectileEntity(level, entity, Vec3.ZERO);
            projectile.setPos(x, y, z);
            projectile.setNoGravity(true);
            projectile.setMaxTicks(200);
            level.addFreshEntity(projectile);
            spawnedProjectiles.add(projectile);
        }

        if (ticks == 100) {
            for (BloodProjectileEntity projectile : spawnedProjectiles) {
                if (projectile.isAlive()) {
                    Vec3 direction = projectile.position().subtract(entity.position().add(0, 1.5, 0)).normalize();
                    projectile.setDeltaMovement(direction.scale(0.5));
                    projectile.setMotionFactor(1.0f);
                }
            }
        }
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Dracula entity, long gameTime) {
        return entity.getBrain().hasMemoryValue(ModMemoryTypes.BLOOD_PROJECTILES_ACTIVE.get());
    }

    @Override
    protected void stop(ServerLevel level, Dracula entity, long gameTime) {
        this.spawnedProjectiles.clear();
    }
}
