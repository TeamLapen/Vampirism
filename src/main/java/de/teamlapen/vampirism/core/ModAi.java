package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.entity.ai.sensing.VampireVillagerHostilesSensor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAi {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(Registries.SENSOR_TYPE, REFERENCE.MODID);

    public static final DeferredHolder<SensorType<?>, SensorType<VampireVillagerHostilesSensor>> VAMPIRE_VILLAGER_HOSTILES = SENSOR_TYPES.register("vampire_villager_hostiles", () -> new SensorType<>(VampireVillagerHostilesSensor::new));

    static void register(IEventBus bus) {
        SENSOR_TYPES.register(bus);
    }
}
