package de.teamlapen.vampirism.common.world.entity.dracula.ai.sensors;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.animal.Animal;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class DraculaAttackablesSensor extends Sensor<Dracula> {

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(ModMemoryTypes.NEAREST_ATTACKABLE.get(), ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get(), MemoryModuleType.NEAREST_LIVING_ENTITIES, ModMemoryTypes.ALLIES.get());
    }

    @Override
    protected void doTick(ServerLevel level, Dracula entity) {
        Brain<?> brain = entity.getBrain();
        List<LivingEntity> nearest = brain.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElse(List.of());
        var entities = nearest.stream().filter(EntitySelector.NO_CREATIVE_OR_SPECTATOR);

        Optional<Set<UUID>> allies = brain.getMemory(ModMemoryTypes.ALLIES.get());
        if (allies.isPresent()) {
            entities = entities.filter(x -> !allies.get().contains(x.getUUID()));
        }

        // the list is already sorted by distance
        List<LivingEntity> list = entities.filter(x -> isEntityAttackable(level, entity, x)).toList();
        brain.setMemory(ModMemoryTypes.NEAREST_ATTACKABLE.get(), list);
        brain.setMemory(ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get(), new NearestVisibleLivingEntities(level, entity, list));
    }

}
