package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.entity.ai.sensing.VampireVillagerHostilesSensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModAi {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, REFERENCE.MODID);

    public static final RegistryObject<SensorType<VampireVillagerHostilesSensor>> VAMPIRE_VILLAGER_HOSTILES = SENSOR_TYPES.register("vampire_villager_hostiles", () -> new SensorType<>(VampireVillagerHostilesSensor::new));

    static void register(IEventBus bus) {
        SENSOR_TYPES.register(bus);
    }
}
