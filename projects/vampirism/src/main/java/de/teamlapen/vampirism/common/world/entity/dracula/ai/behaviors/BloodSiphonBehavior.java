package de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.world.entity.ai.activities.actions.ActionBuilder;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import de.teamlapen.vampirism.common.world.entity.dracula.IDraculaAnimations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Raged stage channel: Dracula stands still and drains blood from all players in range, healing himself for more than
 * he drains. The victims are synced via {@link Dracula#SIPHON_TARGETS} so the client can render the siphon beams.
 */
public class BloodSiphonBehavior extends Behavior<Dracula> {

    private static final int DURATION = 4 * 20;
    private static final double RANGE = 12;
    private static final float DRAIN_PER_PULSE = 1.5f;
    private static final float HEAL_FACTOR = 1.5f;
    private static final float HEALTH_GATE = 0.6f;

    public static void configure(ActionBuilder<Dracula> builder) {
        builder.activeMemory(ModMemoryTypes.BLOOD_SIPHON_ACTIVE)
                .cooldown(ModMemoryTypes.BLOOD_SIPHON_COOLDOWN, () -> 30 * 20)
                .add(BloodSiphonBehavior.create(), BloodSiphonBehavior.sensors(), BloodSiphonBehavior.memories())
                .canActivate((level, dracula) -> dracula.getHealth() < dracula.getMaxHealth() * HEALTH_GATE && !findVictims(level, dracula).isEmpty());
    }

    public static Set<MemoryModuleType<?>> memories() {
        return Set.of(ModMemoryTypes.BLOOD_SIPHON_COOLDOWN.get(), ModMemoryTypes.BLOOD_SIPHON_ACTIVE.get(), ModMemoryTypes.ACTION_COOLDOWN.get(), ModMemoryTypes.ACTION_ACTIVE.get());
    }

    public static Set<SensorType<? extends Sensor<? super Dracula>>> sensors() {
        return Set.of();
    }

    public static BloodSiphonBehavior create() {
        return new BloodSiphonBehavior();
    }

    public BloodSiphonBehavior() {
        super(Map.of(
                ModMemoryTypes.BLOOD_SIPHON_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT,
                ModMemoryTypes.BLOOD_SIPHON_ACTIVE.get(), MemoryStatus.VALUE_PRESENT
        ), DURATION);
    }

    private static List<ServerPlayer> findVictims(ServerLevel level, Dracula dracula) {
        return level.getEntitiesOfClass(ServerPlayer.class, dracula.getBoundingBox().inflate(RANGE),
                player -> player.isAlive() && !player.isSpectator() && !player.isCreative());
    }

    @Override
    protected void start(ServerLevel level, Dracula dracula, long gameTime) {
        Brain<Dracula> brain = dracula.getBrain();
        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        dracula.getNavigation().stop();
        dracula.setSiphonTargets(findVictims(level, dracula).stream().map(Entity::getId).toList());
        dracula.triggerAnim(IDraculaAnimations.Animation.BLOOD_SIPHON);
        level.playSound(null, dracula.blockPosition(), ModSounds.DRACULA_SIPHON.get(), SoundSource.HOSTILE, 2.0f, 1.0f);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Dracula dracula, long gameTime) {
        return dracula.getBrain().getMemory(ModMemoryTypes.BLOOD_SIPHON_ACTIVE.get()).isPresent() && !dracula.getSiphonTargets().isEmpty();
    }

    @Override
    protected void tick(ServerLevel level, Dracula dracula, long gameTime) {
        List<ServerPlayer> victims = findVictims(level, dracula);
        dracula.setSiphonTargets(victims.stream().map(Entity::getId).toList());
        if (gameTime % 10 != 0) return;

        float drained = 0;
        for (ServerPlayer victim : victims) {
            if (victim.hurtServer(level, level.damageSources().indirectMagic(dracula, dracula), DRAIN_PER_PULSE)) {
                drained += DRAIN_PER_PULSE;
            }
        }
        if (drained > 0) {
            dracula.heal(drained * HEAL_FACTOR);
        }
    }

    @Override
    protected void stop(ServerLevel level, Dracula dracula, long gameTime) {
        dracula.clearSiphonTargets();
        dracula.triggerAnim(IDraculaAnimations.Animation.NONE);
    }
}
