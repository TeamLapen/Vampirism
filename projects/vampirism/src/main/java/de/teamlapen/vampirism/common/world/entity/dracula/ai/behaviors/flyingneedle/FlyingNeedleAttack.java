package de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors.flyingneedle;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.core.ModSensors;
import de.teamlapen.vampirism.common.world.entity.ai.activities.actions.ActionBuilder;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import de.teamlapen.vampirism.common.world.entity.dracula.FlyingNeedleEntity;
import de.teamlapen.vampirism.common.world.entity.dracula.IDraculaAnimations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.*;

public class FlyingNeedleAttack extends Behavior<Dracula> {

    public static void configure(ActionBuilder<Dracula> builder) {
        builder.activeMemory(ModMemoryTypes.FLYING_NEEDLE_ACTIVE)
                .cooldown(ModMemoryTypes.FLYING_NEEDLE_COOLDOWN, () -> 30 * 20)
                .add(FlyingNeedleAttack.create(), FlyingNeedleAttack.sensors(), FlyingNeedleAttack.memories());
    }
    private enum Phase {
        CHARGING,
        FIRING
    }

    private Phase phase = Phase.CHARGING;
    private int ticks = 0;
    private final List<FlyingNeedleEntity> needles = new ArrayList<>();
    private final List<LivingEntity> targets = new ArrayList<>();
    private static final int MAX_NEEDLES = 6;

    public static Set<SensorType<? extends Sensor<? super Dracula>>> sensors() {
        return Set.of(SensorType.NEAREST_LIVING_ENTITIES, ModSensors.DRACULA_ATTACKABLE_SENSOR.get());
    }

    public static Set<MemoryModuleType<?>> memories() {
        return Set.of(
                ModMemoryTypes.FLYING_NEEDLE_COOLDOWN.get(),
                ModMemoryTypes.FLYING_NEEDLE_ACTIVE.get(),
                ModMemoryTypes.NEAREST_ATTACKABLE.get(),
                ModMemoryTypes.FLYING_NEEDLES.get()
        );
    }

    public static FlyingNeedleAttack create() {
        return new FlyingNeedleAttack();
    }
    public FlyingNeedleAttack() {
        super(Map.of(
                ModMemoryTypes.FLYING_NEEDLE_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT,
                ModMemoryTypes.FLYING_NEEDLE_ACTIVE.get(), MemoryStatus.VALUE_PRESENT,
                ModMemoryTypes.NEAREST_ATTACKABLE.get(), MemoryStatus.VALUE_PRESENT,
                ModMemoryTypes.FLYING_NEEDLES.get(), MemoryStatus.REGISTERED
        ), 400);
    }



    @Override
    protected void start(ServerLevel level, Dracula entity, long gameTime) {
        this.phase = Phase.CHARGING;
        this.ticks = 0;
        this.needles.clear();
        this.targets.clear();
        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    @Override
    protected void tick(ServerLevel level, Dracula entity, long gameTime) {
        this.ticks++;
        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);

        if (this.phase == Phase.CHARGING) {
            if (this.ticks % 10 == 0 && needles.size() < MAX_NEEDLES) {
                FlyingNeedleEntity needle = new FlyingNeedleEntity(level, entity, 50.0f, this.needles.size(), MAX_NEEDLES);
                level.addFreshEntity(needle);
                this.needles.add(needle);
                entity.getBrain().setMemory(ModMemoryTypes.FLYING_NEEDLES.get(), this.needles.stream().map(Entity::getUUID).toList());
            }

            if (this.needles.size() == MAX_NEEDLES && ticks >= 80) { // Give some time for charging
                this.targets.addAll(entity.getBrain().getMemory(ModMemoryTypes.NEAREST_ATTACKABLE.get()).orElseGet(List::of).stream().filter(x -> x.distanceToSqr(entity) < 20 * 20).toList());

                if (this.targets.isEmpty()) {
                    doStop(level, entity, gameTime);
                } else {
                    this.phase = Phase.FIRING;
                    this.ticks = 0;
                }
            }
        } else if (this.phase == Phase.FIRING) {
            if (this.ticks % 10 == 0) {
                this.needles.removeIf(n -> !n.isAlive() || n.isFlying());
                if (this.needles.isEmpty()) {
                    doStop(level, entity, gameTime);
                    return;
                }

                if (this.targets.isEmpty()) {
                    doStop(level, entity, gameTime);
                    return;
                }

                FlyingNeedleEntity needle = this.needles.getFirst();
                this.needles.remove(needle);
                LivingEntity target = this.targets.get(entity.getRandom().nextInt(this.targets.size()));
                BehaviorUtils.lookAtEntity(entity, target);
                needle.shoot(target);
                entity.triggerAnim(IDraculaAnimations.Animation.NEEDLE_1, IDraculaAnimations.Animation.NEEDLE_2);
                this.ticks = 0; // Reset ticks to wait 5 for next firing
            }
        }
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Dracula entity, long gameTime) {
        return entity.getBrain().hasMemoryValue(ModMemoryTypes.FLYING_NEEDLE_ACTIVE.get());
    }

    @Override
    protected void stop(ServerLevel level, Dracula entity, long gameTime) {
        Brain<Dracula> brain = entity.getBrain();
        var uuids = brain.getMemory(ModMemoryTypes.FLYING_NEEDLES.get()).stream().flatMap(Collection::stream).map(level::getEntity).toList();
        for (Entity uuid : uuids) {
            if (uuid instanceof FlyingNeedleEntity needle && needle.isAlive() && !needle.isFlying()) {
                needle.discard();
            }
        }
        brain.eraseMemory(ModMemoryTypes.FLYING_NEEDLES.get());
    }
}
