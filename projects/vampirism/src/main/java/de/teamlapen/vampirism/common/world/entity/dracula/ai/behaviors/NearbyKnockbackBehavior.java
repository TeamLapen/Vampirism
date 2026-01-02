package de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.core.ModSensors;
import de.teamlapen.vampirism.common.world.entity.ai.activities.BehaviorDescription;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.Set;

public class NearbyKnockbackBehavior {


    public static BehaviorDescription<Dracula> build(double radius, double strength) {
        return new BehaviorDescription<>(create(radius, strength), sensors(), memories());
    }

    public static Set<SensorType<? extends Sensor<? super Dracula>>> sensors() {
        return Set.of(ModSensors.NEAREST_TARGETABLE_ENTITIES.get());
    }

    public static Set<MemoryModuleType<?>> memories() {
        return Set.of(ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get(), ModMemoryTypes.KNOCKED_BACK.get());
    }


    public static BehaviorControl<Dracula> create(double radius, double strength) {
        return BehaviorBuilder.create(inst -> inst.group(
                inst.present(ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get()),
                inst.absent(ModMemoryTypes.KNOCKED_BACK.get())
        ).apply(inst, (attackable, knocked) -> (level, dracula, gameTime) -> {
            inst.get(attackable).findAll(x -> x.distanceToSqr(dracula) < radius * radius).forEach(entity -> entity.knockback(strength, entity.getX() - dracula.getX(), entity.getZ() - dracula.getZ()));
            knocked.setWithExpiry(Unit.INSTANCE, 40);
            return true;
        }));
    }
}
