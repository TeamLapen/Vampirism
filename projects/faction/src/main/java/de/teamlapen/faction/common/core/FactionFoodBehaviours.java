package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.api.world.items.consume.IFactionFoodBehavior;
import de.teamlapen.faction.common.world.items.consume.DefaultFactionFoodBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionFoodBehaviours {
    public static final DeferredRegister<IFactionFoodBehavior> FOOD_BEHAVIOUR = DeferredRegister.create(FactionRegistries.Keys.FOOD_BEHAVIOUR, REFERENCE.MOD_ID);

    public static final DeferredHolder<IFactionFoodBehavior, DefaultFactionFoodBehaviour> DEFAULT = FOOD_BEHAVIOUR.register("default", DefaultFactionFoodBehaviour::new);

    public static void register(IEventBus bus) {
        FOOD_BEHAVIOUR.register(bus);
    }
}
