package de.teamlapen.vampirism.common.world.entity.dracula.ai.sensors;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.DraculaState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DraculaStageSensor extends Sensor<Dracula> {

    @Override
    protected void doTick(ServerLevel serverLevel, Dracula dracula) {
        Set<MemoryModuleType<Unit>> stageMemories = Stream.of(ModMemoryTypes.DRACULA_PHASE_1.get(), ModMemoryTypes.DRACULA_PHASE_2.get(), ModMemoryTypes.DRACULA_PHASE_3.get()).collect(Collectors.toSet());
        MemoryModuleType<Unit> unitMemoryModuleType = memoryForStage(dracula.getState());

        Brain<Dracula> brain = dracula.getBrain();
        if (unitMemoryModuleType != null) {
            stageMemories.remove(unitMemoryModuleType);
            brain.setMemory(unitMemoryModuleType, Unit.INSTANCE);
        }
        stageMemories.forEach(brain::eraseMemory);
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(ModMemoryTypes.DRACULA_PHASE_1.get(), ModMemoryTypes.DRACULA_PHASE_2.get(), ModMemoryTypes.DRACULA_PHASE_3.get());
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
