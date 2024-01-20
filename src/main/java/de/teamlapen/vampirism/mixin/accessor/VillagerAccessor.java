package de.teamlapen.vampirism.mixin.accessor;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Villager.class)
public interface VillagerAccessor {

    @Accessor("SENSOR_TYPES")
    static ImmutableList<SensorType<? extends Sensor<? super Villager>>> getSensorTypes() {
        throw new IllegalStateException("Mixin failed to apply");
    }

    @Accessor("MEMORY_TYPES")
    static ImmutableList<MemoryModuleType<?>> getMemoryTypes() {
        throw new IllegalStateException("Mixin failed to apply");
    }

}
