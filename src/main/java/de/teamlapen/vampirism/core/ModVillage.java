package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.entity.FactionVillagerProfession;
import de.teamlapen.vampirism.entity.villager.Trades;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.FactionPointOfInterestType;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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

import java.util.Map;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModVillage {

    public static final VillagerProfession hunter_expert = getNull();
    public static final VillagerProfession vampire_expert = getNull();

    public static final FactionPointOfInterestType no_faction = getNull();
    public static final FactionPointOfInterestType hunter_faction = getNull();
    public static final FactionPointOfInterestType vampire_faction = getNull();

    public static final Schedule converted_default = getNull();

    static void registerProfessions(IForgeRegistry<VillagerProfession> registry) {
        VillagerProfession vampire_expert = new FactionVillagerProfession("vampire_expert", vampire_faction, ImmutableSet.of(), ImmutableSet.of()){
            @Override
            public IFaction getFaction() {
                return VReference.VAMPIRE_FACTION;
            }
        }.setRegistryName(REFERENCE.MODID, "vampire_expert");
        VillagerProfession hunter_expert = new FactionVillagerProfession("hunter_expert", hunter_faction, ImmutableSet.of(), ImmutableSet.of()){
            @Override
            public IFaction getFaction() {
                return VReference.HUNTER_FACTION;
            }
        }.setRegistryName(REFERENCE.MODID, "hunter_expert");
        registry.register(vampire_expert);
        registry.register(hunter_expert);
        VillagerTrades.VILLAGER_DEFAULT_TRADES.computeIfAbsent(hunter_expert, trades -> new Int2ObjectOpenHashMap<>()).putAll(getHunterTrades());
        VillagerTrades.VILLAGER_DEFAULT_TRADES.computeIfAbsent(vampire_expert, trades -> new Int2ObjectOpenHashMap<>()).putAll(getVampireTrades());
    }

    static void registerVillagePointOfInterestType(IForgeRegistry<PointOfInterestType> registry) {
        PointOfInterestType hunter_faction = new FactionPointOfInterestType("hunter_faction", ImmutableSet.of(ModBlocks.totem_top_vampirism_hunter.getStateContainer().getBaseState()), 1, null, 1).setRegistryName(REFERENCE.MODID, "hunter_faction");
        PointOfInterestType vampire_faction = new FactionPointOfInterestType("vampire_faction", ImmutableSet.of(ModBlocks.totem_top_vampirism_vampire.getStateContainer().getBaseState()), 1, null, 1).setRegistryName(REFERENCE.MODID, "vampire_faction");
        PointOfInterestType no_faction = new FactionPointOfInterestType("no_faction", ImmutableSet.of(ModBlocks.totem_top.getStateContainer().getBaseState()),1,null,1).setRegistryName(REFERENCE.MODID,"no_faction");
        registry.register(hunter_faction);
        registry.register(vampire_faction);
        registry.register(no_faction);
        PointOfInterestType.func_221052_a(hunter_faction);
        PointOfInterestType.func_221052_a(vampire_faction);
        PointOfInterestType.func_221052_a(no_faction);
    }

    static void registerSchedule(IForgeRegistry<Schedule> registry) {
        registry.register(new ScheduleBuilder(new Schedule()).add(12000, Activity.IDLE).add(10, Activity.REST).add(14000, Activity.WORK).add(21000, Activity.MEET).add(23000, Activity.IDLE).build().setRegistryName(REFERENCE.MODID, "converted_default"));
    }

    private static Map<Integer, VillagerTrades.ITrade[]> getHunterTrades() {
        return ImmutableMap.of(
                1, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.item_garlic, new Trades.Price(2, 5)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.armor_of_swiftness_chest_normal, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.armor_of_swiftness_legs_normal, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.armor_of_swiftness_feet_normal, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.armor_of_swiftness_head_normal, new Trades.Price(1, 1))
                },
                2, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForSouls(new Trades.Price(40, 64), Items.DIAMOND, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.crossbow_arrow_normal, new Trades.Price(5, 15)),
                        new VillagerTrades.ItemsForEmeraldsTrade(ModItems.soul_orb_vampire, 10, 10, 4),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.hunter_coat_chest_normal, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.hunter_coat_legs_normal, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.hunter_coat_feet_normal, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.hunter_coat_head_normal, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.armor_of_swiftness_chest_enhanced, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.armor_of_swiftness_legs_enhanced, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.armor_of_swiftness_feet_enhanced, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.armor_of_swiftness_head_enhanced, new Trades.Price(1, 1))
                },
                3, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.vampire_book, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.holy_water_bottle_enhanced, new Trades.Price(1, 3)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.obsidian_armor_chest_normal, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.obsidian_armor_legs_normal, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.obsidian_armor_feet_normal, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.obsidian_armor_head_normal, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.hunter_coat_chest_enhanced, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.hunter_coat_legs_enhanced, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.hunter_coat_feet_enhanced, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.hunter_coat_head_enhanced, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 45), ModItems.armor_of_swiftness_chest_ultimate, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(25, 45), ModItems.armor_of_swiftness_legs_ultimate, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(10, 30), ModItems.armor_of_swiftness_feet_ultimate, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.armor_of_swiftness_head_ultimate, new Trades.Price(1, 1))
                },
                4, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForSouls(new Trades.Price(20, 32), Items.DIAMOND, new Trades.Price(1, 2)),
                        new Trades.ItemsForSouls(new Trades.Price(15, 25), ModItems.holy_water_bottle_ultimate, new Trades.Price(1, 2)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.obsidian_armor_chest_enhanced, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.obsidian_armor_legs_enhanced, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.obsidian_armor_feet_enhanced, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.obsidian_armor_head_enhanced, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 55), ModItems.hunter_coat_chest_ultimate, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(25, 55), ModItems.hunter_coat_legs_ultimate, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(20, 35), ModItems.hunter_coat_feet_ultimate, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(20, 35), ModItems.hunter_coat_head_ultimate, new Trades.Price(1, 1))
                },
                5, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForSouls(new Trades.Price(30, 64), ModItems.obsidian_armor_chest_ultimate, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(25, 64), ModItems.obsidian_armor_legs_ultimate, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(20, 40), ModItems.obsidian_armor_feet_ultimate, new Trades.Price(1, 1)),
                        new Trades.ItemsForSouls(new Trades.Price(20, 40), ModItems.obsidian_armor_head_ultimate, new Trades.Price(1, 1))
                });
    }

    private static Map<Integer, VillagerTrades.ITrade[]> getVampireTrades() {
        return ImmutableMap.of(
                1, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForHeart(new Trades.Price(10, 15), ModItems.pure_blood_0, new Trades.Price(1, 1)),
                        new Trades.BloodBottleForHeart(new Trades.Price(3, 12), new Trades.Price(1, 15), 9),
                        new VillagerTrades.EmeraldForItemsTrade(ModBlocks.vampire_orchid, 4, 1, 3),
                },
                2, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForHeart(new Trades.Price(25, 30), ModItems.pure_blood_1, new Trades.Price(1, 1)),
                        new Trades.ItemsForHeart(new Trades.Price(1, 5), ModBlocks.coffin, new Trades.Price(1, 1)),
                        new Trades.ItemsForHeart(new Trades.Price(10, 25), ModItems.blood_infused_iron_ingot, new Trades.Price(1, 3))
                },
                3, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForHeart(new Trades.Price(30, 40), ModItems.pure_blood_2, new Trades.Price(1, 1)),
                        new Trades.ItemsForHeart(new Trades.Price(15, 30), ModItems.blood_infused_enhanced_iron_ingot, new Trades.Price(1, 2))
                },
                4, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForHeart(new Trades.Price(20, 30), ModItems.pure_blood_3, new Trades.Price(1, 1)),
                        new Trades.ItemsForHeart(new Trades.Price(10, 30), new ItemStack[]{
                                new ItemStack(ModItems.vampire_cloak_black_blue),
                                new ItemStack(ModItems.vampire_cloak_black_red),
                                new ItemStack(ModItems.vampire_cloak_black_white),
                                new ItemStack(ModItems.vampire_cloak_red_black),
                                new ItemStack(ModItems.vampire_cloak_white_black)}, new Trades.Price(1, 1), 10, 2)
                },
                5, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForHeart(new Trades.Price(30, 40), ModItems.pure_blood_4, new Trades.Price(1, 1)),
                        new Trades.ItemsForHeart(new Trades.Price(10, 30), new ItemStack[]{
                                new ItemStack(ModItems.vampire_cloak_black_blue),
                                new ItemStack(ModItems.vampire_cloak_black_red),
                                new ItemStack(ModItems.vampire_cloak_black_white),
                                new ItemStack(ModItems.vampire_cloak_red_black),
                                new ItemStack(ModItems.vampire_cloak_white_black)}, new Trades.Price(1, 1), 10, 2),
                        new VillagerTrades.ItemsForEmeraldsTrade(ModItems.heart_seeker_ultimate, 50, 1, 10)
                });
    }
}
