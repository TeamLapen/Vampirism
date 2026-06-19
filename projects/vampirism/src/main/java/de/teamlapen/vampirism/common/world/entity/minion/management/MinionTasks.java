package de.teamlapen.vampirism.common.world.entity.minion.management;


import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.common.factions.minions.management.CollectResourcesTask;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.world.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.common.world.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.entity.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.common.world.items.BloodBottleItem;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;

public class MinionTasks {
    public static final DeferredRegister<IMinionTask<?, ?>> MINION_TASKS = DeferredRegister.create(FactionRegistries.Keys.MINION_TASK, REFERENCE.MODID);

    public static final DeferredHolder<IMinionTask<?, ?>, CollectResourcesTask<VampireMinionEntity.VampireMinionData>> COLLECT_BLOOD = MINION_TASKS.register("collect_blood", () -> new CollectResourcesTask<>(MinionTasks.COLLECT_BLOOD, ModFactions.VAMPIRE, data -> ModConfig.balance().miResourceCooldown.get(),() -> Arrays.asList(new Weighted<>(BloodBottleItem.createStackWithBlood(BloodBottleItem.AMOUNT), 20), new Weighted<>(new ItemStack(ModItems.HUMAN_HEART.get()), 5), new Weighted<>(new ItemStack(Items.IRON_NUGGET, 12), 12), new Weighted<>(new ItemStack(Items.GOLD_NUGGET, 6), 10)), VampireSkills.MINION_COLLECT));
    public static final DeferredHolder<IMinionTask<?, ?>, CollectResourcesTask<HunterMinionEntity.HunterMinionData>> COLLECT_HUNTER_ITEMS = MINION_TASKS.register("collect_hunter_items", () -> new CollectResourcesTask<>(MinionTasks.COLLECT_HUNTER_ITEMS, ModFactions.HUNTER, data -> (int) (ModConfig.balance().miResourceCooldown.get() * (1f - data.getResourceEfficiencyLevel() / (float) HunterMinionEntity.HunterMinionData.RESOURCES_STATS.getMaxLevel() * 0.4f)), () -> Arrays.asList(new Weighted<>(new ItemStack(ModItems.GARLIC_BREAD.get()), 10), new Weighted<>(new ItemStack(Items.IRON_NUGGET, 19), 25), new Weighted<>(new ItemStack(Items.GOLD_NUGGET, 7), 10), new Weighted<>(new ItemStack(ModBlocks.GARLIC.get(), 2), 15), new Weighted<>(new ItemStack(Items.COAL, 5), 20)), HunterSkills.MINION_COLLECT));

    public static void register(IEventBus bus) {
        MINION_TASKS.register(bus);
    }
}
