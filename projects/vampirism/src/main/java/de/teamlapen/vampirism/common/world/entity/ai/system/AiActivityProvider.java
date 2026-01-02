package de.teamlapen.vampirism.common.world.entity.ai.system;

import de.teamlapen.vampirism.common.world.entity.ai.activities.ActivityBuilder;
import de.teamlapen.vampirism.common.world.entity.ai.activities.ActivityEntry;
import de.teamlapen.vampirism.common.world.entity.ai.activities.actions.Action;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Encapsulates specific AI behaviors, sensors, and memories for an entity.
 */
public abstract class AiActivityProvider<E extends LivingEntity> {

    private final Activity activity;
    protected final ActivityEntry<E> activityEntry;

    public AiActivityProvider(Activity activity) {
        this.activity = activity;
        ActivityBuilder<E> builder = new ActivityBuilder<>(activity);
        createActivity(builder);
        this.activityEntry = builder.build();
    }

    public AiActivityProvider(Supplier<Activity> activitySupplier) {
        this(activitySupplier.get());
    }

    public boolean isNonCore() {
        return this.activity != Activity.CORE && this.activity != Activity.IDLE;
    }

    public Set<SensorType<? extends Sensor<? super E>>> getSensors() {
        return this.activityEntry.sensors();
    }

    public Set<MemoryModuleType<?>> getMemoryModules() {
        return this.activityEntry.memories();
    }

    public void initActivity(Brain<E> brain) {
        this.activityEntry.register(brain);
    }

    public Activity getActivity() {
        return this.activity;
    }

    public Collection<Action<E>> getActions() {
        return this.activityEntry.actions();
    }

    public Stream<Activity> allActivities() {
        return this.activityEntry.activities();
    }

    protected abstract void createActivity(ActivityBuilder<E> builder);

}
