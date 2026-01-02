package de.teamlapen.vampirism.common.world.entity.ai.activities.actions;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.function.Supplier;

/**
 * Behavior wrapper that handles action cooldown and action end events
 *
 * @param original         The actual behavior to wrap
 * @param activeMemory     The memory module type for tracking action status
 * @param cooldownMemory   The memory module type for tracking action cooldown status
 * @param cooldownSupplier The supplier for the cross-action cooldown duration
 * @param <E>              The entity type
 */
public record LastActionBehavior<E extends LivingEntity>(BehaviorControl<E> original,
                                                  MemoryModuleType<Unit> activeMemory,
                                                  Action.Cooldown cooldownMemory,
                                                  Supplier<Integer> cooldownSupplier) implements BehaviorControl<E> {

    @Override
    public Behavior.Status getStatus() {
        return this.original.getStatus();
    }

    @Override
    public boolean tryStart(ServerLevel level, E entity, long gameTime) {
        return this.original.tryStart(level, entity, gameTime);
    }

    @Override
    public void tickOrStop(ServerLevel level, E entity, long gameTime) {
        this.original.tickOrStop(level, entity, gameTime);
        if (this.original.getStatus() == Behavior.Status.STOPPED) {
            stopAction(entity);
        }
    }

    @Override
    public void doStop(ServerLevel level, E entity, long gameTime) {
        this.original.doStop(level, entity, gameTime);
        stopAction(entity);
    }

    private void stopAction(E entity) {
        Brain<?> brain = entity.getBrain();
        brain.eraseMemory(activeMemory);
        brain.eraseMemory(ModMemoryTypes.ACTION_ACTIVE.get());
        brain.setMemoryWithExpiry(cooldownMemory.memory(), Unit.INSTANCE, cooldownMemory.cooldown());
        brain.setMemoryWithExpiry(ModMemoryTypes.ACTION_COOLDOWN.get(), Unit.INSTANCE, cooldownSupplier.get());
    }

    @Override
    public String debugString() {
        return "LastAction[" + this.original.debugString() + "]";
    }
}
