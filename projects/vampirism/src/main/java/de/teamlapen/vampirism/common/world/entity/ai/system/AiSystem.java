package de.teamlapen.vampirism.common.world.entity.ai.system;

import com.google.common.collect.ImmutableSet;
import de.teamlapen.vampirism.common.world.entity.ai.activities.actions.Action;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Base class for object-oriented AI systems.
 */
public abstract class AiSystem<E extends LivingEntity> {
    protected final List<AiActivityProvider<E>> activityProviders;

    private Brain.@Nullable Provider<E> brainProvider;

    public AiSystem() {
        this.activityProviders = this.createActivityProviders();
    }

    /**
     * Define the list of activity providers for this AI system.
     */
    protected abstract List<AiActivityProvider<E>> createActivityProviders();

    public Set<SensorType<? extends Sensor<? super E>>> getSensors() {
        return this.activityProviders.stream()
                    .flatMap(p -> p.getSensors().stream())
                    .collect(ImmutableSet.toImmutableSet());
    }

    public Set<MemoryModuleType<?>> getMemoryModules() {
        return this.activityProviders.stream()
                    .flatMap(p -> p.getMemoryModules().stream())
                    .collect(ImmutableSet.toImmutableSet());
    }

    public Brain<E> initializeBrain(Brain<E> brain) {
        this.activityProviders.forEach(p -> p.initActivity(brain));
        this.setupBrainPriorities(brain);

        return brain;
    }

    public Map<Activity,Collection<Action<E>>> getActions() {
        return this.activityProviders.stream().collect(Collectors.toMap(AiActivityProvider::getActivity, AiActivityProvider::getActions));
    }

    public Brain.Provider<E> brainProvider() {
        if (this.brainProvider == null) {
            this.brainProvider = Brain.provider(getMemoryModules(), getSensors());
        }
        return this.brainProvider;
    }

    /**
     * Define core, idle, and default activities.
     */
    protected void setupBrainPriorities(Brain<E> brain) {
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
    }

    @SuppressWarnings("unchecked")
    @MustBeInvokedByOverriders
    public void tick(ServerLevel level, E entity) {
        Brain<E> brain = (Brain<E>) entity.getBrain();
        brain.tick(level, entity);
    }

    @SuppressWarnings("unchecked")
    public void stop(ServerLevel level, E entity) {
        Brain<E> brain = (Brain<E>) entity.getBrain();
        brain.stopAll(level, entity);
        brain.clearMemories();
    }
}
