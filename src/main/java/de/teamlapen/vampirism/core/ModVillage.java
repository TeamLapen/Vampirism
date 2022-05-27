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
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.entity.schedule.ScheduleBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class ModVillage {
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, REFERENCE.MODID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, REFERENCE.MODID);
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, REFERENCE.MODID);
    public static final DeferredRegister<Schedule> SCHEDULES = DeferredRegister.create(ForgeRegistries.SCHEDULES, REFERENCE.MODID);

    public static final RegistryObject<FactionPointOfInterestType> HUNTER_FACTION = POI_TYPES.register("hunter_faction", () -> (FactionPointOfInterestType) PoiType.registerBlockStates(new FactionPointOfInterestType("hunter_faction", getAllStates(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED.get()), 1, 1)));
    public static final RegistryObject<FactionPointOfInterestType> VAMPIRE_FACTION = POI_TYPES.register("vampire_faction", () -> (FactionPointOfInterestType) PoiType.registerBlockStates(new FactionPointOfInterestType("vampire_faction", getAllStates(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED.get()), 1, 1)));
    public static final RegistryObject<FactionPointOfInterestType> NO_FACTION = POI_TYPES.register("no_faction", () -> (FactionPointOfInterestType) PoiType.registerBlockStates(new FactionPointOfInterestType("no_faction", getAllStates(ModBlocks.TOTEM_TOP.get(), ModBlocks.TOTEM_TOP_CRAFTED.get()), 1, 1)));

    public static final RegistryObject<SensorType<VampireVillagerHostilesSensor>> VAMPIRE_VILLAGER_HOSTILES = SENSOR_TYPES.register("vampire_villager_hostiles", () -> new SensorType<>(VampireVillagerHostilesSensor::new));

    public static final RegistryObject<Schedule> CONVERTED_DEFAULT = SCHEDULES.register("converted_default", () ->
            new ScheduleBuilder(new Schedule()).changeActivityAt(12000, Activity.IDLE).changeActivityAt(10, Activity.REST).changeActivityAt(14000, Activity.WORK).changeActivityAt(21000, Activity.MEET).changeActivityAt(23000, Activity.IDLE).build());


    public static final RegistryObject<VillagerProfession> VAMPIRE_EXPERT = PROFESSIONS.register("vampire_expert", () -> new FactionVillagerProfession("vampire_expert", VAMPIRE_FACTION.get(), ImmutableSet.of(), ImmutableSet.of(), null) {
        @Override
        public IFaction<?> getFaction() {
            return VReference.VAMPIRE_FACTION;
        }
    });
    public static final RegistryObject<VillagerProfession> HUNTER_EXPERT = PROFESSIONS.register("hunter_expert", () -> new FactionVillagerProfession("hunter_expert", HUNTER_FACTION.get(), ImmutableSet.of(), ImmutableSet.of(), null) {
        @Override
        public IFaction<?> getFaction() {
            return VReference.VAMPIRE_FACTION;
        }
    });

    static void registerVillageObjects(IEventBus bus) {
        POI_TYPES.register(bus);
        PROFESSIONS.register(bus);
        SENSOR_TYPES.register(bus);
        SCHEDULES.register(bus);
    }

    public static void villagerTradeSetup() {
        VillagerTrades.TRADES.computeIfAbsent(VAMPIRE_EXPERT.get(), trades -> new Int2ObjectOpenHashMap<>()).putAll(getVampireTrades());
        VillagerTrades.TRADES.computeIfAbsent(HUNTER_EXPERT.get(), trades -> new Int2ObjectOpenHashMap<>()).putAll(getHunterTrades());
    }

    private static Set<BlockState> getAllStates(Block... blocks) {
        return Arrays.stream(blocks).map(block -> block.getStateDefinition().any()).collect(ImmutableSet.toImmutableSet());
    }

    private static Map<Integer, VillagerTrades.ItemListing[]> getHunterTrades() {
        return ImmutableMap.of(
                1, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.ITEM_GARLIC.get(), new Trades.Price(2, 5)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), new Trades.Price(1, 1), 6, 1),
                        new Trades.BiomeMapForEmeralds(5, ModBiomes.VAMPIRE_FOREST.getKey(), 3, 2)
                },
                2, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForSouls(new Trades.Price(25, 45), Items.DIAMOND, new Trades.Price(1, 1), 2, 5),
                        new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.CROSSBOW_ARROW_NORMAL.get(), new Trades.Price(5, 15)),
                        new VillagerTrades.ItemsForEmeralds(ModItems.SOUL_ORB_VAMPIRE.get(), 10, 10, 4),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.HUNTER_COAT_CHEST_NORMAL.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.HUNTER_COAT_LEGS_NORMAL.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.HUNTER_COAT_FEET_NORMAL.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.HUNTER_COAT_HEAD_NORMAL.get(), new Trades.Price(1, 1), 6, 1),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), new Trades.Price(1, 1), 6, 1)
                },
                3, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForSouls(new Trades.Price(40, 64), ModItems.VAMPIRE_BOOK.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), new Trades.Price(1, 3)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.OBSIDIAN_ARMOR_CHEST_NORMAL.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.OBSIDIAN_ARMOR_LEGS_NORMAL.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.OBSIDIAN_ARMOR_FEET_NORMAL.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.OBSIDIAN_ARMOR_HEAD_NORMAL.get(), new Trades.Price(1, 1), 6, 1),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.HUNTER_COAT_CHEST_ENHANCED.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.HUNTER_COAT_LEGS_ENHANCED.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.HUNTER_COAT_FEET_ENHANCED.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.HUNTER_COAT_HEAD_ENHANCED.get(), new Trades.Price(1, 1), 6, 1),
                        new Trades.ItemsForSouls(new Trades.Price(30, 45), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 45), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(15, 30), ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get(), new Trades.Price(1, 1), 6, 1)
                },
                4, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForSouls(new Trades.Price(20, 32), Items.DIAMOND, new Trades.Price(1, 2)),
                        new Trades.ItemsForSouls(new Trades.Price(15, 25), ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get(), new Trades.Price(1, 2)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.OBSIDIAN_ARMOR_CHEST_ENHANCED.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.OBSIDIAN_ARMOR_LEGS_ENHANCED.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.OBSIDIAN_ARMOR_FEET_ENHANCED.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.OBSIDIAN_ARMOR_HEAD_ENHANCED.get(), new Trades.Price(1, 1), 6, 1),
                        new Trades.ItemsForSouls(new Trades.Price(30, 55), ModItems.HUNTER_COAT_CHEST_ULTIMATE.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 55), ModItems.HUNTER_COAT_LEGS_ULTIMATE.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 35), ModItems.HUNTER_COAT_FEET_ULTIMATE.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 35), ModItems.HUNTER_COAT_HEAD_ULTIMATE.get(), new Trades.Price(1, 1), 6, 1)
                },
                5, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForSouls(new Trades.Price(30, 64), ModItems.OBSIDIAN_ARMOR_CHEST_ULTIMATE.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 64), ModItems.OBSIDIAN_ARMOR_LEGS_ULTIMATE.get(), new Trades.Price(1, 1), 9, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 40), ModItems.OBSIDIAN_ARMOR_FEET_ULTIMATE.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 40), ModItems.OBSIDIAN_ARMOR_HEAD_ULTIMATE.get(), new Trades.Price(1, 1), 8, 1)
                });
    }

    private static Map<Integer, VillagerTrades.ItemListing[]> getVampireTrades() {
        return ImmutableMap.of(
                1, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForHeart(new Trades.Price(10, 15), ModItems.PURE_BLOOD_0.get(), new Trades.Price(1, 1)),
                        new Trades.BloodBottleForHeart(new Trades.Price(3, 12), new Trades.Price(1, 15), 9),
                        new VillagerTrades.EmeraldForItems(ModBlocks.VAMPIRE_ORCHID.get(), 4, 1, 3),
                        new Trades.BiomeMapForEmeralds(5, ModBiomes.VAMPIRE_FOREST.getKey(), 3, 2)
                },
                2, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForHeart(new Trades.Price(25, 30), ModItems.PURE_BLOOD_1.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(1, 5), ModBlocks.COFFIN.get(), new Trades.Price(1, 1), 2, 2),
                        new Trades.ItemsForHeart(new Trades.Price(10, 25), ModItems.BLOOD_INFUSED_IRON_INGOT.get(), new Trades.Price(1, 3))
                },
                3, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForHeart(new Trades.Price(30, 40), ModItems.PURE_BLOOD_2.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(15, 30), ModItems.BLOOD_INFUSED_ENHANCED_IRON_INGOT.get(), new Trades.Price(1, 2))
                },
                4, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForHeart(new Trades.Price(20, 30), ModItems.PURE_BLOOD_3.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(10, 30), new ItemStack[]{
                                new ItemStack(ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_BLACK_RED.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_RED_BLACK.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get())}, new Trades.Price(1, 1), 10, 2)
                },
                5, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForHeart(new Trades.Price(30, 40), ModItems.PURE_BLOOD_4.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(10, 30), new ItemStack[]{
                                new ItemStack(ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_BLACK_RED.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_RED_BLACK.get()),
                                new ItemStack(ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get())}, new Trades.Price(1, 1), 10, 2),
                        new VillagerTrades.ItemsForEmeralds(ModItems.HEART_SEEKER_ULTIMATE.get(), 40, 1, 15),
                        new VillagerTrades.ItemsForEmeralds(ModItems.HEART_STRIKER_ULTIMATE.get(), 40, 1, 15)

                });
    }
}
