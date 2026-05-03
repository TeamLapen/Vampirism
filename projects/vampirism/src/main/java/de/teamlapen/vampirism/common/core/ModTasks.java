package de.teamlapen.vampirism.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.factions.tasks.Task;
import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.faction.common.core.FactionBlocks;
import de.teamlapen.faction.common.core.FactionItems;
import de.teamlapen.faction.common.core.FactionStats;
import de.teamlapen.faction.common.factions.tasks.TaskBuilder;
import de.teamlapen.faction.common.factions.tasks.reward.LordLevelReward;
import de.teamlapen.faction.common.factions.tasks.reward.RefinementItemReward;
import de.teamlapen.faction.common.factions.tasks.unlock.LordLvlUnlocker;
import de.teamlapen.faction.common.factions.tasks.unlock.LvlUnlocker;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.tags.ModEntityTags;
import de.teamlapen.vampirism.common.util.ItemDataUtils;
import net.jpountz.util.SafeUtils;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.IEventBus;

public class ModTasks {

    // Vampire

    // Lord Tasks
    public static final ResourceKey<Task> VAMPIRE_LORD_1 = key("vampire_lord1");
    public static final ResourceKey<Task> VAMPIRE_LORD_2 = key("vampire_lord2");
    public static final ResourceKey<Task> VAMPIRE_LORD_3 = key("vampire_lord3");
    public static final ResourceKey<Task> VAMPIRE_LORD_4 = key("vampire_lord4");
    public static final ResourceKey<Task> VAMPIRE_LORD_5 = key("vampire_lord5");
    // Minion Equipment
    public static final ResourceKey<Task> VAMPIRE_MINION_BINDING = key("vampire_minion_binding");
    public static final ResourceKey<Task> VAMPIRE_MINION_UPGRADE_SIMPLE = key("vampire_minion_upgrade_simple");
    public static final ResourceKey<Task> VAMPIRE_MINION_UPGRADE_ENHANCED = key("vampire_minion_upgrade_enhanced");
    public static final ResourceKey<Task> VAMPIRE_MINION_UPGRADE_SPECIAL = key("vampire_minion_upgrade_special");
    // Other
    public static final ResourceKey<Task> RANDOM_REFINEMENT_1 = key("random_refinement1");
    public static final ResourceKey<Task> RANDOM_REFINEMENT_2 = key("random_refinement2");
    public static final ResourceKey<Task> RANDOM_REFINEMENT_3 = key("random_refinement3");
    public static final ResourceKey<Task> RANDOM_RARE_REFINEMENT = key("random_rare_refinement");
    public static final ResourceKey<Task> FEEDING_ADAPTER = key("feeding_adapter");
    public static final ResourceKey<Task> FIRE_RESISTANCE_1 = key("fire_resistance1");
    public static final ResourceKey<Task> FIRE_RESISTANCE_2 = key("fire_resistance2");
    public static final ResourceKey<Task> V_INFECT_1 = key("v_infect1");
    public static final ResourceKey<Task> V_INFECT_2 = key("v_infect2");
    public static final ResourceKey<Task> V_INFECT_3 = key("v_infect3");
    public static final ResourceKey<Task> V_CAPTURE_1 = key("v_capture1");
    public static final ResourceKey<Task> V_CAPTURE_2 = key("v_capture2");
    public static final ResourceKey<Task> V_KILL_1 = key("v_kill1");
    public static final ResourceKey<Task> V_KILL_2 = key("v_kill2");

    // Hunter

    // Lord tasks
    public static final ResourceKey<Task> HUNTER_LORD_1 = key("hunter_lord1");
    public static final ResourceKey<Task> HUNTER_LORD_2 = key("hunter_lord2");
    public static final ResourceKey<Task> HUNTER_LORD_3 = key("hunter_lord3");
    public static final ResourceKey<Task> HUNTER_LORD_4 = key("hunter_lord4");
    public static final ResourceKey<Task> HUNTER_LORD_5 = key("hunter_lord5");
    // Minion Equipment
    public static final ResourceKey<Task> HUNTER_MINION_EQUIPMENT = key("hunter_minion_equipment");
    public static final ResourceKey<Task> HUNTER_MINION_UPGRADE_SIMPLE = key("hunter_minion_upgrade_simple");
    public static final ResourceKey<Task> HUNTER_MINION_UPGRADE_ENHANCED = key("hunter_minion_upgrade_enhanced");
    public static final ResourceKey<Task> HUNTER_MINION_UPGRADE_SPECIAL = key("hunter_minion_upgrade_special");
    // Other
    public static final ResourceKey<Task> H_CAPTURE_1 = key("h_capture1");
    public static final ResourceKey<Task> H_KILL_1 = key("h_kill1");
    public static final ResourceKey<Task> H_KILL_2 = key("h_kill2");

