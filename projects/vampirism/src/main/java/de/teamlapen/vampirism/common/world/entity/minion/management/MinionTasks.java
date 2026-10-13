package de.teamlapen.vampirism.common.world.entity.minion.management;


import de.teamlapen.faction.api.registries.minion_tasks.DeferredMinionTask;
import de.teamlapen.faction.api.registries.minion_tasks.DeferredMinionTaskRegister;
import de.teamlapen.faction.common.core.FactionMinionTaskCategories;
import de.teamlapen.faction.common.factions.minions.management.CollectResourcesTask;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.common.core.ModLootTables;
import de.teamlapen.vampirism.common.world.entity.player.vampire.skills.VampireSkills;
import net.neoforged.bus.api.IEventBus;

public class MinionTasks {
    public static final DeferredMinionTaskRegister MINION_TASKS = DeferredMinionTaskRegister.create(REFERENCE.MODID);

    public static final DeferredMinionTask<CollectResourcesTask.State, CollectResourcesTask> COLLECT_BLOOD = MINION_TASKS.registerTask("collect_blood", p -> new CollectResourcesTask(p, ModLootTables.MINION_COLLECT_BLOOD), p -> p.categoryDefault(FactionMinionTaskCategories.COLLECT_RESOURCES).factions(VampirismTags.Factions.IS_VAMPIRE).requires(VampireSkills.MINION_COLLECT));

    public static void register(IEventBus bus) {
        MINION_TASKS.register(bus);
    }
}
