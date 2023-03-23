package de.teamlapen.vampirism.core;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.task.*;
import de.teamlapen.vampirism.entity.player.tasks.TaskBuilder;
import de.teamlapen.vampirism.entity.player.tasks.req.*;
import de.teamlapen.vampirism.entity.player.tasks.reward.ConsumerReward;
import de.teamlapen.vampirism.entity.player.tasks.reward.ItemReward;
import de.teamlapen.vampirism.entity.player.tasks.reward.LordLevelReward;
import de.teamlapen.vampirism.entity.player.tasks.reward.RefinementItemReward;
import de.teamlapen.vampirism.entity.player.tasks.unlock.LordLvlUnlocker;
import de.teamlapen.vampirism.entity.player.tasks.unlock.LvlUnlocker;
import de.teamlapen.vampirism.entity.player.tasks.unlock.ParentUnlocker;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class ModTasks {
    public static final DeferredRegister<Codec<? extends TaskUnlocker>> TASK_UNLOCKER = DeferredRegister.create(VampirismRegistries.TASK_UNLOCKER_ID, REFERENCE.MODID);
    public static final DeferredRegister<Codec<? extends TaskReward>> TASK_REWARDS = DeferredRegister.create(VampirismRegistries.TASK_REWARD_ID, REFERENCE.MODID);
    public static final DeferredRegister<Codec<? extends TaskRequirement.Requirement<?>>> TASK_REQUIREMENTS = DeferredRegister.create(VampirismRegistries.TASK_REQUIREMENT_ID, REFERENCE.MODID);
    public static final DeferredRegister<Codec<? extends ITaskRewardInstance>> TASK_REWARD_INSTANCES = DeferredRegister.create(VampirismRegistries.TASK_REWARD_INSTANCE_ID, REFERENCE.MODID);

    public static final RegistryObject<Codec<? extends TaskUnlocker>> LORD_LEVEL_UNLOCKER = TASK_UNLOCKER.register("lord_level", () -> LordLvlUnlocker.CODEC);
    public static final RegistryObject<Codec<? extends TaskUnlocker>> LEVEL_UNLOCKER = TASK_UNLOCKER.register("level", () -> LvlUnlocker.CODEC);
    public static final RegistryObject<Codec<? extends TaskUnlocker>> PARENT_UNLOCKER = TASK_UNLOCKER.register("parent", () -> ParentUnlocker.CODEC);

    public static final RegistryObject<Codec<ItemReward>> ITEM_REWARD = TASK_REWARDS.register("item", () -> ItemReward.CODEC);
    public static final RegistryObject<Codec<LordLevelReward>> LORD_LEVEL_REWARD = TASK_REWARDS.register("lord_level", () -> LordLevelReward.CODEC);
    public static final RegistryObject<Codec<RefinementItemReward>> REFINEMENT_REWARD = TASK_REWARDS.register("refinement", () -> RefinementItemReward.CODEC);
    public static final RegistryObject<Codec<ConsumerReward>> CONSUMER = TASK_REWARDS.register("consumer", () -> ConsumerReward.CODEC);

    public static final RegistryObject<Codec<ItemReward.Instance>> ITEM_REWARD_INSTANCE = TASK_REWARD_INSTANCES.register("item", () -> ItemReward.Instance.CODEC);
    public static final RegistryObject<Codec<LordLevelReward>> LORD_LEVEL_REWARD_INSTANCE = TASK_REWARD_INSTANCES.register("lord_level", () -> LordLevelReward.CODEC);
    public static final RegistryObject<Codec<ConsumerReward>> CONSUMER_INSTANCE = TASK_REWARD_INSTANCES.register("consumer", () -> ConsumerReward.CODEC);

    public static final RegistryObject<Codec<? extends TaskRequirement.Requirement<?>>> BOOLEAN_REQUIREMENT = TASK_REQUIREMENTS.register("boolean", () -> BooleanRequirement.CODEC);
    public static final RegistryObject<Codec<? extends TaskRequirement.Requirement<?>>> ENTITY_REQUIREMENT = TASK_REQUIREMENTS.register("entity", () -> EntityRequirement.CODEC);
    public static final RegistryObject<Codec<? extends TaskRequirement.Requirement<?>>> ENTITY_TYPE_REQUIREMENT = TASK_REQUIREMENTS.register("entity_type", () -> EntityTypeRequirement.CODEC);
    public static final RegistryObject<Codec<? extends TaskRequirement.Requirement<?>>> ITEM_REQUIREMENT = TASK_REQUIREMENTS.register("item", () -> ItemRequirement.CODEC);
    public static final RegistryObject<Codec<? extends TaskRequirement.Requirement<?>>> STAT_REQUIREMENT = TASK_REQUIREMENTS.register("stat", () -> StatRequirement.CODEC);

    //vampire
    //  lord tasks
    public static final ResourceKey<Task> VAMPIRE_LORD_1 = key("vampire_lord1");
    public static final ResourceKey<Task> VAMPIRE_LORD_2 = key("vampire_lord2");
    public static final ResourceKey<Task> VAMPIRE_LORD_3 = key("vampire_lord3");
    public static final ResourceKey<Task> VAMPIRE_LORD_4 = key("vampire_lord4");
    public static final ResourceKey<Task> VAMPIRE_LORD_5 = key("vampire_lord5");
    //  other
    public static final ResourceKey<Task> VAMPIRE_MINION_BINDING = key("vampire_minion_binding");
    public static final ResourceKey<Task> VAMPIRE_MINION_UPGRADE_SIMPLE = key("vampire_minion_upgrade_simple");
    public static final ResourceKey<Task> VAMPIRE_MINION_UPGRADE_ENHANCED = key("vampire_minion_upgrade_enhanced");
    public static final ResourceKey<Task> VAMPIRE_MINION_UPGRADE_SPECIAL = key("vampire_minion_upgrade_special");
    public static final ResourceKey<Task> RANDOM_REFINEMENT_1 = key("random_refinement1");
    public static final ResourceKey<Task> RANDOM_REFINEMENT_2 = key("random_refinement2");
    public static final ResourceKey<Task> RANDOM_REFINEMENT_3 = key("random_refinement3");
    public static final ResourceKey<Task> RANDOM_RARE_REFINEMENT = key("random_rare_refinement");
    public static final ResourceKey<Task> FIRE_RESISTANCE_1 = key("fire_resistance1");
    public static final ResourceKey<Task> FIRE_RESISTANCE_2 = key("fire_resistance2");
    public static final ResourceKey<Task> FEEDING_ADAPTER = key("feeding_adapter");
    public static final ResourceKey<Task> V_INFECT_1 = key("v_infect1");
    public static final ResourceKey<Task> V_INFECT_2 = key("v_infect2");
    public static final ResourceKey<Task> V_INFECT_3 = key("v_infect3");
    public static final ResourceKey<Task> V_CAPTURE_1 = key("v_capture1");
    public static final ResourceKey<Task> V_CAPTURE_2 = key("v_capture2");
    public static final ResourceKey<Task> V_KILL_1 = key("v_kill1");
    public static final ResourceKey<Task> V_KILL_2 = key("v_kill2");

    //hunter
    //  lord tasks
    public static final ResourceKey<Task> HUNTER_LORD_1 = key("hunter_lord1");
    public static final ResourceKey<Task> HUNTER_LORD_2 = key("hunter_lord2");
    public static final ResourceKey<Task> HUNTER_LORD_3 = key("hunter_lord3");
    public static final ResourceKey<Task> HUNTER_LORD_4 = key("hunter_lord4");
    public static final ResourceKey<Task> HUNTER_LORD_5 = key("hunter_lord5");

    //  other
    public static final ResourceKey<Task> HUNTER_MINION_EQUIPMENT = key("hunter_minion_equipment");
    public static final ResourceKey<Task> HUNTER_MINION_UPGRADE_SIMPLE = key("hunter_minion_upgrade_simple");
    public static final ResourceKey<Task> HUNTER_MINION_UPGRADE_ENHANCED = key("hunter_minion_upgrade_enhanced");
    public static final ResourceKey<Task> HUNTER_MINION_UPGRADE_SPECIAL = key("hunter_minion_upgrade_special");
    public static final ResourceKey<Task> H_KILL_1 = key("h_kill1");
    public static final ResourceKey<Task> H_KILL_2 = key("h_kill2");
    public static final ResourceKey<Task> H_CAPTURE_1 = key("h_capture1");

    //all factions
    public static final ResourceKey<Task> OBLIVION_POTION = key("oblivion_potion");
    public static final ResourceKey<Task> OBLIVION_POTION_PURE_BLOOD_1 = key("oblivion_potion_pure_blood_1");
    public static final ResourceKey<Task> OBLIVION_POTION_PURE_BLOOD_2 = key("oblivion_potion_pure_blood_2");
    public static final ResourceKey<Task> OBLIVION_POTION_PURE_BLOOD_3 = key("oblivion_potion_pure_blood_3");
    public static final ResourceKey<Task> OBLIVION_POTION_PURE_BLOOD_4 = key("oblivion_potion_pure_blood_4");
    public static final ResourceKey<Task> OBLIVION_POTION_PURE_BLOOD_5 = key("oblivion_potion_pure_blood_5");
    public static final ResourceKey<Task> BREAK_BONES_1 = key("break_bones1");
    public static final ResourceKey<Task> BREAK_BONES_2 = key("break_bones2");
    public static final ResourceKey<Task> BREAK_BONES_3 = key("break_bones3");
    public static final ResourceKey<Task> BREAK_BONES_4 = key("break_bones4");
    public static final ResourceKey<Task> TOTEM_TOP = key("totem_top");

    public static void register(IEventBus bus) {
        TASK_UNLOCKER.register(bus);
        TASK_REWARDS.register(bus);
        TASK_REQUIREMENTS.register(bus);
        TASK_REWARD_INSTANCES.register(bus);
    }

    private static ResourceKey<Task> key(String path) {
        return ResourceKey.create(VampirismRegistries.TASK_ID, new ResourceLocation(REFERENCE.MODID, path));
    }

    public static void createTasks(BootstapContext<Task> context) {
        context.register(FEEDING_ADAPTER, TaskBuilder.builder(FEEDING_ADAPTER).defaultTitle().unlockedBy(new LvlUnlocker(4)).addRequirement(ModTags.Entities.ADVANCED_HUNTER, 10).addRequirement(new ItemStack(Items.GOLD_INGOT, 5)).setReward(new ItemStack(ModItems.FEEDING_ADAPTER.get())).build());
        context.register(VAMPIRE_LORD_1, TaskBuilder.builder(VAMPIRE_LORD_1).defaultTitle().unlockedBy(new LvlUnlocker(VReference.VAMPIRE_FACTION.getHighestReachableLevel())).addRequirement(ModStats.infected_creatures, 25).addRequirement(new ItemStack(ModItems.PURE_BLOOD_4.get(), 5)).addRequirement(new ItemStack(Items.GOLD_INGOT, 32)).addRequirement(ModStats.win_village_capture, 3).setReward(new LordLevelReward(1, Component.translatable("task.vampirism.vampire_lord1.reward"))).build());
        context.register(VAMPIRE_LORD_2, TaskBuilder.builder(VAMPIRE_LORD_2).defaultTitle().unlockedBy(new LordLvlUnlocker(1, true)).addRequirement(ModTags.Entities.HUNTER, 30).addRequirement(new ItemStack(ModItems.PURE_BLOOD_4.get(), 5)).addRequirement(new ItemStack(Items.GOLD_INGOT, 48)).setReward(new LordLevelReward(2)).build());
        context.register(VAMPIRE_LORD_3, TaskBuilder.builder(VAMPIRE_LORD_3).defaultTitle().unlockedBy(new LordLvlUnlocker(2, true)).addRequirement(ModTags.Entities.HUNTER, 30).addRequirement(new ItemStack(ModItems.PURE_BLOOD_4.get(), 5)).addRequirement(new ItemStack(Items.GOLD_INGOT, 48)).setReward(new LordLevelReward(3)).build());
        context.register(VAMPIRE_LORD_4, TaskBuilder.builder(VAMPIRE_LORD_4).defaultTitle().unlockedBy(new LordLvlUnlocker(3, true)).addRequirement(ModTags.Entities.ADVANCED_HUNTER, 5).addRequirement(new ItemStack(ModItems.PURE_BLOOD_4.get(), 10)).addRequirement(new ItemStack(Items.GOLD_INGOT, 64)).setReward(new LordLevelReward(4)).build());
        context.register(VAMPIRE_LORD_5, TaskBuilder.builder(VAMPIRE_LORD_5).defaultTitle().unlockedBy(new LordLvlUnlocker(4, true)).addRequirement(ModStats.infected_creatures, 50).addRequirement(new ItemStack(ModItems.PURE_BLOOD_4.get(), 20)).addRequirement(new ItemStack(Items.GOLD_INGOT, 64)).addRequirement(ModStats.capture_village, 6).setReward(new LordLevelReward(5)).build());
        context.register(HUNTER_LORD_1, TaskBuilder.builder(HUNTER_LORD_1).defaultTitle().unlockedBy(new LvlUnlocker(VReference.HUNTER_FACTION.getHighestReachableLevel())).addRequirement(ModTags.Entities.VAMPIRE, 50).addRequirement(new ItemStack(Items.GOLD_INGOT, 32)).addRequirement(ModStats.win_village_capture, 3).setReward(new LordLevelReward(1, Component.translatable("task.vampirism.hunter_lord1.reward"))).build());
        context.register(HUNTER_LORD_2, TaskBuilder.builder(HUNTER_LORD_2).defaultTitle().unlockedBy(new LordLvlUnlocker(1, true)).addRequirement(ModTags.Entities.VAMPIRE, 50).addRequirement(new ItemStack(Items.GOLD_INGOT, 32)).setReward(new LordLevelReward(2)).build());
        context.register(HUNTER_LORD_3, TaskBuilder.builder(HUNTER_LORD_3).defaultTitle().unlockedBy(new LordLvlUnlocker(2, true)).addRequirement(ModTags.Entities.VAMPIRE, 50).addRequirement(new ItemStack(Items.GOLD_INGOT, 32)).setReward(new LordLevelReward(3)).build());
        context.register(HUNTER_LORD_4, TaskBuilder.builder(HUNTER_LORD_4).defaultTitle().unlockedBy(new LordLvlUnlocker(3, true)).addRequirement(ModTags.Entities.VAMPIRE, 75).addRequirement(new ItemStack(Items.GOLD_INGOT, 64)).setReward(new LordLevelReward(4)).build());
        context.register(HUNTER_LORD_5, TaskBuilder.builder(HUNTER_LORD_5).defaultTitle().unlockedBy(new LordLvlUnlocker(4, true)).addRequirement(ModTags.Entities.VAMPIRE, 100).addRequirement(new ItemStack(Items.GOLD_INGOT, 64)).addRequirement(ModStats.capture_village, 6).setReward(new LordLevelReward(5)).build());
        context.register(OBLIVION_POTION, TaskBuilder.builder(OBLIVION_POTION).defaultTitle().addRequirement(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement(new ItemStack(ModItems.VAMPIRE_BLOOD_BOTTLE.get())).setReward(new ItemStack(ModItems.OBLIVION_POTION.get())).build());
        context.register(FIRE_RESISTANCE_1, TaskBuilder.builder(FIRE_RESISTANCE_1).defaultTitle().addRequirement(new ItemStack(Items.MAGMA_CREAM, 3)).addRequirement(ModTags.Entities.HUNTER, 10).setReward(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.VAMPIRE_FIRE_RESISTANCE.get())).build());
        context.register(FIRE_RESISTANCE_2, TaskBuilder.builder(FIRE_RESISTANCE_2).defaultTitle().unlockedBy(new LvlUnlocker(7)).addRequirement(new ItemStack(Items.MAGMA_CREAM, 5)).addRequirement(ModTags.Entities.HUNTER, 15).setReward(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LONG_VAMPIRE_FIRE_RESISTANCE.get())).build());
        context.register(VAMPIRE_MINION_BINDING, TaskBuilder.builder(VAMPIRE_MINION_BINDING).defaultTitle().unlockedBy(new LordLvlUnlocker(1)).addRequirement(ModTags.Entities.ADVANCED_HUNTER, 4).addRequirement(ModEntities.VAMPIRE_BARON.get(), 5).addRequirement(new ItemStack(Items.GOLD_INGOT, 32)).setReward(new ItemStack(ModItems.VAMPIRE_MINION_BINDING.get())).build());
        context.register(VAMPIRE_MINION_UPGRADE_SIMPLE, TaskBuilder.builder(VAMPIRE_MINION_UPGRADE_SIMPLE).defaultTitle().unlockedBy(new LordLvlUnlocker(2)).addRequirement(ModTags.Entities.ADVANCED_HUNTER, 8).addRequirement(new ItemStack(Items.GOLD_BLOCK, 16)).setReward(new ItemStack(ModItems.VAMPIRE_MINION_UPGRADE_SIMPLE.get())).build());
        context.register(VAMPIRE_MINION_UPGRADE_ENHANCED, TaskBuilder.builder(VAMPIRE_MINION_UPGRADE_ENHANCED).defaultTitle().unlockedBy(new LordLvlUnlocker(3)).addRequirement(ModEntities.VAMPIRE_BARON.get(), 10).addRequirement(new ItemStack(ModItems.HUMAN_HEART.get(), 32)).addRequirement(new ItemStack(ModItems.VAMPIRE_BOOK.get())).addRequirement(new ItemStack(Items.DIAMOND_BLOCK, 3)).setReward(new ItemStack(ModItems.VAMPIRE_MINION_UPGRADE_ENHANCED.get())).build());
        context.register(VAMPIRE_MINION_UPGRADE_SPECIAL, TaskBuilder.builder(VAMPIRE_MINION_UPGRADE_SPECIAL).defaultTitle().unlockedBy(new LordLvlUnlocker(5)).addRequirement(ModEntities.VAMPIRE_BARON.get(), 20).addRequirement(new ItemStack(ModItems.HUMAN_HEART.get(), 64)).addRequirement(new ItemStack(ModItems.VAMPIRE_BOOK.get())).addRequirement(new ItemStack(Items.DIAMOND_BLOCK, 8)).setReward(new ItemStack(ModItems.VAMPIRE_MINION_UPGRADE_SPECIAL.get())).build());
        context.register(HUNTER_MINION_EQUIPMENT, TaskBuilder.builder(HUNTER_MINION_EQUIPMENT).defaultTitle().unlockedBy(new LordLvlUnlocker(1)).addRequirement(ModTags.Entities.ADVANCED_VAMPIRE, 4).addRequirement(ModEntities.VAMPIRE_BARON.get(), 5).addRequirement(new ItemStack(Items.GOLD_INGOT, 32)).setReward(new ItemStack(ModItems.HUNTER_MINION_EQUIPMENT.get())).build());
        context.register(HUNTER_MINION_UPGRADE_SIMPLE, TaskBuilder.builder(HUNTER_MINION_UPGRADE_SIMPLE).defaultTitle().unlockedBy(new LordLvlUnlocker(2)).addRequirement(ModTags.Entities.ADVANCED_VAMPIRE, 8).addRequirement(new ItemStack(Items.GOLD_BLOCK, 16)).setReward(new ItemStack(ModItems.HUNTER_MINION_UPGRADE_SIMPLE.get())).build());
        context.register(HUNTER_MINION_UPGRADE_ENHANCED, TaskBuilder.builder(HUNTER_MINION_UPGRADE_ENHANCED).defaultTitle().unlockedBy(new LordLvlUnlocker(3)).addRequirement(ModEntities.VAMPIRE_BARON.get(), 10).addRequirement(new ItemStack(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 16)).addRequirement(new ItemStack(ModItems.VAMPIRE_BOOK.get())).addRequirement(new ItemStack(Items.DIAMOND_BLOCK, 3)).setReward(new ItemStack(ModItems.HUNTER_MINION_UPGRADE_ENHANCED.get())).build());
        context.register(HUNTER_MINION_UPGRADE_SPECIAL, TaskBuilder.builder(HUNTER_MINION_UPGRADE_SPECIAL).defaultTitle().unlockedBy(new LordLvlUnlocker(5)).addRequirement(ModEntities.VAMPIRE_BARON.get(), 20).addRequirement(new ItemStack(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 32)).addRequirement(new ItemStack(ModItems.VAMPIRE_BOOK.get())).addRequirement(new ItemStack(Items.DIAMOND_BLOCK, 8)).setReward(new ItemStack(ModItems.HUNTER_MINION_UPGRADE_SPECIAL.get())).build());
        context.register(V_INFECT_1, TaskBuilder.builder(V_INFECT_1).defaultTitle().addRequirement(ModStats.infected_creatures, 20).setReward(new ItemStack(Items.GOLD_INGOT, 5)).build());
        context.register(V_INFECT_2, TaskBuilder.builder(V_INFECT_2).defaultTitle().addRequirement(ModStats.infected_creatures, 25).setReward(new ItemStack(Items.GOLD_INGOT, 15)).build());
        context.register(V_INFECT_3, TaskBuilder.builder(V_INFECT_3).defaultTitle().addRequirement(ModStats.infected_creatures, 15).setReward(new ItemStack(Items.IRON_INGOT, 5)).build());
        context.register(V_CAPTURE_1, TaskBuilder.builder(V_CAPTURE_1).defaultTitle().addRequirement(ModStats.capture_village, 1).setReward(new ItemStack(Items.EMERALD, 10)).build());
        context.register(V_CAPTURE_2, TaskBuilder.builder(V_CAPTURE_2).defaultTitle().addRequirement(ModStats.capture_village, 1).setReward(new ItemStack(Items.EMERALD, 5)).build());
        context.register(V_KILL_1, TaskBuilder.builder(V_KILL_1).defaultTitle().addRequirement(ModTags.Entities.HUNTER, 10).setReward(new ItemStack(ModItems.HUMAN_HEART.get(), 5)).build());
        context.register(V_KILL_2, TaskBuilder.builder(V_KILL_2).defaultTitle().addRequirement(ModTags.Entities.ADVANCED_HUNTER, 4).setReward(new ItemStack(ModItems.HUMAN_HEART.get(), 8)).build());
        context.register(H_KILL_1, TaskBuilder.builder(H_KILL_1).defaultTitle().addRequirement(ModTags.Entities.VAMPIRE, 20).setReward(new ItemStack(Items.DIAMOND, 2)).build());
        context.register(H_KILL_2, TaskBuilder.builder(H_KILL_2).defaultTitle().addRequirement(ModTags.Entities.VAMPIRE, 15).setReward(new ItemStack(Items.DIAMOND, 2)).build());
        context.register(H_CAPTURE_1, TaskBuilder.builder(H_CAPTURE_1).defaultTitle().addRequirement(ModStats.capture_village, 2).setReward(new ItemStack(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 10)).build());
        context.register(BREAK_BONES_1, TaskBuilder.builder(BREAK_BONES_1).defaultTitle().addRequirement(EntityType.SKELETON, 20).setReward(new ItemStack(Items.CHAINMAIL_CHESTPLATE)).build());
        context.register(BREAK_BONES_2, TaskBuilder.builder(BREAK_BONES_2).defaultTitle().addRequirement(EntityType.SKELETON, 14).setReward(new ItemStack(Items.CHAINMAIL_LEGGINGS)).build());
        context.register(BREAK_BONES_3, TaskBuilder.builder(BREAK_BONES_3).defaultTitle().addRequirement(EntityType.SKELETON, 10).setReward(new ItemStack(Items.CHAINMAIL_BOOTS)).build());
        context.register(BREAK_BONES_4, TaskBuilder.builder(BREAK_BONES_4).defaultTitle().addRequirement(EntityType.SKELETON, 10).setReward(new ItemStack(Items.CHAINMAIL_HELMET)).build());
        context.register(TOTEM_TOP, TaskBuilder.builder(TOTEM_TOP).defaultTitle().unlockedBy(new LvlUnlocker(5)).addRequirement(new ItemStack(Items.OBSIDIAN, 32)).addRequirement(new ItemStack(Items.DIAMOND, 1)).addRequirement(ModTags.Entities.ZOMBIES, 32).setReward(new ItemStack(ModBlocks.TOTEM_TOP_CRAFTED.get())).build());
        context.register(OBLIVION_POTION_PURE_BLOOD_1, TaskBuilder.builder(OBLIVION_POTION_PURE_BLOOD_1).defaultTitle().unlockedBy(new LvlUnlocker(1, 3)).addRequirement(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement(new ItemStack(ModItems.PURE_BLOOD_0.get())).setReward(new ItemStack(ModItems.OBLIVION_POTION.get())).build());
        context.register(OBLIVION_POTION_PURE_BLOOD_2, TaskBuilder.builder(OBLIVION_POTION_PURE_BLOOD_2).defaultTitle().unlockedBy(new LvlUnlocker(4, 6)).addRequirement(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement(new ItemStack(ModItems.PURE_BLOOD_1.get())).setReward(new ItemStack(ModItems.OBLIVION_POTION.get())).build());
        context.register(OBLIVION_POTION_PURE_BLOOD_3, TaskBuilder.builder(OBLIVION_POTION_PURE_BLOOD_3).defaultTitle().unlockedBy(new LvlUnlocker(7, 9)).addRequirement(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement(new ItemStack(ModItems.PURE_BLOOD_2.get())).setReward(new ItemStack(ModItems.OBLIVION_POTION.get())).build());
        context.register(OBLIVION_POTION_PURE_BLOOD_4, TaskBuilder.builder(OBLIVION_POTION_PURE_BLOOD_4).defaultTitle().unlockedBy(new LvlUnlocker(10, 12)).addRequirement(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement(new ItemStack(ModItems.PURE_BLOOD_3.get())).setReward(new ItemStack(ModItems.OBLIVION_POTION.get())).build());
        context.register(OBLIVION_POTION_PURE_BLOOD_5, TaskBuilder.builder(OBLIVION_POTION_PURE_BLOOD_5).defaultTitle().unlockedBy(new LvlUnlocker(13, 14)).addRequirement(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement(new ItemStack(ModItems.PURE_BLOOD_4.get())).setReward(new ItemStack(ModItems.OBLIVION_POTION.get())).build());
        context.register(RANDOM_REFINEMENT_1, TaskBuilder.builder(RANDOM_REFINEMENT_1).defaultTitle().addRequirement(ModTags.Entities.ADVANCED_HUNTER, 10).addRequirement(new ItemStack(Items.GOLD_INGOT, 2)).setReward(new RefinementItemReward(VReference.VAMPIRE_FACTION)).build());
        context.register(RANDOM_REFINEMENT_2, TaskBuilder.builder(RANDOM_REFINEMENT_2).defaultTitle().addRequirement(ModEntities.VAMPIRE_BARON.get(), 3).addRequirement(new ItemStack(Items.GOLD_INGOT, 2)).setReward(new RefinementItemReward(VReference.VAMPIRE_FACTION)).build());
        context.register(RANDOM_REFINEMENT_3, TaskBuilder.builder(RANDOM_REFINEMENT_3).defaultTitle().addRequirement(Stats.TRADED_WITH_VILLAGER, 15).addRequirement(new ItemStack(Items.GOLD_INGOT, 2)).setReward(new RefinementItemReward(VReference.VAMPIRE_FACTION, ModItems.RING::get, null)).build());
        context.register(RANDOM_RARE_REFINEMENT, TaskBuilder.builder(RANDOM_RARE_REFINEMENT).defaultTitle().addRequirement(Stats.RAID_WIN, 1).setReward(new RefinementItemReward(VReference.VAMPIRE_FACTION, IRefinementSet.Rarity.RARE)).build());
    }
}
