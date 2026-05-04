package de.teamlapen.vampirism.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.world.items.consume.IFactionFoodBehavior;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.world.items.consume.VampireFoodBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFoodBehaviours {
    public static final DeferredRegister<IFactionFoodBehavior> FOOD_BEHAVIOUR = DeferredRegister.create(FactionRegistries.Keys.FOOD_BEHAVIOUR, REFERENCE.MODID);

    public static final DeferredHolder<IFactionFoodBehavior, VampireFoodBehaviour> VAMPIRE_FOOD = FOOD_BEHAVIOUR.register("vampire_food", VampireFoodBehaviour::new);

    public static void register(IEventBus bus) {
        FOOD_BEHAVIOUR.register(bus);
    }
}
