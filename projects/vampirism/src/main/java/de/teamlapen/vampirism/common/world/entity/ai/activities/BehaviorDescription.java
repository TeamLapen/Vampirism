package de.teamlapen.vampirism.common.world.entity.ai.activities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.Set;

/**
 * A description of a behavior.
 *
 * @param behavior The behavior
 * @param sensors The sensor types required for the behavior
 * @param memories The memory types required for the behavior
 * @param <E> The entity type
 */
public record BehaviorDescription<E extends LivingEntity>(BehaviorControl<? super E> behavior, Set<SensorType<? extends Sensor<? super E>>> sensors, Set<MemoryModuleType<?>> memories) {

}
