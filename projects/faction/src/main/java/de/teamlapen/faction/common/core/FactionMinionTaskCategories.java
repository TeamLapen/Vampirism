package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTaskCategory;
import de.teamlapen.faction.common.factions.minions.management.MinionTaskCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionMinionTaskCategories {

    public static final DeferredRegister<IMinionTaskCategory> MINION_TASK_CATEGORIES = DeferredRegister.create(FactionRegistries.Keys.MINION_TASK_CATEGORY, REFERENCE.MOD_ID);

    public static final DeferredHolder<IMinionTaskCategory, MinionTaskCategory> COLLECT_RESOURCES = MINION_TASK_CATEGORIES.register("collect_resources", MinionTaskCategory::new);
    public static final DeferredHolder<IMinionTaskCategory, MinionTaskCategory> DEFEND = MINION_TASK_CATEGORIES.register("defend", MinionTaskCategory::new);
    public static final DeferredHolder<IMinionTaskCategory, MinionTaskCategory> MOVEMENT = MINION_TASK_CATEGORIES.register("movement", MinionTaskCategory::new);
    public static final DeferredHolder<IMinionTaskCategory, MinionTaskCategory> RECALL = MINION_TASK_CATEGORIES.register("recall", MinionTaskCategory::new);

    public static void register(IEventBus bus) {
        MINION_TASK_CATEGORIES.register(bus);
    }
}
