package de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors;

import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.core.ModSensors;
import de.teamlapen.vampirism.common.world.entity.ai.activities.BehaviorDescription;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;

public class SurroundedEscapeBehavior {

    public static BehaviorDescription<Dracula> build(double radius, int thresholdTicks, int minAttackers) {
        return new BehaviorDescription<>(create(radius, thresholdTicks, minAttackers), sensors(), memories());
    }

    public static Set<SensorType<? extends Sensor<? super Dracula>>> sensors() {
        return Set.of(ModSensors.IS_SURROUNDED.get(), ModSensors.NEAREST_TARGETABLE_ENTITIES.get());
    }

    public static Set<MemoryModuleType<?>> memories() {
        return Set.of(ModMemoryTypes.SURROUNDED_SINCE.get(), ModMemoryTypes.NEAREST_ATTACKABLE.get(), ModMemoryTypes.FLYING_SWORD_ACTIVE.get(), ModMemoryTypes.FLYING_SWORD_COOLDOWN.get(),ModMemoryTypes.ACTION_ACTIVE.get(),ModMemoryTypes.ACTION_COOLDOWN.get(),  ModMemoryTypes.SURROUNDED_COOLDOWN.get());
    }

    public static BehaviorControl<Dracula> create(double radius, int thresholdTicks, int minAttackers) {
        double radiusSqt = radius * radius;

        return BehaviorBuilder.create(inst -> inst.group(
                inst.present(ModMemoryTypes.SURROUNDED_SINCE.get()),
                inst.present(ModMemoryTypes.NEAREST_ATTACKABLE.get()),
                inst.registered(ModMemoryTypes.FLYING_SWORD_ACTIVE.get()),
                inst.registered(ModMemoryTypes.FLYING_SWORD_COOLDOWN.get()),
                inst.registered(ModMemoryTypes.ACTION_ACTIVE.get()),
                inst.registered(ModMemoryTypes.ACTION_COOLDOWN.get()),
                inst.absent(ModMemoryTypes.SURROUNDED_COOLDOWN.get())
        ).apply(inst, (timer,nearest, swordActive, swordCooldown, actionActive, actionCooldown, cooldown) -> (level, dracula, gameTime) -> {
            if (level.getGameTime() - inst.get(timer) < thresholdTicks) return false;

            List<LivingEntity> entities = inst.get(nearest).stream().filter(x -> x.distanceToSqr(dracula) < radiusSqt).toList();
            if (entities.size() < minAttackers) return false;

            for (LivingEntity livingEntity : entities) {
                livingEntity.knockback(1.5, livingEntity.getX() - dracula.getX(), livingEntity.getZ() - dracula.getZ());
                livingEntity.addEffect(new MobEffectInstance(ModEffects.FREEZE, 40, 1));
            }

            Vec3 avgAttackerPos = getAvgPos(entities);
            Vec3 safePos = DefaultRandomPos.getPosAway(dracula, 20, 7, avgAttackerPos);
            if (safePos != null) {
                dracula.teleportTo(safePos.x, safePos.y, safePos.z);
            }

            timer.erase();
            swordActive.set(Unit.INSTANCE);
            swordCooldown.erase();
            actionActive.set(Unit.INSTANCE);
            actionCooldown.erase();
            cooldown.setWithExpiry(Unit.INSTANCE, 20 * 10);
            return false;
        }));
    }

    private static Vec3 getAvgPos(List<LivingEntity> entities) {
        double x = 0;
        double y = 0;
        double z = 0;
        for (LivingEntity entity : entities) {
            x += entity.getX();
            y += entity.getY();
            z += entity.getZ();
        }
        return new Vec3(x / entities.size(), y / entities.size(), z / entities.size());
    }
}
