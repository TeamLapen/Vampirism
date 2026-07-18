package de.teamlapen.vampirism.common.world.entity.ai.activities.actions;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class ActionBuilder<E extends LivingEntity> {

    //<editor-fold desc="Attributes">

    private final Activity activity;
    private final Supplier<Integer> cooldownSupplier;
    private final Set<Pair<MemoryModuleType<?>, MemoryStatus>> requirements = new HashSet<>();
    private final List<BehaviorControl<? super E>> behaviors = new ArrayList<>();
    private final Set<SensorType<? extends Sensor<? super E>>> sensors = new HashSet<>();
    private final Set<MemoryModuleType<?>> memories = new HashSet<>();
    @UnknownNullability
    private MemoryModuleType<Unit> activeMemory;
    private Action.@UnknownNullability Cooldown cooldown;
    private BiPredicate<ServerLevel, E> canActivate = (a,b) -> true;
    private int startPriority = 10;

    //</editor-fold>

    //<editor-fold desc="Constructors">

    public ActionBuilder(Activity activity, Supplier<Integer> cooldownSupplier) {
        this.activity = activity;
        this.cooldownSupplier = cooldownSupplier;
    }

    //</editor-fold>

    //<editor-fold desc="Requirements">

    public ActionBuilder<E> requires(MemoryModuleType<?> memory, MemoryStatus status) {
        this.requirements.add(Pair.of(memory, status));
        this.memories.add(memory);
        return this;
    }

    public <T> ActionBuilder<E> requires(Supplier<MemoryModuleType<T>> memory, MemoryStatus status) {
        return requires(memory.get(), status);
    }

    //</editor-fold>

    //<editor-fold desc="Behaviors">

    public ActionBuilder<E> add(BehaviorControl<? super E> control) {
        return this.add(control, Set.of(), Set.of());
    }

    public ActionBuilder<E> add(BehaviorControl<? super E> control, Set<? extends SensorType<? extends Sensor<? super E>>> sensors, Set<MemoryModuleType<?>> memories) {
        this.behaviors.add(control);
        this.sensors.addAll(sensors);
        this.memories.addAll(memories);
        return this;
    }

    ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> buildBehaviors() {
        ImmutableList.Builder<Pair<Integer, ? extends BehaviorControl<? super E>>> builder = ImmutableList.builder();
        int priority = this.startPriority;
        for (BehaviorControl<? super E> behavior : this.behaviors) {
            builder.add(Pair.of(priority++, behavior));
        }
        return builder.build();
    }

    //</editor-fold>

    //<editor-fold desc="Preconditions">

    @SuppressWarnings("UnusedReturnValue")
    public ActionBuilder<E> canActivate(BiPredicate<ServerLevel, E> canActivate) {
        this.canActivate = canActivate;
        return this;
    }

    //</editor-fold>

    //<editor-fold desc="Memories">

    public ActionBuilder<E> activeMemory(MemoryModuleType<Unit> actionMemory) {
        this.activeMemory = actionMemory;
        this.memories.add(actionMemory);
        return this;
    }

    public ActionBuilder<E> activeMemory(Supplier<MemoryModuleType<Unit>> actionMemory) {
        return activeMemory(actionMemory.get());
    }

    public ActionBuilder<E> cooldown(MemoryModuleType<Unit> cooldownMemory, Supplier<Integer> cooldown) {
        this.cooldown = new Action.Cooldown(cooldownMemory, cooldown);
        this.memories.add(cooldownMemory);
        return this;
    }

    public ActionBuilder<E> cooldown(Supplier<MemoryModuleType<Unit>> cooldownMemory, Supplier<Integer> cooldown) {
        return cooldown(cooldownMemory.get(), cooldown);
    }

    //</editor-fold>

    //<editor-fold desc="Priority">

    public ActionBuilder<E> priority(int priority) {
        this.startPriority = priority;
        return this;
    }

    //</editor-fold>

    //<editor-fold desc="Builder">

    public Action<E> build() {
        Preconditions.checkNotNull(this.activeMemory, "No active memory defined");
        Preconditions.checkNotNull(this.cooldown, "No cooldown memory defined");
        Preconditions.checkState(!this.behaviors.isEmpty(), "No behaviors defined");

        // wrap last behavior
        BehaviorControl<? super E> wrapped = new LastActionBehavior<>(this.behaviors.removeLast(), this.activeMemory, this.cooldown, this.cooldownSupplier);
        this.behaviors.addLast(wrapped);

        return new Action<>(activity, sensors, memories, activeMemory, cooldown, requirements, canActivate, buildBehaviors());
    }

    //</editor-fold>

}
