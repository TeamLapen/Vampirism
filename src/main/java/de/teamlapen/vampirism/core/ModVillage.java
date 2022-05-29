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

    public static final FactionPointOfInterestType no_faction = getNull();
    public static final FactionPointOfInterestType hunter_faction = getNull();
    public static final FactionPointOfInterestType vampire_faction = getNull();

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
        registry.register(vampire_expert);
        registry.register(hunter_expert);
        VillagerTrades.TRADES.computeIfAbsent(hunter_expert, trades -> new Int2ObjectOpenHashMap<>()).putAll(getHunterTrades());
        VillagerTrades.TRADES.computeIfAbsent(vampire_expert, trades -> new Int2ObjectOpenHashMap<>()).putAll(getVampireTrades());
    }

    static void registerVillagePointOfInterestType(IForgeRegistry<PointOfInterestType> registry) {
        PointOfInterestType hunter_faction = new FactionPointOfInterestType("hunter_faction", getAllStates(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED.get()), 1, 1).setRegistryName(REFERENCE.MODID, "hunter_faction");
        PointOfInterestType vampire_faction = new FactionPointOfInterestType("vampire_faction", getAllStates(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED.get()), 1, 1).setRegistryName(REFERENCE.MODID, "vampire_faction");
        PointOfInterestType no_faction = new FactionPointOfInterestType("no_faction", getAllStates(ModBlocks.TOTEM_TOP.get(), ModBlocks.TOTEM_TOP_CRAFTED.get()), 1, 1).setRegistryName(REFERENCE.MODID, "no_faction");
        registry.register(hunter_faction);
        registry.register(vampire_faction);
        registry.register(no_faction);
        PointOfInterestType.registerBlockStates(hunter_faction);
        PointOfInterestType.registerBlockStates(vampire_faction);
        PointOfInterestType.registerBlockStates(no_faction);
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
                        new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.ITEM_GARLIC.get(), new Trades.Price(2, 5)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), new Trades.Price(1, 1), 6, 1),
                        new Trades.BiomeMapForEmeralds(5, ModBiomes.VAMPIRE_FOREST_KEY,3,2)
                },
                2, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForSouls(new Trades.Price(25, 45), Items.DIAMOND, new Trades.Price(1, 1), 2, 5),
                        new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.CROSSBOW_ARROW_NORMAL.get(), new Trades.Price(5, 15)),
                        new VillagerTrades.ItemsForEmeraldsTrade(ModItems.SOUL_ORB_VAMPIRE.get(), 10, 10, 4),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.HUNTER_COAT_CHEST_NORMAL.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.HUNTER_COAT_LEGS_NORMAL.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.HUNTER_COAT_FEET_NORMAL.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.HUNTER_COAT_HEAD_NORMAL.get(), new Trades.Price(1, 1), 6, 1),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), new Trades.Price(1, 1), 6, 1)
                },
                3, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForSouls(new Trades.Price(40, 64), ModItems.VAMPIRE_BOOK.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), new Trades.Price(1, 3)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.HUNTER_COAT_CHEST_ENHANCED.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.HUNTER_COAT_LEGS_ENHANCED.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.HUNTER_COAT_FEET_ENHANCED.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.HUNTER_COAT_HEAD_ENHANCED.get(), new Trades.Price(1, 1), 6, 1),
                        new Trades.ItemsForSouls(new Trades.Price(30, 45), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 45), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(15, 30), ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get(), new Trades.Price(1, 1), 6, 1)
                },
                4, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForSouls(new Trades.Price(20, 32), Items.DIAMOND, new Trades.Price(1, 2)),
                },
                5, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForSouls(new Trades.Price(15, 25), ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get(), new Trades.Price(1, 2)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 55), ModItems.HUNTER_COAT_CHEST_ULTIMATE.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 55), ModItems.HUNTER_COAT_LEGS_ULTIMATE.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 35), ModItems.HUNTER_COAT_FEET_ULTIMATE.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 35), ModItems.HUNTER_COAT_HEAD_ULTIMATE.get(), new Trades.Price(1, 1), 6, 1)
                });
    }

    private static Map<Integer, VillagerTrades.ITrade[]> getVampireTrades() {
        return ImmutableMap.of(
                1, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForHeart(new Trades.Price(10, 15), ModItems.PURE_BLOOD_0.get(), new Trades.Price(1, 1)),
                        new Trades.BloodBottleForHeart(new Trades.Price(3, 12), new Trades.Price(1, 15), 9),
                        new VillagerTrades.EmeraldForItemsTrade(ModBlocks.VAMPIRE_ORCHID.get(), 4, 1, 3),
                        new Trades.BiomeMapForEmeralds(5, ModBiomes.VAMPIRE_FOREST_KEY,3,2)
                },
                2, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForHeart(new Trades.Price(25, 30), ModItems.PURE_BLOOD_1.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(1, 5), ModBlocks.COFFIN_RED.get(), new Trades.Price(1, 1), 2, 2),
                        new Trades.ItemsForHeart(new Trades.Price(10, 25), ModItems.BLOOD_INFUSED_IRON_INGOT.get(), new Trades.Price(1, 3))
                },
                3, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForHeart(new Trades.Price(30, 40), ModItems.PURE_BLOOD_2.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(15, 30), ModItems.BLOOD_INFUSED_ENHANCED_IRON_INGOT.get(), new Trades.Price(1, 2))
                },
                4, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForHeart(new Trades.Price(20, 30), ModItems.PURE_BLOOD_3.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(10, 30), new ItemStack[]{
                                new ItemStack(ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_BLACK_RED.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_RED_BLACK.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get())}, new Trades.Price(1, 1), 10, 2)
                },
                5, new VillagerTrades.ITrade[]{
                        new Trades.ItemsForHeart(new Trades.Price(30, 40), ModItems.PURE_BLOOD_4.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(10, 30), new ItemStack[]{
                                new ItemStack(ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_BLACK_RED.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_RED_BLACK.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get())}, new Trades.Price(1, 1), 10, 2),
                        new VillagerTrades.ItemsForEmeraldsTrade(ModItems.HEART_SEEKER_ULTIMATE.get(), 40, 1, 15),
                        new VillagerTrades.ItemsForEmeraldsTrade(ModItems.HEART_STRIKER_ULTIMATE.get(), 40, 1, 15)

                });
    }
}
