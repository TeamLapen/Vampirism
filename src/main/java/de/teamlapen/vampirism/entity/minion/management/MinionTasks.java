package de.teamlapen.vampirism.entity.minion.management;


import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.items.BloodBottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class MinionTasks {
    public static final DeferredRegister<IMinionTask<?, ?>> MINION_TASKS = DeferredRegister.create(VampirismRegistries.MINION_TASKS_ID, REFERENCE.MODID);


    public static final RegistryObject<StayTask> STAY = MINION_TASKS.register("stay", StayTask::new);
    public static final RegistryObject<DefendAreaTask> DEFEND_AREA = MINION_TASKS.register("defend_area", DefendAreaTask::new);
    public static final RegistryObject<SimpleMinionTask> FOLLOW_LORD = MINION_TASKS.register("follow_lord", SimpleMinionTask::new);
    public static final RegistryObject<CollectResourcesTask<HunterMinionEntity.HunterMinionData>> COLLECT_HUNTER_ITEMS = MINION_TASKS.register("collect_hunter_items", () -> new CollectResourcesTask<>(VReference.HUNTER_FACTION, data -> (int) (VampirismConfig.BALANCE.miResourceCooldown.get() * (1f - data.getResourceEfficiencyLevel() / HunterMinionEntity.HunterMinionData.MAX_LEVEL_RESOURCES * 0.4f)), Arrays.asList(new WeightedRandomItem<>(new ItemStack(ModItems.GARLIC_BREAD.get()), 10), new WeightedRandomItem<>(new ItemStack(Items.IRON_NUGGET, 19), 25), new WeightedRandomItem<>(new ItemStack(Items.GOLD_NUGGET, 7), 10), new WeightedRandomItem<>(new ItemStack(ModItems.ITEM_GARLIC.get(), 2), 15), new WeightedRandomItem<>(new ItemStack(Items.COAL, 5), 20)), HunterSkills.HUNTER_MINION_COLLECT::get));
    public static final RegistryObject<CollectResourcesTask<VampireMinionEntity.VampireMinionData>> COLLECT_BLOOD = MINION_TASKS.register("collect_blood", () -> new CollectResourcesTask<>(VReference.VAMPIRE_FACTION, data -> VampirismConfig.BALANCE.miResourceCooldown.get(), Arrays.asList(new WeightedRandomItem<>(BloodBottleItem.getStackWithDamage(BloodBottleItem.AMOUNT), 20), new WeightedRandomItem<>(new ItemStack(ModItems.HUMAN_HEART.get()), 5), new WeightedRandomItem<>(new ItemStack(Items.IRON_NUGGET, 12), 12), new WeightedRandomItem<>(new ItemStack(Items.GOLD_NUGGET, 6), 10)), VampireSkills.VAMPIRE_MINION_COLLECT::get));
    public static final RegistryObject<SimpleMinionTask> NOTHING = MINION_TASKS.register("nothing", () -> new SimpleMinionTask() {
        @Override
        public boolean isAvailable(IPlayableFaction<?> faction, @Nullable ILordPlayer player) {
            return false;
        }
    });
    public static final RegistryObject<SimpleMinionTask> PROTECT_LORD = MINION_TASKS.register("protect_lord", SimpleMinionTask::new);

    public static void register(IEventBus bus) {
        MINION_TASKS.register(bus);
    }
}
