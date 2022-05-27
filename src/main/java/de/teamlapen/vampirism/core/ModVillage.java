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

    public static final RegistryObject<FactionPointOfInterestType> hunter_faction = POI_TYPES.register("hunter_faction", () -> (FactionPointOfInterestType) PoiType.registerBlockStates(new FactionPointOfInterestType("hunter_faction", getAllStates(ModBlocks.totem_top_vampirism_hunter.get(), ModBlocks.totem_top_vampirism_hunter_crafted.get()), 1, 1)));
    public static final RegistryObject<FactionPointOfInterestType> vampire_faction = POI_TYPES.register("vampire_faction", () -> (FactionPointOfInterestType) PoiType.registerBlockStates(new FactionPointOfInterestType("vampire_faction", getAllStates(ModBlocks.totem_top_vampirism_vampire.get(), ModBlocks.totem_top_vampirism_vampire_crafted.get()), 1, 1)));
    public static final RegistryObject<FactionPointOfInterestType> no_faction = POI_TYPES.register("no_faction", () -> (FactionPointOfInterestType) PoiType.registerBlockStates(new FactionPointOfInterestType("no_faction", getAllStates(ModBlocks.totem_top.get(), ModBlocks.totem_top_crafted.get()), 1, 1)));

    public static final RegistryObject<SensorType<VampireVillagerHostilesSensor>> vampire_villager_hostiles = SENSOR_TYPES.register("vampire_villager_hostiles", () -> new SensorType<>(VampireVillagerHostilesSensor::new));

    public static final RegistryObject<Schedule> converted_default = SCHEDULES.register("converted_default", () ->
            new ScheduleBuilder(new Schedule()).changeActivityAt(12000, Activity.IDLE).changeActivityAt(10, Activity.REST).changeActivityAt(14000, Activity.WORK).changeActivityAt(21000, Activity.MEET).changeActivityAt(23000, Activity.IDLE).build());


    public static final RegistryObject<VillagerProfession> vampire_expert = PROFESSIONS.register("vampire_expert", () -> new FactionVillagerProfession("vampire_expert", vampire_faction.get(), ImmutableSet.of(), ImmutableSet.of(), null) {
        @Override
        public IFaction<?> getFaction() {
            return VReference.VAMPIRE_FACTION;
        }
    });
    public static final RegistryObject<VillagerProfession> hunter_expert = PROFESSIONS.register("hunter_expert", () -> new FactionVillagerProfession("hunter_expert", hunter_faction.get(), ImmutableSet.of(), ImmutableSet.of(), null) {
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
        VillagerTrades.TRADES.computeIfAbsent(vampire_expert.get(), trades -> new Int2ObjectOpenHashMap<>()).putAll(getVampireTrades());
        VillagerTrades.TRADES.computeIfAbsent(hunter_expert.get(), trades -> new Int2ObjectOpenHashMap<>()).putAll(getHunterTrades());
    }

    private static Set<BlockState> getAllStates(Block... blocks) {
        return Arrays.stream(blocks).map(block -> block.getStateDefinition().any()).collect(ImmutableSet.toImmutableSet());
    }

    private static Map<Integer, VillagerTrades.ItemListing[]> getHunterTrades() {
        return ImmutableMap.of(
                1, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.item_garlic.get(), new Trades.Price(2, 5)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.armor_of_swiftness_chest_normal.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.armor_of_swiftness_legs_normal.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.armor_of_swiftness_feet_normal.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.armor_of_swiftness_head_normal.get(), new Trades.Price(1, 1), 6, 1),
                        new Trades.BiomeMapForEmeralds(5, ModBiomes.VAMPIRE_FOREST.getKey(), 3, 2)
                },
                2, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForSouls(new Trades.Price(25, 45), Items.DIAMOND, new Trades.Price(1, 1), 2, 5),
                        new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.crossbow_arrow_normal.get(), new Trades.Price(5, 15)),
                        new VillagerTrades.ItemsForEmeralds(ModItems.soul_orb_vampire.get(), 10, 10, 4),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.hunter_coat_chest_normal.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.hunter_coat_legs_normal.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.hunter_coat_feet_normal.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.hunter_coat_head_normal.get(), new Trades.Price(1, 1), 6, 1),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.armor_of_swiftness_chest_enhanced.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.armor_of_swiftness_legs_enhanced.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.armor_of_swiftness_feet_enhanced.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.armor_of_swiftness_head_enhanced.get(), new Trades.Price(1, 1), 6, 1)
                },
                3, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForSouls(new Trades.Price(40, 64), ModItems.vampire_book.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.holy_water_bottle_enhanced.get(), new Trades.Price(1, 3)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.obsidian_armor_chest_normal.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.obsidian_armor_legs_normal.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.obsidian_armor_feet_normal.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.obsidian_armor_head_normal.get(), new Trades.Price(1, 1), 6, 1),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.hunter_coat_chest_enhanced.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.hunter_coat_legs_enhanced.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.hunter_coat_feet_enhanced.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.hunter_coat_head_enhanced.get(), new Trades.Price(1, 1), 6, 1),
                        new Trades.ItemsForSouls(new Trades.Price(30, 45), ModItems.armor_of_swiftness_chest_ultimate.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 45), ModItems.armor_of_swiftness_legs_ultimate.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(15, 30), ModItems.armor_of_swiftness_feet_ultimate.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.armor_of_swiftness_head_ultimate.get(), new Trades.Price(1, 1), 6, 1)
                },
                4, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForSouls(new Trades.Price(20, 32), Items.DIAMOND, new Trades.Price(1, 2)),
                        new Trades.ItemsForSouls(new Trades.Price(15, 25), ModItems.holy_water_bottle_ultimate.get(), new Trades.Price(1, 2)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.obsidian_armor_chest_enhanced.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.obsidian_armor_legs_enhanced.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.obsidian_armor_feet_enhanced.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.obsidian_armor_head_enhanced.get(), new Trades.Price(1, 1), 6, 1),
                        new Trades.ItemsForSouls(new Trades.Price(30, 55), ModItems.hunter_coat_chest_ultimate.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 55), ModItems.hunter_coat_legs_ultimate.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 35), ModItems.hunter_coat_feet_ultimate.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 35), ModItems.hunter_coat_head_ultimate.get(), new Trades.Price(1, 1), 6, 1)
                },
                5, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForSouls(new Trades.Price(30, 64), ModItems.obsidian_armor_chest_ultimate.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 64), ModItems.obsidian_armor_legs_ultimate.get(), new Trades.Price(1, 1), 9, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 40), ModItems.obsidian_armor_feet_ultimate.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 40), ModItems.obsidian_armor_head_ultimate.get(), new Trades.Price(1, 1), 8, 1)
                });
    }

    private static Map<Integer, VillagerTrades.ItemListing[]> getVampireTrades() {
        return ImmutableMap.of(
                1, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForHeart(new Trades.Price(10, 15), ModItems.pure_blood_0.get(), new Trades.Price(1, 1)),
                        new Trades.BloodBottleForHeart(new Trades.Price(3, 12), new Trades.Price(1, 15), 9),
                        new VillagerTrades.EmeraldForItems(ModBlocks.vampire_orchid.get(), 4, 1, 3),
                        new Trades.BiomeMapForEmeralds(5, ModBiomes.VAMPIRE_FOREST.getKey(), 3, 2)
                },
                2, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForHeart(new Trades.Price(25, 30), ModItems.pure_blood_1.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(1, 5), ModBlocks.coffin.get(), new Trades.Price(1, 1), 2, 2),
                        new Trades.ItemsForHeart(new Trades.Price(10, 25), ModItems.blood_infused_iron_ingot.get(), new Trades.Price(1, 3))
                },
                3, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForHeart(new Trades.Price(30, 40), ModItems.pure_blood_2.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(15, 30), ModItems.blood_infused_enhanced_iron_ingot.get(), new Trades.Price(1, 2))
                },
                4, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForHeart(new Trades.Price(20, 30), ModItems.pure_blood_3.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(10, 30), new ItemStack[]{
                                new ItemStack(ModItems.vampire_cloak_black_blue.get()),
                                new ItemStack(ModItems.vampire_cloak_black_red.get()),
                                new ItemStack(ModItems.vampire_cloak_black_white.get()),
                                new ItemStack(ModItems.vampire_cloak_red_black.get()),
                                new ItemStack(ModItems.vampire_cloak_white_black.get())}, new Trades.Price(1, 1), 10, 2)
                },
                5, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForHeart(new Trades.Price(30, 40), ModItems.pure_blood_4.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(10, 30), new ItemStack[]{
                                new ItemStack(ModItems.vampire_cloak_black_blue.get()),
                                new ItemStack(ModItems.vampire_cloak_black_red.get()),
                                new ItemStack(ModItems.vampire_cloak_black_white.get()),
                                new ItemStack(ModItems.vampire_cloak_red_black.get()),
                                new ItemStack(ModItems.vampire_cloak_white_black.get())}, new Trades.Price(1, 1), 10, 2),
                        new VillagerTrades.ItemsForEmeralds(ModItems.heart_seeker_ultimate.get(), 40, 1, 15),
                        new VillagerTrades.ItemsForEmeralds(ModItems.heart_striker_ultimate.get(), 40, 1, 15)

                });
    }
}
