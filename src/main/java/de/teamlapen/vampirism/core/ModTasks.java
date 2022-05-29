package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.player.tasks.TaskBuilder;
import de.teamlapen.vampirism.player.tasks.reward.LordLevelReward;
import de.teamlapen.vampirism.player.tasks.reward.RefinementItemReward;
import de.teamlapen.vampirism.player.tasks.unlock.LordLvlUnlocker;
import de.teamlapen.vampirism.player.tasks.unlock.LvlUnlocker;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.stats.Stats;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;
import static de.teamlapen.vampirism.player.tasks.TaskBuilder.builder;

@SuppressWarnings("unused")
@ObjectHolder(REFERENCE.MODID)
public class ModTasks {

    public static final Task feeding_adapter = getNull();

    public static final Task vampire_lord1 = getNull();
    public static final Task vampire_lord2 = getNull();
    public static final Task vampire_lord3 = getNull();
    public static final Task vampire_lord4 = getNull();
    public static final Task vampire_lord5 = getNull();

    public static final Task hunter_lord1 = getNull();
    public static final Task hunter_lord2 = getNull();
    public static final Task hunter_lord3 = getNull();
    public static final Task hunter_lord4 = getNull();
    public static final Task hunter_lord5 = getNull();

    public static final Task oblivion_potion = getNull();


