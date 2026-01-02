package de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors;

import de.teamlapen.vampirism.common.world.entity.ai.activities.BehaviorDescription;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.DraculaState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class MistFormBehavior {

    public static BehaviorDescription<Dracula> build(float speed) {
        return new BehaviorDescription<>(create(speed), sensors(), memories());
    }

    public static Set<SensorType<? extends Sensor<? super Dracula>>> sensors() {
        return Set.of();
    }

    public static Set<MemoryModuleType<?>> memories() {
        return Set.of(MemoryModuleType.WALK_TARGET, MemoryModuleType.ATTACK_TARGET);
    }

    public static BehaviorControl<Dracula> create(float speed) {
        return BehaviorBuilder.create(inst -> inst.group(
                inst.registered(MemoryModuleType.WALK_TARGET),
                inst.registered(MemoryModuleType.ATTACK_TARGET)
        ).apply(inst, (walkTarget, attackTarget) -> (level, dracula, gameTime) -> {
            if (dracula.getState() != DraculaState.MIST) return false;

            LivingEntity target = inst.tryGet(attackTarget).orElse(null);
            if (target != null) {
                Vec3 away = dracula.position().subtract(target.position()).normalize().scale(15);
                walkTarget.set(new WalkTarget(dracula.position().add(away), speed, 0));
                return true;
            }
            return false;
        }));
    }
}
