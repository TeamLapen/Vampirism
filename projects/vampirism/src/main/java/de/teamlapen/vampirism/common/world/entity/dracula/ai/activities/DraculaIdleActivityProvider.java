package de.teamlapen.vampirism.common.world.entity.dracula.ai.activities;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.common.world.entity.ai.activities.ActivityBuilder;
import de.teamlapen.vampirism.common.world.entity.ai.activities.BehaviorDescription;
import de.teamlapen.vampirism.common.world.entity.ai.system.AiActivityProvider;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Set;

public class DraculaIdleActivityProvider extends AiActivityProvider<Dracula> {

    public DraculaIdleActivityProvider() {
        super(Activity.IDLE);
    }

    @Override
    protected void createActivity(ActivityBuilder<Dracula> builder) {
        builder.startPriority(0)
                .add(new RunOne<>(
                        ImmutableList.of(
                                Pair.of(createIdleLookBehaviors(), 2),
                                Pair.of(createIdleMovementBehaviors(0.2f), 1)
                        )), Sets.union(movementSensors(), lookSensors()), Sets.union(movementMemories(), lookMemories()));
    }

    public static BehaviorDescription<Dracula> buildMovement(float speed) {
        return new BehaviorDescription<>(createIdleMovementBehaviors(speed), movementSensors(), movementMemories());
    }

    public static BehaviorDescription<Dracula> buildLook() {
        return new BehaviorDescription<>(createIdleLookBehaviors(), lookSensors(), lookMemories());
    }

    public static RunOne<Dracula> createIdleMovementBehaviors(float speed) {
        return new RunOne<>(
                ImmutableList.of(
                        Pair.of(RandomStroll.stroll(speed), 1),
                        Pair.of(new DoNothing(10, 30), 1))
        );
    }

    public static RunOne<Dracula> createIdleLookBehaviors() {
        return new RunOne<>(
                ImmutableList.of(
                        Pair.of(SetEntityLookTarget.create(8.0F), 1),
                        Pair.of(new DoNothing(10, 30), 1)
                )
        );
    }

    public static Set<SensorType<? extends Sensor<? super Dracula>>> movementSensors() {
        return Set.of();
    }

    public static Set<MemoryModuleType<?>> movementMemories() {
        return Set.of( MemoryModuleType.WALK_TARGET);
    }

    public static Set<SensorType<? extends Sensor<? super Dracula>>> lookSensors() {
        return Set.of(SensorType.NEAREST_LIVING_ENTITIES);
    }

    public static Set<MemoryModuleType<?>> lookMemories() {
        return Set.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }
}
