package de.teamlapen.vampirism.common.world.entity.ai.activities;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.common.world.entity.ai.activities.actions.Action;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @param activity The activity
 * @param sensors The required sensor types for the activity
 * @param memories The required memory types for the activity
 * @param requirements The required memory state for activation of the activity
 * @param behaviors The behaviors of the activity
 * @param actions sub actions of the activity
 * @param <E> The entity type
 */
public record ActivityEntry<E extends LivingEntity>(
        Activity activity,
        Set<SensorType<? extends Sensor<? super E>>> sensors,
        Set<MemoryModuleType<?>> memories,
        Set<Pair<MemoryModuleType<?>, MemoryStatus>> requirements,
        ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> behaviors,
        List<Action<E>> actions
) {
    /**
     * register the activity and sub actions to the brain
     */
    public void register(Brain<E> brain) {
        brain.addActivityWithConditions(this.activity, this.behaviors, this.requirements);
        for (Action<E> actionBuilder : this.actions) {
            actionBuilder.register(brain, this.requirements);
        }
    }

    /**
     * Get all activities in order of importance
     * <p>
     * The importance means that the actions are prioritized over the base activity
     */
    public Stream<Activity> activities() {
        return Stream.concat(this.actions.stream().map(Action::activity), Stream.of(this.activity));
    }
}
