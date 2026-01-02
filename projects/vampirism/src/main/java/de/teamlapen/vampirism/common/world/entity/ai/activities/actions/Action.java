package de.teamlapen.vampirism.common.world.entity.ai.activities.actions;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @param activity The activity of the action
 * @param sensors Required sensor types for the activity
 * @param memories Required memory types for the activity
 * @param activeMemory The memory module type for tracking action status
 * @param cooldownMemory The memory module type for tracking action cooldown status
 * @param requirements Required memory state for activation of the activity
 * @param precondition Additional action activation predicate
 * @param behaviors The behaviors of the activity
 * @param <E>
 */
public record Action<E extends LivingEntity>(
        Activity activity,
        Collection<SensorType<? extends Sensor<? super E>>> sensors,
        Collection<MemoryModuleType<?>> memories,
        MemoryModuleType<Unit> activeMemory,
        Cooldown cooldownMemory,
        Set<Pair<MemoryModuleType<?>, MemoryStatus>> requirements,
        BiPredicate<ServerLevel, E> precondition,
        ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> behaviors
) {
    public void register(Brain<E> brain, Set<Pair<MemoryModuleType<?>, MemoryStatus>> requirements) {
        Stream<Stream<Pair<MemoryModuleType<?>, MemoryStatus>>> stream = Stream.of(requirements.stream(), this.requirements.stream(), Stream.of(Pair.of(activeMemory, MemoryStatus.VALUE_PRESENT), Pair.of(cooldownMemory.memory, MemoryStatus.VALUE_ABSENT)));
        brain.addActivityWithConditions(this.activity, this.behaviors, stream.flatMap(x -> x).collect(Collectors.toUnmodifiableSet()));
    }

    public record Cooldown(MemoryModuleType<Unit> memory, Supplier<Integer> cooldownSupplier) {
        public int cooldown() {
            return this.cooldownSupplier.get();
        }
    }
}
