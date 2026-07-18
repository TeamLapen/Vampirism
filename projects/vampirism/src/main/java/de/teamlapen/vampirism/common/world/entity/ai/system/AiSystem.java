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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base class for object-oriented AI systems.
 */
public abstract class AiSystem<E extends LivingEntity> {
    protected final List<AiActivityProvider<E>> activityProviders;

    private final Brain.Provider<E> brainProvider;

    public AiSystem() {
        this.activityProviders = this.createActivityProviders();
        this.brainProvider = Brain.provider(Stream.concat(getMemoryModules().stream(), extraMemories().stream()).collect(Collectors.toSet()), getSensors(), (entity) -> this.activityProviders.stream().flatMap(x -> x.getData(entity)).toList());
    }

    protected Set<? extends MemoryModuleType<?>> extraMemories() {
        return Set.of();
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

    public Map<Activity,Collection<Action<E>>> getActions() {
        return this.activityProviders.stream().collect(Collectors.toMap(AiActivityProvider::getActivity, AiActivityProvider::getActions));
    }

    public Brain.Provider<E> brainProvider() {
        return this.brainProvider;
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
