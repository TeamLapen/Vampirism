package de.teamlapen.vampirism.common.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.world.entity.ai.sensing.ActionSensor;
import de.teamlapen.vampirism.common.world.entity.ai.sensing.HurtBySensor;
import de.teamlapen.vampirism.common.world.entity.ai.sensing.LivingTargetableLivingEntitySensor;
import de.teamlapen.vampirism.common.world.entity.ai.sensing.SurroundedSensor;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.sensors.DraculaStageSensor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSensors {

    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(Registries.SENSOR_TYPE, REFERENCE.MODID);

    public static final DeferredHolder<SensorType<?>, SensorType<LivingTargetableLivingEntitySensor<LivingEntity>>> NEAREST_TARGETABLE_ENTITIES = SENSOR_TYPES.register("nearest_targetable_entity", () -> new SensorType<>(LivingTargetableLivingEntitySensor::new));
    public static final DeferredHolder<SensorType<?>, SensorType<SurroundedSensor<LivingEntity>>> IS_SURROUNDED = SENSOR_TYPES.register("is_surrounded", () -> new SensorType<>(SurroundedSensor::new));
    public static final DeferredHolder<SensorType<?>, SensorType<HurtBySensor>> HURT_BY = SENSOR_TYPES.register("hurt_by", () -> new SensorType<>(HurtBySensor::new));
    public static final DeferredHolder<SensorType<?>, SensorType<ActionSensor<LivingEntity>>> ACTION_SENSOR = SENSOR_TYPES.register("action_sensor", () -> new SensorType<>(ActionSensor::new));
    public static final DeferredHolder<SensorType<?>, SensorType<DraculaStageSensor>> DRACULA_STAGE_SENSOR = SENSOR_TYPES.register("dracula_stage_sensor", () -> new SensorType<>(DraculaStageSensor::new));

    static void register(IEventBus bus) {
        SENSOR_TYPES.register(bus);
    }
}
