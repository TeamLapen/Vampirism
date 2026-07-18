package de.teamlapen.vampirism.common.world.entity.ai.sensing;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.world.entity.ai.activities.actions.Action;
import de.teamlapen.vampirism.common.world.entity.ai.system.AiSystem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.schedule.Activity;

import java.util.*;

/**
 * Action sensor for tracking and activating action activities for entities.
 * <p>
 * Action flow:
 * <ul>
 *     <li>When no action is active and the action cooldowns are done, the action sensor checks the actions of the Ai System for available ones and sets the Action memory.</li>
 *     <li>When an action is active, the sensor checks if the action is still valid and cancels the action if it was not activated in 40 ticks</li>
 *     <li>The action itself is responsible for clearing the action active memory and set the cooldown memory ({@link de.teamlapen.vampirism.common.world.entity.ai.activities.actions.LastActionBehavior})</li>
 * </ul>
 */
public class ActionSensor<T extends LivingEntity> extends Sensor<T> {

    @Override
    protected void doTick(ServerLevel level, T entity) {
        //noinspection unchecked
        Brain<T> brain = (Brain<T>) entity.getBrain();
        //noinspection unchecked
        Optional<AiSystem<T>> memory = (Optional<AiSystem<T>>) (Object) brain.getMemory(ModMemoryTypes.AI_SYSTEM.get());
        if (memory.isEmpty()) return;
        updateActions(level, entity, brain,  memory.get().getActions());
    }

    public void updateActions(ServerLevel level, T entity, Brain<T> brain, Map<Activity, Collection<Action<T>>> actionsByActivity) {
        if (brain.hasMemoryValue(ModMemoryTypes.ACTION_ACTIVE.get())) {
            Optional<Long> actionActiveSince = brain.getMemory(ModMemoryTypes.ACTION_ACTIVE_SINCE.get());

            if (actionActiveSince.isEmpty()) {
                brain.setMemory(ModMemoryTypes.ACTION_ACTIVE_SINCE.get(), level.getGameTime());
                return;
            }

            if (level.getGameTime() - actionActiveSince.get() < 40) {
                return;
            }

            if (actionsByActivity.values().stream().flatMap(Collection::stream).map(Action::activity).noneMatch(brain::isActive)) {
                brain.eraseMemory(ModMemoryTypes.ACTION_ACTIVE.get());
                brain.eraseMemory(ModMemoryTypes.ACTION_COOLDOWN.get());
                brain.eraseMemory(ModMemoryTypes.ACTION_ACTIVE_SINCE.get());
            }
        } else if (!brain.hasMemoryValue(ModMemoryTypes.ACTION_COOLDOWN.get())) {
            var activity = brain.getActiveNonCoreActivity().orElse(null);
            if (activity == null || activity == Activity.IDLE) return;
            var actionsSource = actionsByActivity.get(activity);
            if (actionsSource == null) return;
            ArrayList<Action<T>> actions = new ArrayList<>(actionsSource);
            Collections.shuffle(actions);

            for (Action<T> action : actions) {
                if (brain.hasMemoryValue(action.cooldownMemory().memory())) {
                    continue;
                }

                if (!action.precondition().test(level, entity)) {
                    continue;
                }

                if (!action.requirements().stream().allMatch(pair -> brain.checkMemory(pair.getFirst(), pair.getSecond()))) {
                    continue;
                }

                brain.setMemory(ModMemoryTypes.ACTION_ACTIVE.get(), Unit.INSTANCE);
                brain.setMemory(action.activeMemory(), Unit.INSTANCE);
                brain.setMemory(ModMemoryTypes.ACTION_ACTIVE_SINCE.get(), level.getGameTime());
                return;
            }
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(ModMemoryTypes.ACTION_ACTIVE.get(), ModMemoryTypes.ACTION_COOLDOWN.get(), ModMemoryTypes.AI_SYSTEM.get(), ModMemoryTypes.ACTION_ACTIVE_SINCE.get());
    }
}
