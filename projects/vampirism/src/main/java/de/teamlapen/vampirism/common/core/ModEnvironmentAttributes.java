package de.teamlapen.vampirism.common.core;

import de.teamlapen.vampirism.api.VReference;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.AttributeTypes;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEnvironmentAttributes {

    public static final DeferredRegister<EnvironmentAttribute<?>> ENVIRONMENT_ATTRIBUTES = DeferredRegister.create(Registries.ENVIRONMENT_ATTRIBUTE, VReference.MODID);

    public static final DeferredHolder<EnvironmentAttribute<?>, EnvironmentAttribute<Boolean>> SUN_DAMAGE = ENVIRONMENT_ATTRIBUTES.register("sun_damage", () -> EnvironmentAttribute.builder(AttributeTypes.BOOLEAN).defaultValue(true).syncable().build());
    public static final DeferredHolder<EnvironmentAttribute<?>, EnvironmentAttribute<Float>> SUN_INTENSITY = ENVIRONMENT_ATTRIBUTES.register("sun_intensity", () -> EnvironmentAttribute.builder(AttributeTypes.FLOAT).defaultValue(1.0f).syncable().build());

    static void register(IEventBus bus) {
        ENVIRONMENT_ATTRIBUTES.register(bus);
    }


}
