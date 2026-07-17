package de.teamlapen.vampirism.common.world.entity.ai.activities;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.core.ModSensors;
import de.teamlapen.vampirism.common.world.entity.ai.activities.actions.ActionsBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActivityBuilder<E extends LivingEntity> {

    //<editor-fold desc="Attributes">

    /** Activity of the builder */
    private final Activity activity;
    /** Requirements for the activity */
    private final Set<Pair<MemoryModuleType<?>, MemoryStatus>> requirements = new HashSet<>();
    /** Behaviors of the activity */
    private final List<BehaviorControl<? super E>> behaviors = new ArrayList<>();
    /** Required sensors for the activity */
    private final Set<SensorType<? extends Sensor<? super E>>> sensors = new HashSet<>();
    /** Required memories for the activity */
    private final Set<MemoryModuleType<?>> memories = new HashSet<>();
    /** Subaction builder */
    private final ActionsBuilder<E> actionBuilders = new ActionsBuilder<>(this.behaviors);

    /** Start priority of the activities behaviors */
    private int startPriority = 20;

    //</editor-fold>

    //<editor-fold desc="Constructors">

    public ActivityBuilder(Activity activity) {
        this.activity = activity;
    }

    //</editor-fold>

    //<editor-fold desc="Requirements">

    public ActivityBuilder<E> requires(MemoryModuleType<?> memory, MemoryStatus status) {
        this.requirements.add(Pair.of(memory, status));
        this.memories.add(memory);
        return this;
    }

    public <T> ActivityBuilder<E> requires(Supplier<MemoryModuleType<T>> memory, MemoryStatus status) {
        return requires(memory.get(), status);
    }

    //</editor-fold>

    //<editor-fold desc="Priorities">

    public ActivityBuilder<E> startPriority(int priority) {
        this.startPriority = priority;
        return this;
    }

    //</editor-fold>

    //<editor-fold desc="Actions">

    public ActionsBuilder<E> useActions(@Nullable Supplier<Integer> cooldownSupplier) {
        this.memories.add(ModMemoryTypes.ACTION_ACTIVE.get());
        this.memories.add(ModMemoryTypes.ACTION_COOLDOWN.get());
        this.sensors.add(ModSensors.ACTION_SENSOR.get());

        if (cooldownSupplier != null) {
            this.actionBuilders.cooldown(cooldownSupplier);
        }

        return this.actionBuilders;
    }

    public ActionsBuilder<E> useActions() {
        return this.useActions(null);
    }

    //</editor-fold>

    //<editor-fold desc="Behaviors">

    public ActivityBuilder<E> add(BehaviorControl<? super E> control) {
        return this.add(control, Set.of(), Set.of());
    }

    public ActivityBuilder<E> add(BehaviorControl<? super E> control, Set<SensorType<? extends Sensor<? super E>>> sensors, Set<MemoryModuleType<?>> memories) {
        this.behaviors.add(control);
        this.sensors.addAll(sensors);
        this.memories.addAll(memories);
        return this;
    }

    public ActivityBuilder<E> add(BehaviorDescription<E> consumer) {
        this.behaviors.add(consumer.behavior());
        this.sensors.addAll(consumer.sensors());
        this.memories.addAll(consumer.memories());
        return this;
    }

    public ActivityBuilder<E> add(SensorType<? extends Sensor<? super E>> sensorType) {
        this.sensors.add(sensorType);
        return this;
    }

    private ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> buildBehaviors() {
        ImmutableList.Builder<Pair<Integer, ? extends BehaviorControl<? super E>>> builder = ImmutableList.builder();

        int priority = this.startPriority;

        for (BehaviorControl<? super E> behavior : behaviors) {
            builder.add(Pair.of(priority++, behavior));
        }

        return builder.build();
    }

    //</editor-fold>

    //<editor-fold desc="Builder">

    public ActivityEntry<E> build() {
        return new ActivityEntry<>(this.activity,
                Stream.concat(this.actionBuilders.actions().stream().flatMap(x -> x.sensors().stream()), this.sensors.stream()).collect(Collectors.toSet()),
                Stream.concat(this.actionBuilders.actions().stream().flatMap(a -> a.memories().stream()), this.memories.stream()).collect(Collectors.toSet()),
                this.requirements,
                buildBehaviors(),
                this.actionBuilders.actions()
        );
    }

    //</editor-fold>

}
