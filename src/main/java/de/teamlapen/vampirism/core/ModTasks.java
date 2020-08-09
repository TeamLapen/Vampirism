package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.tasks.TaskBuilder;
import de.teamlapen.vampirism.player.tasks.unlock.LordLvlUnlocker;
import de.teamlapen.vampirism.player.tasks.unlock.LvlUnlocker;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@SuppressWarnings("unused")
@ObjectHolder(REFERENCE.MODID)
public class ModTasks {

    public static final Task feeding_adapter = getNull();

    public static void registerTasks(IForgeRegistry<Task> registry) {
        //Vampire gadgets
        registry.register(TaskBuilder.builder().withFaction(VReference.VAMPIRE_FACTION).unlockedBy(new LvlUnlocker(4)).addRequirement("advanced_hunter", ModEntities.advanced_hunter, 10).addRequirement("item", new ItemStack(Items.GOLD_INGOT, 5)).setReward(new ItemStack(ModItems.feeding_adapter)).build("feeding_adapter"));
        registry.register(TaskBuilder.builder().withFaction(VReference.VAMPIRE_FACTION).addRequirement("magma", new ItemStack(Items.MAGMA_CREAM, 3)).addRequirement("vampire", ModTags.Entities.VAMPIRE, 10).setReward(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModPotions.vampire_fire_resistance)).build("fire_resistance1"));
        registry.register(TaskBuilder.builder().withFaction(VReference.VAMPIRE_FACTION).unlockedBy(new LvlUnlocker(7)).addRequirement("magma", new ItemStack(Items.MAGMA_CREAM, 5)).addRequirement("vampire", ModTags.Entities.VAMPIRE, 15).setReward(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModPotions.long_vampire_fire_resistance)).build("fire_resistance2"));

        //Vampire Minion
        registry.register(TaskBuilder.builder().withFaction(VReference.VAMPIRE_FACTION).unlockedBy(new LordLvlUnlocker(1)).addRequirement("advanced_hunter", ModTags.Entities.ADVANCED_HUNTER, 5).addRequirement("baron", ModEntities.vampire_baron, 5).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 32)).setReward(new ItemStack(ModItems.vampire_minion_binding)).build("vampire_minion_binding"));
        registry.register(TaskBuilder.builder().withFaction(VReference.VAMPIRE_FACTION).unlockedBy(new LordLvlUnlocker(2)).addRequirement("advanced_hunter", ModTags.Entities.ADVANCED_HUNTER, 40).addRequirement("baron", ModEntities.vampire_baron, 10).addRequirement("gold", new ItemStack(Items.GOLD_BLOCK, 16)).setReward(new ItemStack(ModItems.vampire_minion_upgrade1)).build("vampire_minion_upgrade1"));
        registry.register(TaskBuilder.builder().withFaction(VReference.VAMPIRE_FACTION).unlockedBy(new LordLvlUnlocker(3)).addRequirement("advanced_hunter", ModTags.Entities.ADVANCED_HUNTER, 10).addRequirement("baron", ModEntities.vampire_baron, 20).addRequirement("gold", new ItemStack(Items.GOLD_BLOCK, 32)).addRequirement("human_heart", new ItemStack(ModItems.human_heart, 64)).addRequirement("diamond", new ItemStack(Items.DIAMOND_BLOCK, 10)).setReward(new ItemStack(ModItems.vampire_minion_upgrade2)).build("vampire_minion_upgrade2"));
        //Hunter Minion
        registry.register(TaskBuilder.builder().withFaction(VReference.HUNTER_FACTION).unlockedBy(new LordLvlUnlocker(1)).addRequirement("advanced_vampire", ModTags.Entities.ADVANCED_VAMPIRE, 5).addRequirement("baron", ModEntities.vampire_baron, 5).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 32)).setReward(new ItemStack(ModItems.hunter_minion_equipment)).build("hunter_minion_equipment"));
        registry.register(TaskBuilder.builder().withFaction(VReference.HUNTER_FACTION).unlockedBy(new LordLvlUnlocker(2)).addRequirement("advanced_vampire", ModTags.Entities.ADVANCED_VAMPIRE, 10).addRequirement("baron", ModEntities.vampire_baron, 10).addRequirement("gold", new ItemStack(Items.GOLD_BLOCK, 16)).setReward(new ItemStack(ModItems.hunter_minion_upgrade2)).build("hunter_minion_upgrade1"));
        registry.register(TaskBuilder.builder().withFaction(VReference.HUNTER_FACTION).unlockedBy(new LordLvlUnlocker(3)).addRequirement("advanced_vampire", ModTags.Entities.ADVANCED_VAMPIRE, 20).addRequirement("baron", ModEntities.vampire_baron, 20).addRequirement("gold", new ItemStack(Items.GOLD_BLOCK, 32)).addRequirement("vampire_blood", new ItemStack(ModItems.vampire_blood_bottle, 64)).addRequirement("diamond", new ItemStack(Items.DIAMOND_BLOCK, 10)).setReward(new ItemStack(ModItems.hunter_minion_upgrade2)).build("hunter_minion_upgrade2"));
        //Vampire Lord
        registry.register(TaskBuilder.builder().withFaction(VReference.VAMPIRE_FACTION).unlockedBy(new LvlUnlocker(VReference.VAMPIRE_FACTION.getHighestReachableLevel())).addRequirement("hunter", ModTags.Entities.HUNTER, 30).addRequirement("pure_blood", new ItemStack(ModItems.pure_blood_4, 5)).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 32)).setUnique().setReward(player -> levelupLord(player, 1)).build("vampire_lord1"));
        registry.register(TaskBuilder.builder().withFaction(VReference.VAMPIRE_FACTION).unlockedBy(new LordLvlUnlocker(1, true)).addRequirement("hunter", ModTags.Entities.HUNTER, 30).addRequirement("pure_blood", new ItemStack(ModItems.pure_blood_4, 5)).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 48)).setUnique().setReward(p -> levelupLord(p, 2)).build("vampire_lord2"));
        registry.register(TaskBuilder.builder().withFaction(VReference.VAMPIRE_FACTION).unlockedBy(new LordLvlUnlocker(2, true)).addRequirement("hunter", ModTags.Entities.HUNTER, 30).addRequirement("pure_blood", new ItemStack(ModItems.pure_blood_4, 5)).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 48)).setUnique().setReward(p -> levelupLord(p, 3)).build("vampire_lord3"));
        registry.register(TaskBuilder.builder().withFaction(VReference.VAMPIRE_FACTION).unlockedBy(new LordLvlUnlocker(3, true)).addRequirement("hunter", ModTags.Entities.ADVANCED_HUNTER, 5).addRequirement("pure_blood", new ItemStack(ModItems.pure_blood_4, 10)).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 64)).setUnique().setReward(p -> levelupLord(p, 4)).build("vampire_lord4"));
        registry.register(TaskBuilder.builder().withFaction(VReference.VAMPIRE_FACTION).unlockedBy(new LordLvlUnlocker(4, true)).addRequirement("hunter", ModTags.Entities.ADVANCED_HUNTER, 10).addRequirement("pure_blood", new ItemStack(ModItems.pure_blood_4, 20)).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 64)).setUnique().setReward(p -> levelupLord(p, 5)).build("vampire_lord5"));
        //Hunter Lord
        registry.register(TaskBuilder.builder().withFaction(VReference.HUNTER_FACTION).unlockedBy(new LvlUnlocker(VReference.HUNTER_FACTION.getHighestReachableLevel())).addRequirement("vampire", ModTags.Entities.VAMPIRE, 50).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 32)).setUnique().setReward(p -> levelupLord(p, 1)).build("hunter_lord1"));
        registry.register(TaskBuilder.builder().withFaction(VReference.HUNTER_FACTION).unlockedBy(new LordLvlUnlocker(1, true)).addRequirement("vampire", ModTags.Entities.VAMPIRE, 50).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 32)).setUnique().setReward(p -> levelupLord(p, 2)).build("hunter_lord2"));
        registry.register(TaskBuilder.builder().withFaction(VReference.HUNTER_FACTION).unlockedBy(new LordLvlUnlocker(2, true)).addRequirement("vampire", ModTags.Entities.VAMPIRE, 50).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 32)).setUnique().setReward(p -> levelupLord(p, 3)).build("hunter_lord3"));
        registry.register(TaskBuilder.builder().withFaction(VReference.HUNTER_FACTION).unlockedBy(new LordLvlUnlocker(3, true)).addRequirement("vampire", ModTags.Entities.VAMPIRE, 75).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 64)).setUnique().setReward(p -> levelupLord(p, 4)).build("hunter_lord4"));
        registry.register(TaskBuilder.builder().withFaction(VReference.HUNTER_FACTION).unlockedBy(new LordLvlUnlocker(4, true)).addRequirement("vampire", ModTags.Entities.VAMPIRE, 100).addRequirement("gold", new ItemStack(Items.GOLD_INGOT, 64)).setUnique().setReward(p -> levelupLord(p, 5)).build("hunter_lord5"));

    }


    public static void levelupLord(IFactionPlayer<?> p, int lordLevel) {
        FactionPlayerHandler.getOpt(p.getRepresentingPlayer()).ifPresent(fph -> {
            if (fph.getLordLevel() == lordLevel - 1) {
                fph.setLordLevel(lordLevel);
            }
        });
    }
}
