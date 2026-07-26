package de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.core.ModSensors;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.world.entity.ai.activities.actions.ActionBuilder;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Ranged stage action: Dracula bursts into a swarm of bats and dashes through his target, damaging and blinding
 * everything close to the dash path, reappearing a few blocks behind the target.
 */
public class BatSwarmDashBehavior {

    private static final double DASH_OVERSHOOT = 6;
    private static final double PATH_RADIUS = 1.5;
    private static final float DAMAGE = 8.0f;
    private static final int BLINDNESS_TICKS = 60;

    public static void configure(ActionBuilder<Dracula> builder) {
        builder.activeMemory(ModMemoryTypes.BAT_SWARM_DASH_ACTIVE)
                .cooldown(ModMemoryTypes.BAT_SWARM_DASH_COOLDOWN, () -> 20 * 20)
                .add(create(), sensors(), memories())
                .canActivate((level, dracula) -> findTarget(dracula).isPresent());
    }

    public static Set<SensorType<? extends Sensor<? super Dracula>>> sensors() {
        return Set.of(ModSensors.DRACULA_ATTACKABLE_SENSOR.get());
    }

    public static Set<MemoryModuleType<?>> memories() {
        return Set.of(
                ModMemoryTypes.BAT_SWARM_DASH_COOLDOWN.get(),
                ModMemoryTypes.BAT_SWARM_DASH_ACTIVE.get(),
                ModMemoryTypes.NEAREST_ATTACKABLE.get(),
                ModMemoryTypes.ACTION_COOLDOWN.get(),
                ModMemoryTypes.ACTION_ACTIVE.get()
        );
    }

    private static Optional<LivingEntity> findTarget(Dracula dracula) {
        return dracula.getBrain().getMemory(ModMemoryTypes.NEAREST_ATTACKABLE.get()).orElseGet(List::of).stream()
                .filter(entity -> entity.isAlive() && entity.distanceToSqr(dracula) < 20 * 20)
                .findFirst();
    }

    public static OneShot<Dracula> create() {
        return BehaviorBuilder.create(
                inst -> inst.group(
                        inst.absent(ModMemoryTypes.ACTION_COOLDOWN.get()),
                        inst.present(ModMemoryTypes.ACTION_ACTIVE.get()),
                        inst.absent(ModMemoryTypes.BAT_SWARM_DASH_COOLDOWN.get()),
                        inst.present(ModMemoryTypes.BAT_SWARM_DASH_ACTIVE.get())
                ).apply(inst, (cooldown, active, used, using) ->
                        (level, dracula, gameTime) -> {
                            Optional<LivingEntity> target = findTarget(dracula);
                            if (target.isEmpty()) return false;
                            dash(level, dracula, target.get());
                            return true;
                        })
        );
    }

    private static void dash(ServerLevel level, Dracula dracula, LivingEntity target) {
        Vec3 start = dracula.position();
        Vec3 direction = target.position().subtract(start);
        if (direction.lengthSqr() < 0.01) return;
        Vec3 flatDirection = new Vec3(direction.x, 0, direction.z).normalize();
        Vec3 destination = findDestination(level, dracula, target.position().add(flatDirection.scale(DASH_OVERSHOOT)), target.position());

        level.playSound(null, dracula.blockPosition(), ModSounds.BAT_SWARM.get(), SoundSource.HOSTILE, 2.0f, 1.0f);

        // damage and blind everything close to the dash path
        Vec3 path = destination.subtract(start);
        double pathLength = path.length();
        Vec3 pathDirection = path.normalize();
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, dracula.getBoundingBox().expandTowards(path).inflate(PATH_RADIUS))) {
            if (victim == dracula) continue;
            Vec3 toVictim = victim.position().add(0, victim.getBbHeight() / 2, 0).subtract(start);
            double along = Math.clamp(toVictim.dot(pathDirection), 0, pathLength);
            if (start.add(pathDirection.scale(along)).distanceToSqr(victim.position().add(0, victim.getBbHeight() / 2, 0)) <= PATH_RADIUS * PATH_RADIUS) {
                victim.hurtServer(level, level.damageSources().mobAttack(dracula), DAMAGE);
                victim.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, BLINDNESS_TICKS));
            }
        }

        // bat swarm trail along the path
        int steps = (int) Math.ceil(pathLength * 2);
        for (int i = 0; i <= steps; i++) {
            Vec3 pos = start.add(pathDirection.scale(pathLength * i / steps));
            level.sendParticles(ParticleTypes.LARGE_SMOKE, pos.x, pos.y + 1.0, pos.z, 2, 0.3, 0.4, 0.3, 0.01);
        }

        dracula.getNavigation().stop();
        dracula.teleportTo(destination.x, destination.y, destination.z);
        dracula.lookAt(target, 360, 360);
    }

    /**
     * Steps the overshoot destination down/up to the ground and falls back to the target position if it is not free.
     */
    private static Vec3 findDestination(ServerLevel level, Dracula dracula, Vec3 preferred, Vec3 fallback) {
        BlockPos.MutableBlockPos pos = BlockPos.containing(preferred).mutable();
        for (int i = 0; i < 8 && level.noCollision(dracula, dracula.getBoundingBox().move(Vec3.atBottomCenterOf(pos.below()).subtract(dracula.position()))); i++) {
            pos.move(0, -1, 0);
        }
        for (int i = 0; i < 8 && !level.noCollision(dracula, dracula.getBoundingBox().move(Vec3.atBottomCenterOf(pos).subtract(dracula.position()))); i++) {
            pos.move(0, 1, 0);
        }
        Vec3 destination = Vec3.atBottomCenterOf(pos);
        if (level.noCollision(dracula, dracula.getBoundingBox().move(destination.subtract(dracula.position())))) {
            return destination;
        }
        return fallback;
    }
}
