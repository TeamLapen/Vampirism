package de.teamlapen.vampirism.common.world.entity.ai.sensing;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.Optional;
import java.util.Set;

/**
 * Checks how long the sensor owner is surrounded by attackable entities.
 */
public class SurroundedSensor<T extends LivingEntity> extends Sensor<T> {

    private static final int RADIUS_SQT = 4;

    @Override
    protected void doTick(ServerLevel level, T entity) {
        Brain<?> brain = entity.getBrain();
        var nearest = brain.getMemory(ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get()).orElseGet(NearestVisibleLivingEntities::empty);

        Optional<LivingEntity> closest = nearest.findClosest(x -> true);
        if (closest.isPresent() && closest.get().distanceToSqr(entity) < RADIUS_SQT) {
            if (!brain.hasMemoryValue(ModMemoryTypes.SURROUNDED_SINCE.get())) {
                brain.setMemory(ModMemoryTypes.SURROUNDED_SINCE.get(), level.getGameTime());
            }
        } else {
            brain.eraseMemory(ModMemoryTypes.SURROUNDED_SINCE.get());
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(ModMemoryTypes.SURROUNDED_SINCE.get(), ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get());
    }
}
