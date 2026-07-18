package de.teamlapen.vampirism.common.world.entity.ai.sensing;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.world.entity.ai.memory.HurtByEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Hurt by sensor for managing {@link de.teamlapen.vampirism.common.core.ModMemoryTypes#HURT_BY_ENTITIES} ({@link de.teamlapen.vampirism.common.world.entity.ai.memory.HurtByEntities})
 * <p>
 * This sensor removes outdated entries in the memory, it does not add new entries. For that update the memory in the {@link net.minecraft.world.entity.Entity#hurtServer(net.minecraft.server.level.ServerLevel, net.minecraft.world.damagesource.DamageSource, float)}
 * <pre>
 * HurtByEntities hurtByEntities = getBrain().getMemory(ModMemoryTypes.HURT_BY_ENTITIES.get()).orElseGet(HurtByEntities::empty);
 * getBrain().setMemory(ModMemoryTypes.HURT_BY_ENTITIES.get(), hurtByEntities.hurtBy(level,this, source));
 * </pre>
 */
public class HurtBySensor extends Sensor<LivingEntity> {
    @Override
    protected void doTick(ServerLevel level, LivingEntity entity) {
        Brain<?> brain = entity.getBrain();
        HurtByEntities hurtBy = brain.getMemory(ModMemoryTypes.HURT_BY_ENTITIES.get()).orElseGet(HurtByEntities::empty);
        List<HurtByEntities.HurtBy> remove = hurtBy.hurtBy().stream().filter(x -> x.time() + x.keepInMemoryTicks() > level.getGameTime()).toList();
        if (remove.isEmpty()) return;

        ArrayList<HurtByEntities.HurtBy> newHurts = new ArrayList<>(hurtBy.hurtBy());
        newHurts.removeAll(remove);

        if (newHurts.isEmpty()) {
            brain.eraseMemory(ModMemoryTypes.HURT_BY_ENTITIES.get());
        } else {
            brain.setMemory(ModMemoryTypes.HURT_BY_ENTITIES.get(), new HurtByEntities(newHurts));
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(ModMemoryTypes.HURT_BY_ENTITIES.get());
    }
}
