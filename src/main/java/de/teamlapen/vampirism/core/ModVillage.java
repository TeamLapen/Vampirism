package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.entity.FactionVillagerProfession;
import de.teamlapen.vampirism.entity.villager.Trades;
import de.teamlapen.vampirism.entity.villager.VampireVillagerHostilesSensor;
import de.teamlapen.vampirism.world.FactionPointOfInterestType;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.schedule.ScheduleBuilder;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModVillage {

    public static final VillagerProfession hunter_expert = getNull();
    public static final VillagerProfession vampire_expert = getNull();
    public static final VillagerProfession priest = getNull();

    public static final FactionPointOfInterestType no_faction = getNull();
    public static final FactionPointOfInterestType hunter_faction = getNull();
    public static final FactionPointOfInterestType vampire_faction = getNull();
    public static final PointOfInterestType church_altar = getNull();

    public static final Schedule converted_default = getNull();

    public static final SensorType<VampireVillagerHostilesSensor> vampire_villager_hostiles = getNull();

    static void registerProfessions(IForgeRegistry<VillagerProfession> registry) {
        VillagerProfession vampire_expert = new FactionVillagerProfession("vampire_expert", vampire_faction, ImmutableSet.of(), ImmutableSet.of(), null) {
            @Override
            public IFaction getFaction() {
                return VReference.VAMPIRE_FACTION;
            }
        }.setRegistryName(REFERENCE.MODID, "vampire_expert");
        VillagerProfession hunter_expert = new FactionVillagerProfession("hunter_expert", hunter_faction, ImmutableSet.of(), ImmutableSet.of(), null) {
            @Override
            public IFaction getFaction() {
                return VReference.HUNTER_FACTION;
            }
        }.setRegistryName(REFERENCE.MODID, "hunter_expert");
        VillagerProfession priest = new VillagerProfession("priest", church_altar, ImmutableSet.of(), ImmutableSet.of(), ModSounds.blessing_music).setRegistryName(REFERENCE.MODID, "priest");
        registry.register(vampire_expert);
        registry.register(hunter_expert);
        registry.register(priest);
        VillagerTrades.TRADES.computeIfAbsent(hunter_expert, trades -> new Int2ObjectOpenHashMap<>()).putAll(getHunterTrades());
        VillagerTrades.TRADES.computeIfAbsent(vampire_expert, trades -> new Int2ObjectOpenHashMap<>()).putAll(getVampireTrades());
        VillagerTrades.TRADES.computeIfAbsent(priest, trades -> new Int2ObjectOpenHashMap<>()).putAll(getPriestTrades());
    }

    static void registerVillagePointOfInterestType(IForgeRegistry<PointOfInterestType> registry) {
        PointOfInterestType hunter_faction = new FactionPointOfInterestType("hunter_faction", getAllStates(ModBlocks.totem_top_vampirism_hunter, ModBlocks.totem_top_vampirism_hunter_crafted), 1, 1).setRegistryName(REFERENCE.MODID, "hunter_faction");
        PointOfInterestType vampire_faction = new FactionPointOfInterestType("vampire_faction", getAllStates(ModBlocks.totem_top_vampirism_vampire, ModBlocks.totem_top_vampirism_vampire_crafted), 1, 1).setRegistryName(REFERENCE.MODID, "vampire_faction");
        PointOfInterestType no_faction = new FactionPointOfInterestType("no_faction", getAllStates(ModBlocks.totem_top, ModBlocks.totem_top_crafted), 1, 1).setRegistryName(REFERENCE.MODID, "no_faction");
        PointOfInterestType church_altar = new PointOfInterestType("church_altar", getAllStates(ModBlocks.church_altar), 1, 1).setRegistryName(REFERENCE.MODID, "church_altar");
        registry.register(hunter_faction);
        registry.register(vampire_faction);
        registry.register(no_faction);
        registry.register(church_altar);
        PointOfInterestType.registerBlockStates(hunter_faction);
        PointOfInterestType.registerBlockStates(vampire_faction);
        PointOfInterestType.registerBlockStates(no_faction);
        PointOfInterestType.registerBlockStates(church_altar);
    }

    static void registerSensor(IForgeRegistry<SensorType<?>> registry) {
        registry.register(new SensorType<>(VampireVillagerHostilesSensor::new).setRegistryName(REFERENCE.MODID, "vampire_villager_hostiles"));
    }

    static void registerSchedule(IForgeRegistry<Schedule> registry) {
        registry.register(new ScheduleBuilder(new Schedule()).changeActivityAt(12000, Activity.IDLE).changeActivityAt(10, Activity.REST).changeActivityAt(14000, Activity.WORK).changeActivityAt(21000, Activity.MEET).changeActivityAt(23000, Activity.IDLE).build().setRegistryName(REFERENCE.MODID, "converted_default"));
    }

    private static Set<BlockState> getAllStates(Block... blocks) {
        return Arrays.stream(blocks).map(block -> block.getStateDefinition().any()).collect(ImmutableSet.toImmutableSet());
    }

    private static Map<Integer, VillagerTrades.ITrade[]> getHunterTrades() {
        return ImmutableMap.of(
                1, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.item_garlic, new Trades.Price(2, 5)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.armor_of_swiftness_chest_normal, new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.armor_of_swiftness_legs_normal, new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.armor_of_swiftness_feet_normal, new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.armor_of_swiftness_head_normal, new Trades.Price(1, 1), 6, 1),
                        new Trades.BiomeMapForEmeralds(5, ModBiomes.VAMPIRE_FOREST_KEY,3,2)
                },
                2, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForSouls(new Trades.Price(25, 45), Items.DIAMOND, new Trades.Price(1, 1), 2, 5),
                        new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.crossbow_arrow_normal, new Trades.Price(5, 15)),
                        new VillagerTrades.ItemsForEmeraldsTrade(ModItems.soul_orb_vampire, 10, 10, 4),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.hunter_coat_chest_normal, new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.hunter_coat_legs_normal, new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.hunter_coat_feet_normal, new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.hunter_coat_head_normal, new Trades.Price(1, 1), 6, 1),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.armor_of_swiftness_chest_enhanced, new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.armor_of_swiftness_legs_enhanced, new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.armor_of_swiftness_feet_enhanced, new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.armor_of_swiftness_head_enhanced, new Trades.Price(1, 1), 6, 1)
                },
                3, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForSouls(new Trades.Price(40, 64), ModItems.vampire_book, new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.hunter_coat_chest_enhanced, new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.hunter_coat_legs_enhanced, new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.hunter_coat_feet_enhanced, new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.hunter_coat_head_enhanced, new Trades.Price(1, 1), 6, 1),
                        new Trades.ItemsForSouls(new Trades.Price(30, 45), ModItems.armor_of_swiftness_chest_ultimate, new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 45), ModItems.armor_of_swiftness_legs_ultimate, new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(15, 30), ModItems.armor_of_swiftness_feet_ultimate, new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.armor_of_swiftness_head_ultimate, new Trades.Price(1, 1), 6, 1)
                },
                4, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForSouls(new Trades.Price(20, 32), Items.DIAMOND, new Trades.Price(1, 2)),
                },
                5, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForSouls(new Trades.Price(30, 55), ModItems.hunter_coat_chest_ultimate, new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 55), ModItems.hunter_coat_legs_ultimate, new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 35), ModItems.hunter_coat_feet_ultimate, new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 35), ModItems.hunter_coat_head_ultimate, new Trades.Price(1, 1), 6, 1)
                });
    }

    private static Map<Integer, VillagerTrades.ITrade[]> getPriestTrades() {
        return ImmutableMap.of(
                1, new VillagerTrades.ITrade[]{
                        new VillagerTrades.EmeraldForItemsTrade(ModItems.pure_salt, 25, 1, 4),
                        new VillagerTrades.EmeraldForItemsTrade(ModItems.pure_salt_water, 25, 1, 4),
                        new VillagerTrades.EmeraldForItemsTrade(ModItems.item_garlic, 30, 4, 2),

                },
                2, new VillagerTrades.ITrade[]{
                        new VillagerTrades.ItemsForEmeraldsTrade(ModItems.holy_water_bottle_normal, 3, 5, 4),
                        new VillagerTrades.EmeraldForItemsTrade(ModItems.soul_orb_vampire, 10, 10, 4),
                        new VillagerTrades.EmeraldForItemsTrade(ModItems.vampire_blood_bottle, 9, 4, 5)
                },
                3, new VillagerTrades.ITrade[]{
                        new VillagerTrades.ItemsForEmeraldsTrade(ModItems.holy_water_bottle_enhanced, 2, 5, 4),


                },
                4, new VillagerTrades.ITrade[]{
                        new VillagerTrades.ItemsForEmeraldsTrade(ModItems.holy_water_bottle_ultimate, 1, 4, 4),

                },
                5, new VillagerTrades.ITrade[]{
                        new VillagerTrades.ItemsForEmeraldsTrade(ModItems.holy_water_bottle_enhanced, 3, 4, 4),

                });
    }

    private static Map<Integer, VillagerTrades.ITrade[]> getVampireTrades() {
        return ImmutableMap.of(
                1, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForHeart(new Trades.Price(10, 15), ModItems.pure_blood_0, new Trades.Price(1, 1)),
                        new Trades.BloodBottleForHeart(new Trades.Price(3, 12), new Trades.Price(1, 15), 9),
                        new VillagerTrades.EmeraldForItemsTrade(ModBlocks.vampire_orchid, 4, 1, 3),
                        new Trades.BiomeMapForEmeralds(5, ModBiomes.VAMPIRE_FOREST_KEY,3,2)
                },
                2, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForHeart(new Trades.Price(25, 30), ModItems.pure_blood_1, new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(1, 5), ModBlocks.coffin, new Trades.Price(1, 1), 2, 2),
                        new Trades.ItemsForHeart(new Trades.Price(10, 25), ModItems.blood_infused_iron_ingot, new Trades.Price(1, 3))
                },
                3, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForHeart(new Trades.Price(30, 40), ModItems.pure_blood_2, new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(15, 30), ModItems.blood_infused_enhanced_iron_ingot, new Trades.Price(1, 2))
                },
                4, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForHeart(new Trades.Price(20, 30), ModItems.pure_blood_3, new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(10, 30), new ItemStack[]{
                                new ItemStack(ModItems.vampire_cloak_black_blue),
                                new ItemStack(ModItems.vampire_cloak_black_red),
                                new ItemStack(ModItems.vampire_cloak_black_white),
                                new ItemStack(ModItems.vampire_cloak_red_black),
                                new ItemStack(ModItems.vampire_cloak_white_black)}, new Trades.Price(1, 1), 10, 2)
                },
                5, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForHeart(new Trades.Price(30, 40), ModItems.pure_blood_4, new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(10, 30), new ItemStack[]{
                                new ItemStack(ModItems.vampire_cloak_black_blue),
                                new ItemStack(ModItems.vampire_cloak_black_red),
                                new ItemStack(ModItems.vampire_cloak_black_white),
                                new ItemStack(ModItems.vampire_cloak_red_black),
                                new ItemStack(ModItems.vampire_cloak_white_black)}, new Trades.Price(1, 1), 10, 2),
                        new VillagerTrades.ItemsForEmeraldsTrade(ModItems.heart_seeker_ultimate, 40, 1, 15),
                        new VillagerTrades.ItemsForEmeraldsTrade(ModItems.heart_striker_ultimate, 40, 1, 15)

                });
    }
}
