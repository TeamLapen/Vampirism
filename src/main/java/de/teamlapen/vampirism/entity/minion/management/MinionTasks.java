package de.teamlapen.vampirism.entity.minion.management;


import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.items.BloodBottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Arrays;

public class MinionTasks {
    public static final DeferredRegister<IMinionTask<?,?>> MINION_TASKS = DeferredRegister.create(ModRegistries.MINION_TASKS_ID, REFERENCE.MODID);
    

    public static final RegistryObject<SimpleMinionTask> nothing;
    public static final RegistryObject<StayTask> stay;
    public static final RegistryObject<DefendAreaTask> defend_area;
    public static final RegistryObject<SimpleMinionTask> follow_lord;
    public static final RegistryObject<CollectResourcesTask<HunterMinionEntity.HunterMinionData>> collect_hunter_items;
    public static final RegistryObject<SimpleMinionTask> protect_lord;
    public static final RegistryObject<CollectResourcesTask<VampireMinionEntity.VampireMinionData>> collect_blood;

    public static void register(IEventBus bus) {
        MINION_TASKS.register(bus);
    }

    static {
        stay = MINION_TASKS.register("stay", StayTask::new);
        defend_area = MINION_TASKS.register("defend_area", DefendAreaTask::new);
        follow_lord = MINION_TASKS.register("follow_lord", SimpleMinionTask::new);
        collect_hunter_items = MINION_TASKS.register("collect_hunter_items", () -> new CollectResourcesTask<HunterMinionEntity.HunterMinionData>(VReference.HUNTER_FACTION, data -> (int) (VampirismConfig.BALANCE.miResourceCooldown.get() * (1f - data.getResourceEfficiencyLevel() / HunterMinionEntity.HunterMinionData.MAX_LEVEL_RESOURCES * 0.4f)), Arrays.asList(new WeightedRandomItem<>(new ItemStack(ModItems.garlic_bread.get()), 10), new WeightedRandomItem<>(new ItemStack(Items.IRON_NUGGET, 19), 25), new WeightedRandomItem<>(new ItemStack(Items.GOLD_NUGGET, 7), 10), new WeightedRandomItem<>(new ItemStack(ModItems.item_garlic.get(), 2), 15), new WeightedRandomItem<>(new ItemStack(Items.COAL, 5), 20))));
        collect_blood = MINION_TASKS.register("collect_blood", () -> new CollectResourcesTask<VampireMinionEntity.VampireMinionData>(VReference.VAMPIRE_FACTION, data -> VampirismConfig.BALANCE.miResourceCooldown.get(), Arrays.asList(new WeightedRandomItem<>(BloodBottleItem.getStackWithDamage(BloodBottleItem.AMOUNT), 20), new WeightedRandomItem<>(new ItemStack(ModItems.human_heart.get()), 5), new WeightedRandomItem<>(new ItemStack(Items.IRON_NUGGET, 12), 12), new WeightedRandomItem<>(new ItemStack(Items.GOLD_NUGGET, 6), 10))));
        nothing = MINION_TASKS.register("nothing", () -> new SimpleMinionTask() {
            @Override
            public boolean isAvailable(IFaction<?> faction, @Nullable ILordPlayer player) {
                return false;
            }
        });
        protect_lord = MINION_TASKS.register("protect_lord", SimpleMinionTask::new);
    }
}