    public static void registerTasks(IForgeRegistry<Task> registry) {
        //Vampire gadgets
        registry.register(vampire().unlockedBy(new LvlUnlocker(4)).addRequirement("advanced_hunter", ModTags.Entities.ADVANCED_HUNTER, 10).addRequirement("item", new ItemStack(Items.GOLD_INGOT, 5)).setReward(new ItemStack(ModItems.FEEDING_ADAPTER.get())).build("feeding_adapter"));
        registry.register(vampire().addRequirement("magma", new ItemStack(Items.MAGMA_CREAM, 3)).addRequirement("hunter", ModTags.Entities.HUNTER, 10).setReward(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.vampire_fire_resistance)).build("fire_resistance1"));
        registry.register(vampire().unlockedBy(new LvlUnlocker(7)).addRequirement("magma", new ItemStack(Items.MAGMA_CREAM, 5)).addRequirement("hunter", ModTags.Entities.HUNTER, 15).setReward(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.long_vampire_fire_resistance)).build("fire_resistance2"));

        //Vampire Minion
        registry.register(vampire().unlockedBy(new LordLvlUnlocker(1)).addRequirement("advanced_hunter", ModTags.Entities.ADVANCED_HUNTER, 4).addRequirement("baron", ModEntities.vampire_baron, 5).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 32)).setReward(new ItemStack(ModItems.VAMPIRE_MINION_BINDING.get())).build("vampire_minion_binding"));
        registry.register(vampire().unlockedBy(new LordLvlUnlocker(2)).addRequirement("advanced_hunter", ModTags.Entities.ADVANCED_HUNTER, 8).addRequirement("gold", new ItemStack(Items.GOLD_BLOCK, 16)).setReward(new ItemStack(ModItems.VAMPIRE_MINION_UPGRADE_SIMPLE.get())).build("vampire_minion_upgrade_simple"));
        registry.register(vampire().unlockedBy(new LordLvlUnlocker(3)).addRequirement("baron", ModEntities.vampire_baron, 10).addRequirement("human_heart", new ItemStack(ModItems.HUMAN_HEART.get(), 32)).addRequirement("book", new ItemStack(ModItems.VAMPIRE_BOOK.get())).addRequirement("diamond", new ItemStack(Items.DIAMOND_BLOCK, 3)).setReward(new ItemStack(ModItems.VAMPIRE_MINION_UPGRADE_ENHANCED.get())).build("vampire_minion_upgrade_enhanced"));
        registry.register(vampire().unlockedBy(new LordLvlUnlocker(5)).addRequirement("baron", ModEntities.vampire_baron, 20).addRequirement("human_heart", new ItemStack(ModItems.HUMAN_HEART.get(), 64)).addRequirement("book", new ItemStack(ModItems.VAMPIRE_BOOK.get())).addRequirement("diamond", new ItemStack(Items.DIAMOND_BLOCK, 8)).setReward(new ItemStack(ModItems.VAMPIRE_MINION_UPGRADE_SPECIAL.get())).build("vampire_minion_upgrade_special"));
        //Hunter Minion
        registry.register(hunter().unlockedBy(new LordLvlUnlocker(1)).addRequirement("advanced_vampire", ModTags.Entities.ADVANCED_VAMPIRE, 4).addRequirement("baron", ModEntities.vampire_baron, 5).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 32)).setReward(new ItemStack(ModItems.HUNTER_MINION_EQUIPMENT.get())).build("hunter_minion_equipment"));
        registry.register(hunter().unlockedBy(new LordLvlUnlocker(2)).addRequirement("advanced_vampire", ModTags.Entities.ADVANCED_VAMPIRE, 8).addRequirement("gold", new ItemStack(Items.GOLD_BLOCK, 16)).setReward(new ItemStack(ModItems.HUNTER_MINION_UPGRADE_SIMPLE.get())).build("hunter_minion_upgrade_simple"));
        registry.register(hunter().unlockedBy(new LordLvlUnlocker(3)).addRequirement("baron", ModEntities.vampire_baron, 10).addRequirement("vampire_blood", new ItemStack(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 16)).addRequirement("book", new ItemStack(ModItems.VAMPIRE_BOOK.get())).addRequirement("diamond", new ItemStack(Items.DIAMOND_BLOCK, 3)).setReward(new ItemStack(ModItems.HUNTER_MINION_UPGRADE_ENHANCED.get())).build("hunter_minion_upgrade_enhanced"));
        registry.register(hunter().unlockedBy(new LordLvlUnlocker(5)).addRequirement("baron", ModEntities.vampire_baron, 20).addRequirement("vampire_blood", new ItemStack(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 32)).addRequirement("book", new ItemStack(ModItems.VAMPIRE_BOOK.get())).addRequirement("diamond", new ItemStack(Items.DIAMOND_BLOCK, 8)).setReward(new ItemStack(ModItems.HUNTER_MINION_UPGRADE_SPECIAL.get())).build("hunter_minion_upgrade_special"));

        //Vampire Lord
        registry.register(vampire().unlockedBy(new LvlUnlocker(VReference.VAMPIRE_FACTION.getHighestReachableLevel())).addRequirement("infect", ModStats.infected_creatures, 25).addRequirement("pure_blood", new ItemStack(ModItems.PURE_BLOOD_4.get(), 5)).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 32)).addRequirement("village", ModStats.win_village_capture, 3).setUnique().setReward(new LordLevelReward(1)).build("vampire_lord1"));
        registry.register(vampire().unlockedBy(new LordLvlUnlocker(1, true)).addRequirement("hunter", ModTags.Entities.HUNTER, 30).addRequirement("pure_blood", new ItemStack(ModItems.PURE_BLOOD_4.get(), 5)).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 48)).setUnique().setReward(new LordLevelReward(2)).build("vampire_lord2"));
        registry.register(vampire().unlockedBy(new LordLvlUnlocker(2, true)).addRequirement("hunter", ModTags.Entities.HUNTER, 30).addRequirement("pure_blood", new ItemStack(ModItems.PURE_BLOOD_4.get(), 5)).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 48)).setUnique().setReward(new LordLevelReward(3)).build("vampire_lord3"));
        registry.register(vampire().unlockedBy(new LordLvlUnlocker(3, true)).addRequirement("hunter", ModTags.Entities.ADVANCED_HUNTER, 5).addRequirement("pure_blood", new ItemStack(ModItems.PURE_BLOOD_4.get(), 10)).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 64)).setUnique().setReward(new LordLevelReward(4)).build("vampire_lord4"));
        registry.register(vampire().unlockedBy(new LordLvlUnlocker(4, true)).addRequirement("infect", ModStats.infected_creatures, 50).addRequirement("pure_blood", new ItemStack(ModItems.PURE_BLOOD_4.get(), 20)).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 64)).addRequirement("village", ModStats.capture_village, 6).setUnique().setReward(new LordLevelReward(5)).build("vampire_lord5"));
        //Hunter Lord
        registry.register(hunter().unlockedBy(new LvlUnlocker(VReference.HUNTER_FACTION.getHighestReachableLevel())).addRequirement("vampire", ModTags.Entities.VAMPIRE, 50).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 32)).addRequirement("village", ModStats.win_village_capture, 3).setUnique().setReward(new LordLevelReward(1)).build("hunter_lord1"));
        registry.register(hunter().unlockedBy(new LordLvlUnlocker(1, true)).addRequirement("vampire", ModTags.Entities.VAMPIRE, 50).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 32)).setUnique().setReward(new LordLevelReward(2)).build("hunter_lord2"));
        registry.register(hunter().unlockedBy(new LordLvlUnlocker(2, true)).addRequirement("vampire", ModTags.Entities.VAMPIRE, 50).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 32)).setUnique().setReward(new LordLevelReward(3)).build("hunter_lord3"));
        registry.register(hunter().unlockedBy(new LordLvlUnlocker(3, true)).addRequirement("vampire", ModTags.Entities.VAMPIRE, 75).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 64)).setUnique().setReward(new LordLevelReward(4)).build("hunter_lord4"));
        registry.register(hunter().unlockedBy(new LordLvlUnlocker(4, true)).addRequirement("vampire", ModTags.Entities.VAMPIRE, 100).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 64)).addRequirement("village", ModStats.capture_village, 6).setUnique().setReward(new LordLevelReward(5)).build("hunter_lord5"));

        //Generic tasks
        registry.register(vampire().addRequirement("infect", ModStats.infected_creatures, 20).setReward(new ItemStack(Items.GOLD_INGOT, 5)).build("v_infect1"));
        registry.register(vampire().addRequirement("infect", ModStats.infected_creatures, 25).setReward(new ItemStack(Items.GOLD_INGOT, 15)).build("v_infect2"));
        registry.register(vampire().addRequirement("infect", ModStats.infected_creatures, 15).setReward(new ItemStack(Items.IRON_INGOT, 5)).build("v_infect3"));
        registry.register(vampire().addRequirement("village", ModStats.capture_village, 1).setReward(new ItemStack(Items.EMERALD, 10)).build("v_capture1"));
        registry.register(vampire().addRequirement("village", ModStats.capture_village, 1).setReward(new ItemStack(Items.EMERALD, 5)).build("v_capture2"));
        registry.register(vampire().addRequirement("hunter", ModTags.Entities.HUNTER, 10).setReward(new ItemStack(ModItems.HUMAN_HEART.get(), 5)).build("v_kill1"));
        registry.register(vampire().addRequirement("hunter", ModTags.Entities.ADVANCED_HUNTER, 4).setReward(new ItemStack(ModItems.HUMAN_HEART.get(), 8)).build("v_kill2"));

        registry.register(hunter().addRequirement("vampire", ModTags.Entities.VAMPIRE, 20).setReward(new ItemStack(Items.DIAMOND, 2)).build("h_kill1"));
        registry.register(hunter().addRequirement("vampire", ModTags.Entities.VAMPIRE, 15).setReward(new ItemStack(Items.DIAMOND, 2)).build("h_kill2"));
        registry.register(hunter().addRequirement("village", ModStats.capture_village, 2).setReward(new ItemStack(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 10)).build("h_capture1"));
        registry.register(builder().addRequirement("skeleton", EntityType.SKELETON, 20).setReward(new ItemStack(Items.CHAINMAIL_CHESTPLATE)).build("break_bones1"));
        registry.register(builder().addRequirement("skeleton", EntityType.SKELETON, 14).setReward(new ItemStack(Items.CHAINMAIL_LEGGINGS)).build("break_bones2"));
        registry.register(builder().addRequirement("skeleton", EntityType.SKELETON, 10).setReward(new ItemStack(Items.CHAINMAIL_BOOTS)).build("break_bones3"));
        registry.register(builder().addRequirement("skeleton", EntityType.SKELETON, 10).setReward(new ItemStack(Items.CHAINMAIL_HELMET)).build("break_bones4"));
        registry.register(builder().unlockedBy(new LvlUnlocker(5)).addRequirement("obsidian", new ItemStack(Items.OBSIDIAN, 32)).addRequirement("diamond", new ItemStack(Items.DIAMOND, 1)).addRequirement("zombie", ModTags.Entities.ZOMBIES, 32).setReward(new ItemStack(ModBlocks.TOTEM_TOP_CRAFTED.get())).build("totem_top"));

        registry.register(builder().unlockedBy(new LvlUnlocker(1, 3)).addRequirement("poison", PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement("vampire_blood", new ItemStack(ModItems.PURE_BLOOD_0.get())).setReward(new ItemStack(ModItems.OBLIVION_POTION.get())).build("oblivion_potion_pure_blood_1"));
        registry.register(builder().unlockedBy(new LvlUnlocker(4, 6)).addRequirement("poison", PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement("vampire_blood", new ItemStack(ModItems.PURE_BLOOD_1.get())).setReward(new ItemStack(ModItems.OBLIVION_POTION.get())).build("oblivion_potion_pure_blood_2"));
        registry.register(builder().unlockedBy(new LvlUnlocker(7, 9)).addRequirement("poison", PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement("vampire_blood", new ItemStack(ModItems.PURE_BLOOD_2.get())).setReward(new ItemStack(ModItems.OBLIVION_POTION.get())).build("oblivion_potion_pure_blood_3"));
        registry.register(builder().unlockedBy(new LvlUnlocker(10, 12)).addRequirement("poison", PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement("vampire_blood", new ItemStack(ModItems.PURE_BLOOD_3.get())).setReward(new ItemStack(ModItems.OBLIVION_POTION.get())).build("oblivion_potion_pure_blood_4"));
        registry.register(builder().unlockedBy(new LvlUnlocker(13, 14)).addRequirement("poison", PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement("vampire_blood", new ItemStack(ModItems.PURE_BLOOD_4.get())).setReward(new ItemStack(ModItems.OBLIVION_POTION.get())).build("oblivion_potion_pure_blood_5"));

        registry.register(builder().addRequirement("poison", PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement("vampire_blood", new ItemStack(ModItems.VAMPIRE_BLOOD_BOTTLE.get())).setReward(new ItemStack(ModItems.OBLIVION_POTION.get())).build("oblivion_potion"));

        //vampire refinement items
        registry.register(vampire().addRequirement("hunter", ModTags.Entities.ADVANCED_HUNTER, 10).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 2)).setReward(new RefinementItemReward(VReference.VAMPIRE_FACTION)).build("random_refinement1"));
        registry.register(vampire().addRequirement("barons", ModEntities.vampire_baron, 3).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 2)).setReward(new RefinementItemReward(VReference.VAMPIRE_FACTION)).build("random_refinement2"));
        registry.register(vampire().addRequirement("trades", Stats.TRADED_WITH_VILLAGER, 15).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 2)).setReward(new RefinementItemReward(VReference.VAMPIRE_FACTION)).build("random_refinement3"));
        registry.register(vampire().addRequirement("raid", Stats.RAID_WIN, 1).setReward(new RefinementItemReward(VReference.VAMPIRE_FACTION, IRefinementSet.Rarity.RARE)).build("random_rare_refinement"));
    }

    @Nonnull
    private static TaskBuilder vampire() {
        return builder().withFaction(() -> VReference.VAMPIRE_FACTION);
    }

    @Nonnull
    private static TaskBuilder hunter() {
        return builder().withFaction(() -> VReference.HUNTER_FACTION);
    }
}
