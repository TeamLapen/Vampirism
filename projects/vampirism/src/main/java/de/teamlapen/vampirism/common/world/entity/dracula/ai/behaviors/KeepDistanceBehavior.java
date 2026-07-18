package de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.core.ModSensors;
import de.teamlapen.vampirism.common.world.entity.ai.activities.BehaviorDescription;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class KeepDistanceBehavior {

    public static BehaviorDescription<Dracula> build(float speed, int minDistance) {
        return new BehaviorDescription<>(create(speed, minDistance), sensors(), memories());
    }

    public static Set<SensorType<? extends Sensor<? super Dracula>>> sensors() {
        return Set.of(ModSensors.NEAREST_TARGETABLE_ENTITIES.get());
    }

    public static Set<MemoryModuleType<?>> memories() {
        return Set.of(ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get(), MemoryModuleType.WALK_TARGET);
    }

    public static BehaviorControl<Dracula> create(float speed, int minDistance) {
        int minDistanceSq = minDistance * minDistance;
        return BehaviorBuilder.create(inst -> inst.group(
                inst.present(ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get()),
                inst.registered(MemoryModuleType.WALK_TARGET)
        ).apply(inst, (target, walkTarget) -> (level, dracula, gameTime) -> {
            var targetEntity = inst.get(target);
            var nearbyEntity = targetEntity.findClosest(x -> x.distanceToSqr(dracula) < minDistanceSq);

            if (nearbyEntity.isPresent()) {
                Vec3 away = dracula.position().subtract(nearbyEntity.get().position()).normalize().scale(minDistance);
                walkTarget.set(new WalkTarget(dracula.position().add(away), speed, 0));
                return true;
            }
            return false;
        }));
    }
}
