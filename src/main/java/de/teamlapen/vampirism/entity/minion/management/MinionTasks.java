package de.teamlapen.vampirism.entity.minion.management;


import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.items.BloodBottleItem;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;

public class MinionTasks {
    public static final DeferredRegister<IMinionTask<?, ?>> MINION_TASKS = DeferredRegister.create(VampirismRegistries.Keys.MINION_TASK, REFERENCE.MODID);


    public static final DeferredHolder<IMinionTask<?, ?>, StayTask> STAY = MINION_TASKS.register("stay", StayTask::new);
    public static final DeferredHolder<IMinionTask<?, ?>, DefendAreaTask> DEFEND_AREA = MINION_TASKS.register("defend_area", DefendAreaTask::new);
    public static final DeferredHolder<IMinionTask<?, ?>, SimpleMinionTask> FOLLOW_LORD = MINION_TASKS.register("follow_lord", SimpleMinionTask::new);
    public static final DeferredHolder<IMinionTask<?, ?>, CollectResourcesTask<HunterMinionEntity.HunterMinionData>> COLLECT_HUNTER_ITEMS = MINION_TASKS.register("collect_hunter_items", () -> new CollectResourcesTask<>(ModFactions.HUNTER, data -> (int) (VampirismConfig.BALANCE.miResourceCooldown.get() * (1f - data.getResourceEfficiencyLevel() / HunterMinionEntity.HunterMinionData.MAX_LEVEL_RESOURCES * 0.4f)), Arrays.asList(WeightedEntry.wrap(new ItemStack(ModItems.GARLIC_BREAD.get()), 10), WeightedEntry.wrap(new ItemStack(Items.IRON_NUGGET, 19), 25), WeightedEntry.wrap(new ItemStack(Items.GOLD_NUGGET, 7), 10), WeightedEntry.wrap(new ItemStack(ModItems.ITEM_GARLIC.get(), 2), 15), WeightedEntry.wrap(new ItemStack(Items.COAL, 5), 20)), HunterSkills.MINION_COLLECT));
    public static final DeferredHolder<IMinionTask<?, ?>, CollectResourcesTask<VampireMinionEntity.VampireMinionData>> COLLECT_BLOOD = MINION_TASKS.register("collect_blood", () -> new CollectResourcesTask<>(ModFactions.VAMPIRE, data -> VampirismConfig.BALANCE.miResourceCooldown.get(), Arrays.asList(WeightedEntry.wrap(BloodBottleItem.getStackWithDamage(BloodBottleItem.AMOUNT), 20), WeightedEntry.wrap(new ItemStack(ModItems.HUMAN_HEART.get()), 5), WeightedEntry.wrap(new ItemStack(Items.IRON_NUGGET, 12), 12), WeightedEntry.wrap(new ItemStack(Items.GOLD_NUGGET, 6), 10)), VampireSkills.MINION_COLLECT));
    public static final DeferredHolder<IMinionTask<?, ?>, SimpleMinionTask> NOTHING = MINION_TASKS.register("nothing", NothingTask::new);
    public static final DeferredHolder<IMinionTask<?, ?>, SimpleMinionTask> PROTECT_LORD = MINION_TASKS.register("protect_lord", SimpleMinionTask::new);

    public static void register(IEventBus bus) {
        MINION_TASKS.register(bus);
    }
}