    // Neutral
    public static final ResourceKey<Task> TOTEM_TOP = key("totem_top");
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

    public static void register(IEventBus bus) {
    }

    private static ResourceKey<Task> key(String path) {
        return ResourceKey.create(FactionRegistries.Keys.TASK, VIdentifier.mod(path));
    }

    static void createTasks(BootstrapContext<Task> context) {
        HolderGetter<IFaction<?>> factions = context.lookup(FactionRegistries.Keys.FACTION);
        TaskBuilder.builder().unlockedBy(new LvlUnlocker(ModFactions.VAMPIRE.value().getHighestReachableLevel())).addRequirement(ModStats.INFECTED_CREATURES.get(), 25).addRequirement(new ItemStackTemplate(ModItems.PURE_BLOOD_4, 5)).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 32)).addRequirement(FactionStats.WIN_VILLAGE_CAPTURE.get(), 3).setReward(new de.teamlapen.faction.common.factions.tasks.reward.LordLevelReward(1, Component.translatable("task.vampirism.vampire_lord1.reward"))).build(context::register, VAMPIRE_LORD_1);
        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(1, true)).addRequirement(ModEntityTags.HUNTER, 30).addRequirement(new ItemStackTemplate(ModItems.PURE_BLOOD_4.get(), 5)).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 48)).setReward(new de.teamlapen.faction.common.factions.tasks.reward.LordLevelReward(2)).build(context::register, VAMPIRE_LORD_2);
        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(2, true)).addRequirement(ModEntityTags.HUNTER, 30).addRequirement(new ItemStackTemplate(ModItems.PURE_BLOOD_4.get(), 5)).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 48)).setReward(new de.teamlapen.faction.common.factions.tasks.reward.LordLevelReward(3)).build(context::register, VAMPIRE_LORD_3);
        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(3, true)).addRequirement(ModEntityTags.ADVANCED_HUNTER, 5).addRequirement(new ItemStackTemplate(ModItems.PURE_BLOOD_4.get(), 10)).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 64)).setReward(new de.teamlapen.faction.common.factions.tasks.reward.LordLevelReward(4)).build(context::register, VAMPIRE_LORD_4);
        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(4, true)).addRequirement(ModStats.INFECTED_CREATURES.get(), 50).addRequirement(new ItemStackTemplate(ModItems.PURE_BLOOD_4.get(), 20)).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 64)).addRequirement(FactionStats.CAPTURE_VILLAGE.get(), 6).setReward(new de.teamlapen.faction.common.factions.tasks.reward.LordLevelReward(5)).build(context::register, VAMPIRE_LORD_5);

        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(1)).addRequirement(ModEntityTags.ADVANCED_HUNTER, 4).addRequirement(ModEntities.VAMPIRE_BARON.get(), 5).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 32)).setReward(new ItemStackTemplate(ModItems.VAMPIRE_MINION_BINDING.get())).build(context::register, VAMPIRE_MINION_BINDING);
        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(2)).addRequirement(ModEntityTags.ADVANCED_HUNTER, 8).addRequirement(new ItemStackTemplate(Items.GOLD_BLOCK, 16)).setReward(new ItemStackTemplate(ModItems.VAMPIRE_MINION_UPGRADE_SIMPLE.get())).build(context::register, VAMPIRE_MINION_UPGRADE_SIMPLE);
        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(3)).addRequirement(ModEntities.VAMPIRE_BARON.get(), 10).addRequirement(new ItemStackTemplate(ModItems.HUMAN_HEART.get(), 32)).addRequirement(new ItemStackTemplate(ModItems.VAMPIRE_BOOK.get())).addRequirement(new ItemStackTemplate(Items.DIAMOND_BLOCK, 3)).setReward(new ItemStackTemplate(ModItems.VAMPIRE_MINION_UPGRADE_ENHANCED.get())).build(context::register, VAMPIRE_MINION_UPGRADE_ENHANCED);
        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(5)).addRequirement(ModEntities.VAMPIRE_BARON.get(), 20).addRequirement(new ItemStackTemplate(ModItems.HUMAN_HEART.get(), 64)).addRequirement(new ItemStackTemplate(ModItems.VAMPIRE_BOOK.get())).addRequirement(new ItemStackTemplate(Items.DIAMOND_BLOCK, 8)).setReward(new ItemStackTemplate(ModItems.VAMPIRE_MINION_UPGRADE_SPECIAL.get())).build(context::register, VAMPIRE_MINION_UPGRADE_SPECIAL);

        TaskBuilder.builder().addRequirement(ModEntityTags.ADVANCED_HUNTER, 10).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 2)).setReward(new RefinementItemReward(SafeCast.cast(factions.getOrThrow(VampirismTags.Factions.IS_VAMPIRE)))).build(context::register, RANDOM_REFINEMENT_1);
        TaskBuilder.builder().addRequirement(ModEntities.VAMPIRE_BARON.get(), 3).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 2)).setReward(new RefinementItemReward(SafeCast.cast(factions.getOrThrow(VampirismTags.Factions.IS_VAMPIRE)))).build(context::register, RANDOM_REFINEMENT_2);
        TaskBuilder.builder().addRequirement(Stats.TRADED_WITH_VILLAGER, 15).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 2)).setReward(new RefinementItemReward(SafeCast.cast(factions.getOrThrow(VampirismTags.Factions.IS_VAMPIRE)))).build(context::register, RANDOM_REFINEMENT_3);
        TaskBuilder.builder().addRequirement(Stats.RAID_WIN, 1).setReward(new RefinementItemReward(SafeCast.cast(factions.getOrThrow(VampirismTags.Factions.IS_VAMPIRE)), IRefinementSet.Rarity.RARE)).build(context::register, RANDOM_RARE_REFINEMENT);
        TaskBuilder.builder().unlockedBy(new LvlUnlocker(4)).addRequirement(ModEntityTags.ADVANCED_HUNTER, 10).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 5)).setReward(new ItemStackTemplate(ModItems.FEEDING_ADAPTER.get())).build(context::register, FEEDING_ADAPTER);
        TaskBuilder.builder().addRequirement(new ItemStackTemplate(Items.MAGMA_CREAM, 3)).addRequirement(ModEntityTags.HUNTER, 10).setReward(ItemDataUtils.template(ModPotions.VAMPIRE_FIRE_RESISTANCE)).build(context::register, FIRE_RESISTANCE_1);
        TaskBuilder.builder().unlockedBy(new LvlUnlocker(7)).addRequirement(new ItemStackTemplate(Items.MAGMA_CREAM, 5)).addRequirement(ModEntityTags.HUNTER, 15).setReward(ItemDataUtils.template(ModPotions.LONG_VAMPIRE_FIRE_RESISTANCE)).build(context::register, FIRE_RESISTANCE_2);
        TaskBuilder.builder().addRequirement(ModStats.INFECTED_CREATURES.get(), 20).setReward(new ItemStackTemplate(Items.GOLD_INGOT, 5)).build(context::register, V_INFECT_1);
        TaskBuilder.builder().addRequirement(ModStats.INFECTED_CREATURES.get(), 25).setReward(new ItemStackTemplate(Items.GOLD_INGOT, 15)).build(context::register, V_INFECT_2);
        TaskBuilder.builder().addRequirement(ModStats.INFECTED_CREATURES.get(), 15).setReward(new ItemStackTemplate(Items.IRON_INGOT, 5)).build(context::register, V_INFECT_3);
        TaskBuilder.builder().addRequirement(FactionStats.CAPTURE_VILLAGE.get(), 1).setReward(new ItemStackTemplate(Items.EMERALD, 10)).build(context::register, V_CAPTURE_1);
        TaskBuilder.builder().addRequirement(FactionStats.CAPTURE_VILLAGE.get(), 1).setReward(new ItemStackTemplate(Items.EMERALD, 5)).build(context::register, V_CAPTURE_2);
        TaskBuilder.builder().addRequirement(ModEntityTags.HUNTER, 10).setReward(new ItemStackTemplate(ModItems.HUMAN_HEART.get(), 5)).build(context::register, V_KILL_1);
        TaskBuilder.builder().addRequirement(ModEntityTags.ADVANCED_HUNTER, 4).setReward(new ItemStackTemplate(ModItems.HUMAN_HEART.get(), 8)).build(context::register, V_KILL_2);

        TaskBuilder.builder().unlockedBy(new LvlUnlocker(ModFactions.HUNTER.value().getHighestReachableLevel())).addRequirement(ModEntityTags.VAMPIRE, 50).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 32)).addRequirement(FactionStats.WIN_VILLAGE_CAPTURE.get(), 3).setReward(new de.teamlapen.faction.common.factions.tasks.reward.LordLevelReward(1, Component.translatable("task.vampirism.hunter_lord1.reward"))).build(context::register, HUNTER_LORD_1);
        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(1, true)).addRequirement(ModEntityTags.VAMPIRE, 50).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 32)).setReward(new de.teamlapen.faction.common.factions.tasks.reward.LordLevelReward(2)).build(context::register, HUNTER_LORD_2);
        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(2, true)).addRequirement(ModEntityTags.VAMPIRE, 50).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 32)).setReward(new de.teamlapen.faction.common.factions.tasks.reward.LordLevelReward(3)).build(context::register, HUNTER_LORD_3);
        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(3, true)).addRequirement(ModEntityTags.VAMPIRE, 75).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 64)).setReward(new de.teamlapen.faction.common.factions.tasks.reward.LordLevelReward(4)).build(context::register, HUNTER_LORD_4);
        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(4, true)).addRequirement(ModEntityTags.VAMPIRE, 100).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 64)).addRequirement(FactionStats.CAPTURE_VILLAGE.get(), 6).setReward(new LordLevelReward(5)).build(context::register, HUNTER_LORD_5);

        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(1)).addRequirement(ModEntityTags.ADVANCED_VAMPIRE, 4).addRequirement(ModEntities.VAMPIRE_BARON.get(), 5).addRequirement(new ItemStackTemplate(Items.GOLD_INGOT, 32)).setReward(new ItemStackTemplate(ModItems.HUNTER_MINION_EQUIPMENT.get())).build(context::register, HUNTER_MINION_EQUIPMENT);
        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(2)).addRequirement(ModEntityTags.ADVANCED_VAMPIRE, 8).addRequirement(new ItemStackTemplate(Items.GOLD_BLOCK, 16)).setReward(new ItemStackTemplate(ModItems.HUNTER_MINION_UPGRADE_SIMPLE.get())).build(context::register, HUNTER_MINION_UPGRADE_SIMPLE);
        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(3)).addRequirement(ModEntities.VAMPIRE_BARON.get(), 10).addRequirement(new ItemStackTemplate(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 16)).addRequirement(new ItemStackTemplate(ModItems.VAMPIRE_BOOK.get())).addRequirement(new ItemStackTemplate(Items.DIAMOND_BLOCK, 3)).setReward(new ItemStackTemplate(ModItems.HUNTER_MINION_UPGRADE_ENHANCED.get())).build(context::register, HUNTER_MINION_UPGRADE_ENHANCED);
        TaskBuilder.builder().unlockedBy(new LordLvlUnlocker(5)).addRequirement(ModEntities.VAMPIRE_BARON.get(), 20).addRequirement(new ItemStackTemplate(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 32)).addRequirement(new ItemStackTemplate(ModItems.VAMPIRE_BOOK.get())).addRequirement(new ItemStackTemplate(Items.DIAMOND_BLOCK, 8)).setReward(new ItemStackTemplate(ModItems.HUNTER_MINION_UPGRADE_SPECIAL.get())).build(context::register, HUNTER_MINION_UPGRADE_SPECIAL);

        TaskBuilder.builder().addRequirement(ModEntityTags.VAMPIRE, 20).setReward(new ItemStackTemplate(Items.DIAMOND, 2)).build(context::register, H_KILL_1);
        TaskBuilder.builder().addRequirement(ModEntityTags.VAMPIRE, 15).setReward(new ItemStackTemplate(Items.DIAMOND, 2)).build(context::register, H_KILL_2);
        TaskBuilder.builder().addRequirement(FactionStats.CAPTURE_VILLAGE.get(), 2).setReward(new ItemStackTemplate(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 10)).build(context::register, H_CAPTURE_1);

        TaskBuilder.builder().unlockedBy(new LvlUnlocker(5)).addRequirement(new ItemStackTemplate(Items.OBSIDIAN, 32)).addRequirement(new ItemStackTemplate(Items.DIAMOND, 1)).addRequirement(ModEntityTags.ZOMBIES, 32).setReward(new ItemStackTemplate(FactionBlocks.TOTEM_TOP_CRAFTED.asItem())).build(context::register, TOTEM_TOP);
        TaskBuilder.builder().addRequirement(ItemDataUtils.template(Potions.POISON)).addRequirement(new ItemStackTemplate(ModItems.VAMPIRE_BLOOD_BOTTLE.get())).setReward(new ItemStackTemplate(FactionItems.OBLIVION_POTION.get())).build(context::register, OBLIVION_POTION);
        TaskBuilder.builder().unlockedBy(new LvlUnlocker(1, 3)).addRequirement(ItemDataUtils.template(Potions.POISON)).addRequirement(new ItemStackTemplate(ModItems.PURE_BLOOD_0.get())).setReward(new ItemStackTemplate(FactionItems.OBLIVION_POTION.get())).build(context::register, OBLIVION_POTION_PURE_BLOOD_1);
        TaskBuilder.builder().unlockedBy(new LvlUnlocker(4, 6)).addRequirement(ItemDataUtils.template(Potions.POISON)).addRequirement(new ItemStackTemplate(ModItems.PURE_BLOOD_1.get())).setReward(new ItemStackTemplate(FactionItems.OBLIVION_POTION.get())).build(context::register, OBLIVION_POTION_PURE_BLOOD_2);
        TaskBuilder.builder().unlockedBy(new LvlUnlocker(7, 9)).addRequirement(ItemDataUtils.template(Potions.POISON)).addRequirement(new ItemStackTemplate(ModItems.PURE_BLOOD_2.get())).setReward(new ItemStackTemplate(FactionItems.OBLIVION_POTION.get())).build(context::register, OBLIVION_POTION_PURE_BLOOD_3);
        TaskBuilder.builder().unlockedBy(new LvlUnlocker(10, 12)).addRequirement(ItemDataUtils.template(Potions.POISON)).addRequirement(new ItemStackTemplate(ModItems.PURE_BLOOD_3.get())).setReward(new ItemStackTemplate(FactionItems.OBLIVION_POTION.get())).build(context::register, OBLIVION_POTION_PURE_BLOOD_4);
        TaskBuilder.builder().unlockedBy(new LvlUnlocker(13, 14)).addRequirement(ItemDataUtils.template(Potions.POISON)).addRequirement(new ItemStackTemplate(ModItems.PURE_BLOOD_4.get())).setReward(new ItemStackTemplate(FactionItems.OBLIVION_POTION.get())).build(context::register, OBLIVION_POTION_PURE_BLOOD_5);
        TaskBuilder.builder().addRequirement(EntityType.SKELETON, 20).setReward(new ItemStackTemplate(Items.CHAINMAIL_CHESTPLATE)).build(context::register, BREAK_BONES_1);
        TaskBuilder.builder().addRequirement(EntityType.SKELETON, 14).setReward(new ItemStackTemplate(Items.CHAINMAIL_LEGGINGS)).build(context::register, BREAK_BONES_2);
        TaskBuilder.builder().addRequirement(EntityType.SKELETON, 10).setReward(new ItemStackTemplate(Items.CHAINMAIL_BOOTS)).build(context::register, BREAK_BONES_3);
        TaskBuilder.builder().addRequirement(EntityType.SKELETON, 10).setReward(new ItemStackTemplate(Items.CHAINMAIL_HELMET)).build(context::register, BREAK_BONES_4);
    }
}
