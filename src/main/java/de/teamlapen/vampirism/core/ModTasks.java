package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.player.tasks.TaskBuilder;
import de.teamlapen.vampirism.player.tasks.reward.LordLevelReward;
import de.teamlapen.vampirism.player.tasks.reward.RefinementItemReward;
import de.teamlapen.vampirism.player.tasks.unlock.LordLvlUnlocker;
import de.teamlapen.vampirism.player.tasks.unlock.LvlUnlocker;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ModTasks {
    public static final DeferredRegister<Task> TASKS = DeferredRegister.create(VampirismRegistries.TASK_ID, REFERENCE.MODID);

    public static final RegistryObject<Task> FEEDING_ADAPTER = TASKS.register("feeding_adapter", () -> vampire().unlockedBy(new LvlUnlocker(4)).addRequirement("advanced_hunter", ModTags.Entities.ADVANCED_HUNTER, 10).addRequirement("item", () -> new ItemStack(Items.GOLD_INGOT, 5)).setReward(() -> new ItemStack(ModItems.FEEDING_ADAPTER.get())).build());

    //Vampire Lord
    public static final RegistryObject<Task> VAMPIRE_LORD1 = TASKS.register("vampire_lord1", () -> vampire().unlockedBy(new LvlUnlocker(VReference.VAMPIRE_FACTION.getHighestReachableLevel())).addRequirement("infect", ModStats.infected_creatures, 25).addRequirement("pure_blood", () -> new ItemStack(ModItems.PURE_BLOOD_4.get(), 5)).addRequirement("gold", () -> new ItemStack(Items.GOLD_INGOT, 32)).addRequirement("village", ModStats.win_village_capture, 3).setUnique().setReward(new LordLevelReward(1)).build());
    public static final RegistryObject<Task> VAMPIRE_LORD2 = TASKS.register("vampire_lord2", () -> vampire().unlockedBy(new LordLvlUnlocker(1, true)).addRequirement("hunter", ModTags.Entities.HUNTER, 30).addRequirement("pure_blood", () -> new ItemStack(ModItems.PURE_BLOOD_4.get(), 5)).addRequirement("gold", () -> new ItemStack(Items.GOLD_INGOT, 48)).setUnique().setReward(new LordLevelReward(2)).build());
    public static final RegistryObject<Task> VAMPIRE_LORD3 = TASKS.register("vampire_lord3", () -> vampire().unlockedBy(new LordLvlUnlocker(2, true)).addRequirement("hunter", ModTags.Entities.HUNTER, 30).addRequirement("pure_blood", () -> new ItemStack(ModItems.PURE_BLOOD_4.get(), 5)).addRequirement("gold", () -> new ItemStack(Items.GOLD_INGOT, 48)).setUnique().setReward(new LordLevelReward(3)).build());
    public static final RegistryObject<Task> VAMPIRE_LORD4 = TASKS.register("vampire_lord4", () -> vampire().unlockedBy(new LordLvlUnlocker(3, true)).addRequirement("hunter", ModTags.Entities.ADVANCED_HUNTER, 5).addRequirement("pure_blood", () -> new ItemStack(ModItems.PURE_BLOOD_4.get(), 10)).addRequirement("gold", () -> new ItemStack(Items.GOLD_INGOT, 64)).setUnique().setReward(new LordLevelReward(4)).build());
    public static final RegistryObject<Task> VAMPIRE_LORD5 = TASKS.register("vampire_lord5", () -> vampire().unlockedBy(new LordLvlUnlocker(4, true)).addRequirement("infect", ModStats.infected_creatures, 50).addRequirement("pure_blood", () -> new ItemStack(ModItems.PURE_BLOOD_4.get(), 20)).addRequirement("gold", () -> new ItemStack(Items.GOLD_INGOT, 64)).addRequirement("village", ModStats.capture_village, 6).setUnique().setReward(new LordLevelReward(5)).build());

    //Hunter Lord
    public static final RegistryObject<Task> HUNTER_LORD1 = TASKS.register("hunter_lord1", () -> hunter().unlockedBy(new LvlUnlocker(VReference.HUNTER_FACTION.getHighestReachableLevel())).addRequirement("vampire", ModTags.Entities.VAMPIRE, 50).addRequirement("gold", () -> new ItemStack(Items.GOLD_INGOT, 32)).addRequirement("village", ModStats.win_village_capture, 3).setUnique().setReward(new LordLevelReward(1)).build());
    public static final RegistryObject<Task> HUNTER_LORD2 = TASKS.register("hunter_lord2", () -> hunter().unlockedBy(new LordLvlUnlocker(1, true)).addRequirement("vampire", ModTags.Entities.VAMPIRE, 50).addRequirement("gold", () -> new ItemStack(Items.GOLD_INGOT, 32)).setUnique().setReward(new LordLevelReward(2)).build());
    public static final RegistryObject<Task> HUNTER_LORD3 = TASKS.register("hunter_lord3", () -> hunter().unlockedBy(new LordLvlUnlocker(2, true)).addRequirement("vampire", ModTags.Entities.VAMPIRE, 50).addRequirement("gold", () -> new ItemStack(Items.GOLD_INGOT, 32)).setUnique().setReward(new LordLevelReward(3)).build());
    public static final RegistryObject<Task> HUNTER_LORD4 = TASKS.register("hunter_lord4", () -> hunter().unlockedBy(new LordLvlUnlocker(3, true)).addRequirement("vampire", ModTags.Entities.VAMPIRE, 75).addRequirement("gold", () -> new ItemStack(Items.GOLD_INGOT, 64)).setUnique().setReward(new LordLevelReward(4)).build());
    public static final RegistryObject<Task> HUNTER_LORD5 = TASKS.register("hunter_lord5", () -> hunter().unlockedBy(new LordLvlUnlocker(4, true)).addRequirement("vampire", ModTags.Entities.VAMPIRE, 100).addRequirement("gold", () -> new ItemStack(Items.GOLD_INGOT, 64)).addRequirement("village", ModStats.capture_village, 6).setUnique().setReward(new LordLevelReward(5)).build());

    public static final RegistryObject<Task> OBLIVION_POTION = TASKS.register("oblivion_potion", () -> TaskBuilder.builder().addRequirement("poison", () -> PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement("vampire_blood", () -> new ItemStack(ModItems.VAMPIRE_BLOOD_BOTTLE.get())).setReward(() -> new ItemStack(ModItems.OBLIVION_POTION.get())).build());


    public static void register(IEventBus bus) {
        TASKS.register(bus);
    }

    static {
        //Vampire gadgets
        TASKS.register("fire_resistance1", () -> vampire().addRequirement("magma", () -> new ItemStack(Items.MAGMA_CREAM, 3)).addRequirement("hunter", ModTags.Entities.HUNTER, 10).setReward(() -> PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.VAMPIRE_FIRE_RESISTANCE.get())).build());
        TASKS.register("fire_resistance2", () -> vampire().unlockedBy(new LvlUnlocker(7)).addRequirement("magma", () -> new ItemStack(Items.MAGMA_CREAM, 5)).addRequirement("hunter", ModTags.Entities.HUNTER, 15).setReward(() -> PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LONG_VAMPIRE_FIRE_RESISTANCE.get())).build());

        //Vampire Minion
        TASKS.register("vampire_minion_binding", () -> vampire().unlockedBy(new LordLvlUnlocker(1)).addRequirement("advanced_hunter", ModTags.Entities.ADVANCED_HUNTER, 4).addRequirement("baron", ModEntities.VAMPIRE_BARON.get(), 5).addRequirement("gold", () -> new ItemStack(Items.GOLD_INGOT, 32)).setReward(() -> new ItemStack(ModItems.VAMPIRE_MINION_BINDING.get())).build());
        TASKS.register("vampire_minion_upgrade_simple", () -> vampire().unlockedBy(new LordLvlUnlocker(2)).addRequirement("advanced_hunter", ModTags.Entities.ADVANCED_HUNTER, 8).addRequirement("gold", () -> new ItemStack(Items.GOLD_BLOCK, 16)).setReward(() -> new ItemStack(ModItems.VAMPIRE_MINION_UPGRADE_SIMPLE.get())).build());
        TASKS.register("vampire_minion_upgrade_enhanced", () -> vampire().unlockedBy(new LordLvlUnlocker(3)).addRequirement("baron", ModEntities.VAMPIRE_BARON.get(), 10).addRequirement("human_heart", () -> new ItemStack(ModItems.HUMAN_HEART.get(), 32)).addRequirement("book", () -> new ItemStack(ModItems.VAMPIRE_BOOK.get())).addRequirement("diamond", () -> new ItemStack(Items.DIAMOND_BLOCK, 3)).setReward(() -> new ItemStack(ModItems.VAMPIRE_MINION_UPGRADE_ENHANCED.get())).build());
        TASKS.register("vampire_minion_upgrade_special", () -> vampire().unlockedBy(new LordLvlUnlocker(5)).addRequirement("baron", ModEntities.VAMPIRE_BARON.get(), 20).addRequirement("human_heart", () -> new ItemStack(ModItems.HUMAN_HEART.get(), 64)).addRequirement("book", () -> new ItemStack(ModItems.VAMPIRE_BOOK.get())).addRequirement("diamond", () -> new ItemStack(Items.DIAMOND_BLOCK, 8)).setReward(() -> new ItemStack(ModItems.VAMPIRE_MINION_UPGRADE_SPECIAL.get())).build());
        //Hunter Minion
        TASKS.register("hunter_minion_equipment", () -> hunter().unlockedBy(new LordLvlUnlocker(1)).addRequirement("advanced_vampire", ModTags.Entities.ADVANCED_VAMPIRE, 4).addRequirement("baron", ModEntities.VAMPIRE_BARON.get(), 5).addRequirement("gold", () -> new ItemStack(Items.GOLD_INGOT, 32)).setReward(() -> new ItemStack(ModItems.HUNTER_MINION_EQUIPMENT.get())).build());
        TASKS.register("hunter_minion_upgrade_simple", () -> hunter().unlockedBy(new LordLvlUnlocker(2)).addRequirement("advanced_vampire", ModTags.Entities.ADVANCED_VAMPIRE, 8).addRequirement("gold", () -> new ItemStack(Items.GOLD_BLOCK, 16)).setReward(() -> new ItemStack(ModItems.HUNTER_MINION_UPGRADE_SIMPLE.get())).build());
        TASKS.register("hunter_minion_upgrade_enhanced", () -> hunter().unlockedBy(new LordLvlUnlocker(3)).addRequirement("baron", ModEntities.VAMPIRE_BARON.get(), 10).addRequirement("vampire_blood", () -> new ItemStack(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 16)).addRequirement("book", () -> new ItemStack(ModItems.VAMPIRE_BOOK.get())).addRequirement("diamond", () -> new ItemStack(Items.DIAMOND_BLOCK, 3)).setReward(() -> new ItemStack(ModItems.HUNTER_MINION_UPGRADE_ENHANCED.get())).build());
        TASKS.register("hunter_minion_upgrade_special", () -> hunter().unlockedBy(new LordLvlUnlocker(5)).addRequirement("baron", ModEntities.VAMPIRE_BARON.get(), 20).addRequirement("vampire_blood", () -> new ItemStack(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 32)).addRequirement("book", () -> new ItemStack(ModItems.VAMPIRE_BOOK.get())).addRequirement("diamond", () -> new ItemStack(Items.DIAMOND_BLOCK, 8)).setReward(() -> new ItemStack(ModItems.HUNTER_MINION_UPGRADE_SPECIAL.get())).build());

        //Generic tasks
        TASKS.register("v_infect1", () -> vampire().addRequirement("infect", ModStats.infected_creatures, 20).setReward(() -> new ItemStack(Items.GOLD_INGOT, 5)).build());
        TASKS.register("v_infect2", () -> vampire().addRequirement("infect", ModStats.infected_creatures, 25).setReward(() -> new ItemStack(Items.GOLD_INGOT, 15)).build());
        TASKS.register("v_infect3", () -> vampire().addRequirement("infect", ModStats.infected_creatures, 15).setReward(() -> new ItemStack(Items.IRON_INGOT, 5)).build());
        TASKS.register("v_capture1", () -> vampire().addRequirement("village", ModStats.capture_village, 1).setReward(() -> new ItemStack(Items.EMERALD, 10)).build());
        TASKS.register("v_capture2", () -> vampire().addRequirement("village", ModStats.capture_village, 1).setReward(() -> new ItemStack(Items.EMERALD, 5)).build());
        TASKS.register("v_kill1", () -> vampire().addRequirement("hunter", ModTags.Entities.HUNTER, 10).setReward(() -> new ItemStack(ModItems.HUMAN_HEART.get(), 5)).build());
        TASKS.register("v_kill2", () -> vampire().addRequirement("hunter", ModTags.Entities.ADVANCED_HUNTER, 4).setReward(() -> new ItemStack(ModItems.HUMAN_HEART.get(), 8)).build());

        TASKS.register("h_kill1", () -> hunter().addRequirement("vampire", ModTags.Entities.VAMPIRE, 20).setReward(() -> new ItemStack(Items.DIAMOND, 2)).build());
        TASKS.register("h_kill2", () -> hunter().addRequirement("vampire", ModTags.Entities.VAMPIRE, 15).setReward(() -> new ItemStack(Items.DIAMOND, 2)).build());
        TASKS.register("h_capture1", () -> hunter().addRequirement("village", ModStats.capture_village, 2).setReward(() -> new ItemStack(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 10)).build());
        TASKS.register("break_bones1", () -> TaskBuilder.builder().addRequirement("skeleton", EntityType.SKELETON, 20).setReward(() -> new ItemStack(Items.CHAINMAIL_CHESTPLATE)).build());
        TASKS.register("break_bones2", () -> TaskBuilder.builder().addRequirement("skeleton", EntityType.SKELETON, 14).setReward(() -> new ItemStack(Items.CHAINMAIL_LEGGINGS)).build());
        TASKS.register("break_bones3", () -> TaskBuilder.builder().addRequirement("skeleton", EntityType.SKELETON, 10).setReward(() -> new ItemStack(Items.CHAINMAIL_BOOTS)).build());
        TASKS.register("break_bones4", () -> TaskBuilder.builder().addRequirement("skeleton", EntityType.SKELETON, 10).setReward(() -> new ItemStack(Items.CHAINMAIL_HELMET)).build());
        TASKS.register("totem_top", () -> TaskBuilder.builder().unlockedBy(new LvlUnlocker(5)).addRequirement("obsidian", () -> new ItemStack(Items.OBSIDIAN, 32)).addRequirement("diamond", () -> new ItemStack(Items.DIAMOND, 1)).addRequirement("zombie", ModTags.Entities.ZOMBIES, 32).setReward(() -> new ItemStack(ModBlocks.TOTEM_TOP_CRAFTED.get())).build());

        TASKS.register("oblivion_potion_pure_blood_1", () -> TaskBuilder.builder().unlockedBy(new LvlUnlocker(1, 3)).addRequirement("poison", () -> PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement("vampire_blood", () -> new ItemStack(ModItems.PURE_BLOOD_0.get())).setReward(() -> new ItemStack(ModItems.OBLIVION_POTION.get())).build());
        TASKS.register("oblivion_potion_pure_blood_2", () -> TaskBuilder.builder().unlockedBy(new LvlUnlocker(4, 6)).addRequirement("poison", () -> PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement("vampire_blood", () -> new ItemStack(ModItems.PURE_BLOOD_1.get())).setReward(() -> new ItemStack(ModItems.OBLIVION_POTION.get())).build());
        TASKS.register("oblivion_potion_pure_blood_3", () -> TaskBuilder.builder().unlockedBy(new LvlUnlocker(7, 9)).addRequirement("poison", () -> PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement("vampire_blood", () -> new ItemStack(ModItems.PURE_BLOOD_2.get())).setReward(() -> new ItemStack(ModItems.OBLIVION_POTION.get())).build());
        TASKS.register("oblivion_potion_pure_blood_4", () -> TaskBuilder.builder().unlockedBy(new LvlUnlocker(10, 12)).addRequirement("poison", () -> PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement("vampire_blood", () -> new ItemStack(ModItems.PURE_BLOOD_3.get())).setReward(() -> new ItemStack(ModItems.OBLIVION_POTION.get())).build());
        TASKS.register("oblivion_potion_pure_blood_5", () -> TaskBuilder.builder().unlockedBy(new LvlUnlocker(13, 14)).addRequirement("poison", () -> PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)).addRequirement("vampire_blood", () -> new ItemStack(ModItems.PURE_BLOOD_4.get())).setReward(() -> new ItemStack(ModItems.OBLIVION_POTION.get())).build());

        //vampire refinement items
        TASKS.register("random_refinement1", () -> vampire().addRequirement("hunter", ModTags.Entities.ADVANCED_HUNTER, 10).addRequirement("gold", () -> new ItemStack(Items.GOLD_INGOT, 2)).setReward(new RefinementItemReward(VReference.VAMPIRE_FACTION)).build());
        TASKS.register("random_refinement2", () -> vampire().addRequirement("barons", ModEntities.VAMPIRE_BARON.get(), 3).addRequirement("gold", () -> new ItemStack(Items.GOLD_INGOT, 2)).setReward(new RefinementItemReward(VReference.VAMPIRE_FACTION)).build());
        TASKS.register("random_refinement3", () -> vampire().addRequirement("trades", Stats.TRADED_WITH_VILLAGER, 15).addRequirement("gold", () -> new ItemStack(Items.GOLD_INGOT, 2)).setReward(new RefinementItemReward(VReference.VAMPIRE_FACTION)).build());
        TASKS.register("random_rare_refinement", () -> vampire().addRequirement("raid", Stats.RAID_WIN, 1).setReward(new RefinementItemReward(VReference.VAMPIRE_FACTION, IRefinementSet.Rarity.RARE)).build());

    }

    @NotNull
    private static TaskBuilder vampire() {
        return TaskBuilder.builder().withFaction(() -> VReference.VAMPIRE_FACTION);
    }

    @NotNull
    private static TaskBuilder hunter() {
        return TaskBuilder.builder().withFaction(() -> VReference.HUNTER_FACTION);
    }
}
