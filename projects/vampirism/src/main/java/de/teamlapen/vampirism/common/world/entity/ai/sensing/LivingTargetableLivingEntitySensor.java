package de.teamlapen.vampirism.common.world.entity.ai.sensing;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.*;

public class LivingTargetableLivingEntitySensor<T extends LivingEntity> extends Sensor<T> {

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(ModMemoryTypes.NEAREST_ATTACKABLE.get(), ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get(), MemoryModuleType.NEAREST_LIVING_ENTITIES);
    }

    @Override
    protected void doTick(ServerLevel level, T entity) {
        Brain<?> brain = entity.getBrain();
        var entities = brain.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).stream().flatMap(Collection::stream);

        Optional<Set<UUID>> allies = brain.getMemory(ModMemoryTypes.ALLIES.get());
        if (allies.isPresent()) {
            entities = entities.filter(x -> !allies.get().contains(x.getUUID()));
        }

        // the list is already sorted by distance
        entities = entities.filter(x -> isAttackable(level, entity, x));

        List<LivingEntity> list = entities.toList();
        brain.setMemory(ModMemoryTypes.NEAREST_ATTACKABLE.get(), list);
        brain.setMemory(ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get(), new NearestVisibleLivingEntities(level, entity, list));
    }

    private boolean isAttackable(ServerLevel level, LivingEntity entity, LivingEntity target) {
        return isEntityAttackable(level, entity, target);
    }
}
