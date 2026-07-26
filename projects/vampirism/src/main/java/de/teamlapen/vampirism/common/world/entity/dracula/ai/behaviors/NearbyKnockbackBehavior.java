package de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.core.ModSensors;
import de.teamlapen.vampirism.common.world.entity.ai.activities.BehaviorDescription;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NearbyKnockbackBehavior {


    public static BehaviorDescription<Dracula> build(double radius, double strength) {
        return new BehaviorDescription<>(create(radius, strength), sensors(), memories());
    }

    public static Set<SensorType<? extends Sensor<? super Dracula>>> sensors() {
        return Set.of(ModSensors.DRACULA_ATTACKABLE_SENSOR.get());
    }

    public static Set<MemoryModuleType<?>> memories() {
        return Set.of(ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get(), ModMemoryTypes.KNOCKED_BACK.get());
    }


    public static BehaviorControl<Dracula> create(double radius, double strength) {
        return BehaviorBuilder.create(inst -> inst.group(
                inst.present(ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get()),
                inst.absent(ModMemoryTypes.KNOCKED_BACK.get())
        ).apply(inst, (attackable, knocked) -> (level, dracula, gameTime) -> {
            List<LivingEntity> nearby = new ArrayList<>();
            inst.get(attackable).findAll(x -> x.distanceToSqr(dracula) < radius * radius).forEach(nearby::add);
            if (nearby.isEmpty()) {
                return false;
            }
            nearby.forEach(entity -> entity.knockback(strength, entity.getX() - dracula.getX(), entity.getZ() - dracula.getZ()));
            knocked.setWithExpiry(Unit.INSTANCE, 40);
            return true;
        }));
    }
}
