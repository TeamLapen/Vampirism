package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import de.teamlapen.vampirism.entity.villager.Trades;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.schedule.ScheduleBuilder;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModVillage {

    public static final VillagerProfession hunter_expert = getNull();
    public static final VillagerProfession vampire_expert = getNull();

    public static final PointOfInterestType hunter_faction = getNull();
    public static final PointOfInterestType vampire_faction = getNull();

    public static final Schedule converted_default = getNull();

    static void registerProfessions(IForgeRegistry<VillagerProfession> registry) {
        VillagerProfession vampire_expert = new VillagerProfession("vampire_expert", vampire_faction, ImmutableSet.of(), ImmutableSet.of()).setRegistryName(REFERENCE.MODID, "vampire_expert");
        VillagerProfession hunter_expert = new VillagerProfession("hunter_expert", hunter_faction, ImmutableSet.of(), ImmutableSet.of()).setRegistryName(REFERENCE.MODID, "hunter_expert");
        registry.register(vampire_expert);
        registry.register(hunter_expert);
        VillagerTrades.field_221239_a.computeIfAbsent(hunter_expert, trades -> new Int2ObjectOpenHashMap<>()).putAll(ImmutableMap.of(
                1, new VillagerTrades.ITrade[]{new Trades.ItemsForSouls(new Trades.Price(10, 15), new ItemStack[]{new ItemStack(ModItems.hunter_coat_feet_normal), new ItemStack(ModItems.obsidian_armor_feet_normal), new ItemStack(ModItems.armor_of_swiftness_feet_normal)}, new Trades.Price(1, 1)), new Trades.ItemsForSouls(new Trades.Price(25, 35), new ItemStack[]{new ItemStack(ModItems.hunter_coat_legs_normal), new ItemStack(ModItems.obsidian_armor_legs_normal), new ItemStack(ModItems.armor_of_swiftness_legs_normal)}, new Trades.Price(1, 1)), new Trades.ItemsForSouls(new Trades.Price(30, 40), new ItemStack[]{new ItemStack(ModItems.hunter_coat_chest_normal), new ItemStack(ModItems.obsidian_armor_chest_normal), new ItemStack(ModItems.armor_of_swiftness_chest_normal)}, new Trades.Price(1, 1)), new Trades.ItemsForSouls(new Trades.Price(20, 30), new ItemStack[]{new ItemStack(ModItems.hunter_coat_head_normal), new ItemStack(ModItems.obsidian_armor_head_normal), new ItemStack(ModItems.armor_of_swiftness_head_normal)}, new Trades.Price(1, 1)), new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.item_garlic, new Trades.Price(2, 5)), new Trades.ItemsForSouls(new Trades.Price(50, 100), Items.DIAMOND, new Trades.Price(1, 1))},
                2, new VillagerTrades.ITrade[]{new Trades.ItemsForSouls(new Trades.Price(10, 15), new ItemStack[]{new ItemStack(ModItems.hunter_coat_feet_enhanced), new ItemStack(ModItems.obsidian_armor_feet_enhanced), new ItemStack(ModItems.armor_of_swiftness_feet_enhanced)}, new Trades.Price(1, 1)), new Trades.ItemsForSouls(new Trades.Price(25, 35), new ItemStack[]{new ItemStack(ModItems.hunter_coat_legs_enhanced), new ItemStack(ModItems.obsidian_armor_legs_enhanced), new ItemStack(ModItems.armor_of_swiftness_legs_enhanced)}, new Trades.Price(1, 1)), new Trades.ItemsForSouls(new Trades.Price(30, 40), new ItemStack[]{new ItemStack(ModItems.hunter_coat_chest_enhanced), new ItemStack(ModItems.obsidian_armor_chest_enhanced), new ItemStack(ModItems.armor_of_swiftness_chest_enhanced)}, new Trades.Price(1, 1)), new Trades.ItemsForSouls(new Trades.Price(20, 30), new ItemStack[]{new ItemStack(ModItems.hunter_coat_head_enhanced), new ItemStack(ModItems.obsidian_armor_head_enhanced), new ItemStack(ModItems.armor_of_swiftness_head_enhanced)}, new Trades.Price(1, 1)), new Trades.ItemsForSouls(new Trades.Price(40, 90), Items.DIAMOND, new Trades.Price(1, 2))},
                3, new VillagerTrades.ITrade[]{new Trades.ItemsForSouls(new Trades.Price(100, 200), new ItemStack[]{new ItemStack(ModItems.hunter_coat_feet_ultimate), new ItemStack(ModItems.obsidian_armor_feet_ultimate), new ItemStack(ModItems.armor_of_swiftness_feet_ultimate), new ItemStack(ModItems.hunter_coat_legs_ultimate), new ItemStack(ModItems.obsidian_armor_legs_ultimate), new ItemStack(ModItems.armor_of_swiftness_legs_ultimate), new ItemStack(ModItems.hunter_coat_chest_ultimate), new ItemStack(ModItems.obsidian_armor_chest_ultimate), new ItemStack(ModItems.armor_of_swiftness_chest_ultimate), new ItemStack(ModItems.hunter_coat_head_ultimate), new ItemStack(ModItems.obsidian_armor_head_ultimate), new ItemStack(ModItems.armor_of_swiftness_head_ultimate)}, new Trades.Price(1, 1))}
        ));
        VillagerTrades.field_221239_a.computeIfAbsent(vampire_expert, trades -> new Int2ObjectOpenHashMap<>()).putAll(ImmutableMap.of(
                1, new VillagerTrades.ITrade[]{new Trades.ItemsForHeart(new Trades.Price(10, 15), ModItems.pure_blood_0, new Trades.Price(1, 1)), new Trades.ItemsForHeart(new Trades.Price(25, 30), ModItems.pure_blood_1, new Trades.Price(1, 1)), new Trades.ItemsForHeart(new Trades.Price(30, 40), ModItems.pure_blood_2, new Trades.Price(1, 1)), new Trades.ItemsForHeart(new Trades.Price(1, 5), ModBlocks.coffin, new Trades.Price(1, 1))},
                2, new VillagerTrades.ITrade[]{new Trades.BloodBottleForHeart(new Trades.Price(3, 12), new Trades.Price(1, 15), 9), new Trades.ItemsForHeart(new Trades.Price(30, 40), ModItems.pure_blood_4, new Trades.Price(1, 1)), new Trades.ItemsForHeart(new Trades.Price(20, 30), ModItems.pure_blood_3, new Trades.Price(1, 1))},
                3, new VillagerTrades.ITrade[]{new Trades.ItemsForHeart(new Trades.Price(10, 30), new ItemStack[]{new ItemStack(ModItems.vampire_cloak_black_blue), new ItemStack(ModItems.vampire_cloak_black_red), new ItemStack(ModItems.vampire_cloak_black_white), new ItemStack(ModItems.vampire_cloak_red_black), new ItemStack(ModItems.vampire_cloak_white_black)}, new Trades.Price(1, 1))}
        ));
    }

    static void registerVillagePointOfInterestType(IForgeRegistry<PointOfInterestType> registry) {
        PointOfInterestType hunter_faction = new PointOfInterestType("hunter_faction", ImmutableSet.of(ModBlocks.totem_top_hunter.getStateContainer().getBaseState()), 1, null, 1).setRegistryName(REFERENCE.MODID, "hunter_faction");
        PointOfInterestType vampire_faction = new PointOfInterestType("vampire_faction", ImmutableSet.of(ModBlocks.totem_top_vampire.getStateContainer().getBaseState()), 1, null, 1).setRegistryName(REFERENCE.MODID, "vampire_faction");
        registry.register(hunter_faction);
        registry.register(vampire_faction);
        PointOfInterestType.func_221052_a(hunter_faction);
        PointOfInterestType.func_221052_a(vampire_faction);
    }

    static void registerSchedule(IForgeRegistry<Schedule> registry) {
        registry.register(new ScheduleBuilder(new Schedule()).add(12000, Activity.IDLE).add(10, Activity.REST).add(14000, Activity.WORK).add(21000, Activity.MEET).add(23000, Activity.IDLE).build().setRegistryName(REFERENCE.MODID, "converted_default"));
    }
}
