package de.teamlapen.vampirism.common.world.entity.dracula.ai;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.util.StreamUtil;
import de.teamlapen.vampirism.common.world.entity.ai.system.AiActivityProvider;
import de.teamlapen.vampirism.common.world.entity.ai.system.AiSystem;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.activities.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DraculaAiSystem extends AiSystem<Dracula> {

    public static final DraculaAiSystem AI = new DraculaAiSystem();

    @Override
    protected List<AiActivityProvider<Dracula>> createActivityProviders() {
        return List.of(
                new DraculaCoreActivityProvider(),
                new DraculaIdleActivityProvider(),
                new DraculaPhase1ActivityProvider(),
                new DraculaPhase2ActivityProvider(),
                new DraculaPhase3ActivityProvider()
        );
    }

    @Override
    public void tick(ServerLevel level, Dracula entity) {
        this.updateMemories(entity);
        this.updateActivity(level, entity);
        super.tick(level, entity);
    }

    private void updateMemories(Dracula entity) {
        Brain<Dracula> brain = entity.getBrain();
        if (!brain.hasMemoryValue(ModMemoryTypes.AI_SYSTEM.get())) {
            brain.setMemory(ModMemoryTypes.AI_SYSTEM.get(), this);
        }
        Set<MemoryModuleType<Unit>> stageMemories = Stream.of(ModMemoryTypes.DRACULA_PHASE_1.get(), ModMemoryTypes.DRACULA_PHASE_2.get(), ModMemoryTypes.DRACULA_PHASE_3.get()).collect(Collectors.toSet());
        MemoryModuleType<Unit> unitMemoryModuleType = memoryForStage(entity.getState());

        if (unitMemoryModuleType != null) {
            stageMemories.remove(unitMemoryModuleType);
            brain.setMemory(unitMemoryModuleType, Unit.INSTANCE);
        }
        stageMemories.forEach(brain::eraseMemory);
    }

    private void updateActivity(ServerLevel level, Dracula entity) {
        Brain<Dracula> brain = entity.getBrain();
        brain.setActiveActivityToFirstValid(StreamUtil.append(this.activityProviders.stream().filter(AiActivityProvider::isNonCore).flatMap(AiActivityProvider::allActivities), Activity.IDLE).toList());
    }

    @Nullable
    private static MemoryModuleType<Unit> memoryForStage(DraculaState state) {
        return switch (state) {
            case PASSIVE -> ModMemoryTypes.DRACULA_PHASE_1.get();
            case RANGED -> ModMemoryTypes.DRACULA_PHASE_2.get();
            case RAGED, MIST -> ModMemoryTypes.DRACULA_PHASE_3.get();
            default -> null;
        };
    }
}
