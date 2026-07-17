package de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.core.ModSensors;
import de.teamlapen.vampirism.common.world.entity.ai.activities.BehaviorDescription;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.DraculaState;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class MistFormBehavior {

    public static BehaviorDescription<Dracula> build(float speed) {
        return new BehaviorDescription<>(create(speed), sensors(), memories());
    }

    public static Set<SensorType<? extends Sensor<? super Dracula>>> sensors() {
        return Set.of(ModSensors.NEAREST_TARGETABLE_ENTITIES.get());
    }

    public static Set<MemoryModuleType<?>> memories() {
        return Set.of(MemoryModuleType.WALK_TARGET, ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get());
    }

    public static BehaviorControl<Dracula> create(float speed) {
        return BehaviorBuilder.create(inst -> inst.group(
                inst.registered(MemoryModuleType.WALK_TARGET),
                inst.present(ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get())
        ).apply(inst, (walkTarget, attackable) -> (level, dracula, gameTime) -> {
            if (dracula.getState() != DraculaState.MIST) return false;

            var nearest = inst.get(attackable).findClosest(x -> true);
            if (nearest.isPresent()) {
                Vec3 safePos = DefaultRandomPos.getPosAway(dracula, 16, 7, nearest.get().position());
                if (safePos != null) {
                    walkTarget.set(new WalkTarget(safePos, speed, 0));
                }
            }
            return true;
        }));
    }
}
