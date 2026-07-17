package de.teamlapen.vampirism.common.world.entity.dracula.ai.activities;

import de.teamlapen.vampirism.common.core.ModSensors;
import de.teamlapen.vampirism.common.world.entity.ai.activities.ActivityBuilder;
import de.teamlapen.vampirism.common.world.entity.ai.activities.BehaviorDescription;
import de.teamlapen.vampirism.common.world.entity.ai.system.AiActivityProvider;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Set;

public class DraculaCoreActivityProvider extends AiActivityProvider<Dracula> {

    public DraculaCoreActivityProvider() {
        super(Activity.CORE);
    }

    @Override
    protected void createActivity(ActivityBuilder<Dracula> builder) {
        builder.startPriority(0)
                .add(new Swim<>(0.8f))
                .add(look())
                .add(moveToTarget())
                .add(ModSensors.DRACULA_STAGE_SENSOR.get());
    }

    private BehaviorDescription<Dracula> look() {
        return new BehaviorDescription<>(new LookAtTargetSink(45, 90), Set.of(), Set.of(MemoryModuleType.LOOK_TARGET));
    }

    private BehaviorDescription<Dracula> moveToTarget() {
        return new BehaviorDescription<>(new MoveToTargetSink(), Set.of(
                SensorType.NEAREST_LIVING_ENTITIES,
                SensorType.HURT_BY
        ), Set.of(
                MemoryModuleType.LOOK_TARGET,
                MemoryModuleType.WALK_TARGET,
                MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                MemoryModuleType.PATH,
                MemoryModuleType.ATTACK_TARGET,
                MemoryModuleType.HURT_BY,
                MemoryModuleType.HURT_BY_ENTITY));
    }

}
