package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.util.REFERENCE;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, REFERENCE.MOD_ID);

    public static final DeferredHolder<Attribute, RangedAttribute> RESOURCE_EFFICIENCY = ATTRIBUTES.register("resource_efficiency", () -> (RangedAttribute) new RangedAttribute("attribute.factionapi.resource_efficiency", 0.3, 0.0, 1.0).setSyncable(true));


    static void register(IEventBus bus) {
        ATTRIBUTES.register(bus);
    }
}
