package de.teamlapen.vampirism.common.world.entity.ai.activities.actions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.schedule.Activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Action helper builder
 */
public class ActionsBuilder<E extends LivingEntity> {

    private final List<Action<E>> actions = new ArrayList<>();
    private Supplier<Integer> cooldownSupplier = () -> 20;
    private final List<BehaviorControl<? super E>> behaviors;

    public ActionsBuilder(List<BehaviorControl<? super E>> behaviors) {
        this.behaviors = behaviors;
    }

    public List<Action<E>> actions() {
        return Collections.unmodifiableList(actions);
    }

    public void addAction(Activity activity, Consumer<ActionBuilder<E>> consumer) {
        ActionBuilder<E> builder = new ActionBuilder<>(activity, this.cooldownSupplier);
        consumer.accept(builder);
//        this.behaviors.forEach(builder::add);
        this.actions.add(builder.build());
    }

    public void addAction(Supplier<Activity> activity, Consumer<ActionBuilder<E>> consumer) {
        addAction(activity.get(), consumer);
    }

    public ActionsBuilder<E> cooldown(Supplier<Integer> cooldownSupplier) {
        this.cooldownSupplier = cooldownSupplier;
        return this;
    }

}
