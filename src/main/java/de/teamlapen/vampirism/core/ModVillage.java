package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.entity.ai.sensing.VampireVillagerHostilesSensor;
import de.teamlapen.vampirism.entity.villager.Trades;
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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class ModVillage {
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, REFERENCE.MODID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, REFERENCE.MODID);
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, REFERENCE.MODID);
    public static final DeferredRegister<Schedule> SCHEDULES = DeferredRegister.create(ForgeRegistries.SCHEDULES, REFERENCE.MODID);

    public static final RegistryObject<PoiType> HUNTER_TOTEM = POI_TYPES.register("hunter_totem", () -> new PoiType(getAllStates(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED.get()), 1, 1));
    public static final RegistryObject<PoiType> VAMPIRE_TOTEM = POI_TYPES.register("vampire_totem", () -> new PoiType(getAllStates(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED.get()), 1, 1));
    public static final RegistryObject<PoiType> NO_FACTION_TOTEM = POI_TYPES.register("no_faction_totem", () -> new PoiType(getAllStates(ModBlocks.TOTEM_TOP.get(), ModBlocks.TOTEM_TOP_CRAFTED.get()), 1, 1));
    public static final RegistryObject<PoiType> ALTAR_CLEANSING = POI_TYPES.register("church_altar", () -> new PoiType(getAllStates(ModBlocks.ALTAR_CLEANSING.get()), 1, 1));

    public static final RegistryObject<SensorType<VampireVillagerHostilesSensor>> VAMPIRE_VILLAGER_HOSTILES = SENSOR_TYPES.register("vampire_villager_hostiles", () -> new SensorType<>(VampireVillagerHostilesSensor::new));

    public static final RegistryObject<Schedule> CONVERTED_DEFAULT = SCHEDULES.register("converted_default", () ->
            new ScheduleBuilder(new Schedule()).changeActivityAt(12000, Activity.IDLE).changeActivityAt(10, Activity.REST).changeActivityAt(14000, Activity.WORK).changeActivityAt(21000, Activity.MEET).changeActivityAt(23000, Activity.IDLE).build());


    public static final RegistryObject<VillagerProfession> VAMPIRE_EXPERT = PROFESSIONS.register("vampire_expert", () -> new VillagerProfession("vampire_expert", (holder) -> holder.is(ModTags.PoiTypes.IS_VAMPIRE), (holder) -> holder.is(ModTags.PoiTypes.IS_VAMPIRE), ImmutableSet.of(), ImmutableSet.of(), null));
    public static final RegistryObject<VillagerProfession> HUNTER_EXPERT = PROFESSIONS.register("hunter_expert", () -> new VillagerProfession("hunter_expert", (holder) -> holder.is(ModTags.PoiTypes.IS_HUNTER), (holder) -> holder.is(ModTags.PoiTypes.IS_HUNTER), ImmutableSet.of(), ImmutableSet.of(), null));
    public static final RegistryObject<VillagerProfession> PRIEST = PROFESSIONS.register("priest", () -> new VillagerProfession("priest", holder -> holder.is(ALTAR_CLEANSING.getKey()), holder -> holder.is(ALTAR_CLEANSING.getKey()), ImmutableSet.of(), ImmutableSet.of(), ModSounds.BLESSING_MUSIC.get()));

    static void register(IEventBus bus) {
        POI_TYPES.register(bus);
        PROFESSIONS.register(bus);
        SENSOR_TYPES.register(bus);
        SCHEDULES.register(bus);
    }

    public static void villagerTradeSetup() {
        VillagerTrades.TRADES.computeIfAbsent(VAMPIRE_EXPERT.get(), trades -> new Int2ObjectOpenHashMap<>()).putAll(getVampireTrades());
        VillagerTrades.TRADES.computeIfAbsent(HUNTER_EXPERT.get(), trades -> new Int2ObjectOpenHashMap<>()).putAll(getHunterTrades());
        VillagerTrades.TRADES.computeIfAbsent(PRIEST.get(), trades -> new Int2ObjectOpenHashMap<>()).putAll(getPriestTrades());

    }

    private static Set<BlockState> getAllStates(Block @NotNull ... blocks) {
        return Arrays.stream(blocks).flatMap(block -> block.getStateDefinition().getPossibleStates().stream()).collect(ImmutableSet.toImmutableSet());
    }

    private static @NotNull Map<Integer, VillagerTrades.ItemListing[]> getHunterTrades() {
        return ImmutableMap.of(
                1, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForSouls(new Trades.Price(10, 20), ModItems.ITEM_GARLIC.get(), new Trades.Price(2, 5)),
                        new Trades.ItemsForSouls(new Trades.Price(30, 40), ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 35), ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(10, 15), ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 30), ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), new Trades.Price(1, 1), 6, 1),
                        new Trades.VampireForestMapTrade(5, 3, 2)
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
                },
                5, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForSouls(new Trades.Price(30, 55), ModItems.HUNTER_COAT_CHEST_ULTIMATE.get(), new Trades.Price(1, 1), 8, 1),
                        new Trades.ItemsForSouls(new Trades.Price(25, 55), ModItems.HUNTER_COAT_LEGS_ULTIMATE.get(), new Trades.Price(1, 1), 7, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 35), ModItems.HUNTER_COAT_FEET_ULTIMATE.get(), new Trades.Price(1, 1), 5, 1),
                        new Trades.ItemsForSouls(new Trades.Price(20, 35), ModItems.HUNTER_COAT_HEAD_ULTIMATE.get(), new Trades.Price(1, 1), 6, 1)
                });
    }

    private static @NotNull Map<Integer, VillagerTrades.ItemListing[]> getPriestTrades() {
        return ImmutableMap.of(
                1, new VillagerTrades.ItemListing[]{
                        new VillagerTrades.EmeraldForItems(ModItems.PURE_SALT.get(), 25, 2, 4),
                        new VillagerTrades.EmeraldForItems(ModItems.ITEM_GARLIC.get(), 30, 6, 2),

                },
                2, new VillagerTrades.ItemListing[]{
                        new VillagerTrades.ItemsForEmeralds(ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), 3, 5, 4),
                        new VillagerTrades.EmeraldForItems(ModItems.SOUL_ORB_VAMPIRE.get(), 10, 10, 4),
                        new VillagerTrades.EmeraldForItems(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 9, 4, 5),
                        new VillagerTrades.ItemsForEmeralds(ModItems.CRUCIFIX_NORMAL.get(), 1, 1, 1)
                },
                3, new VillagerTrades.ItemListing[]{
                        new VillagerTrades.ItemsForEmeralds(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), 2, 5, 4),


                },
                4, new VillagerTrades.ItemListing[]{
                        new VillagerTrades.ItemsForEmeralds(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get(), 1, 4, 4),

                },
                5, new VillagerTrades.ItemListing[]{
                        new VillagerTrades.ItemsForEmeralds(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), 3, 4, 4),

                });
    }

    private static @NotNull Map<Integer, VillagerTrades.ItemListing[]> getVampireTrades() {
        return ImmutableMap.of(
                1, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForHeart(new Trades.Price(10, 15), ModItems.PURE_BLOOD_0.get(), new Trades.Price(1, 1)),
                        new Trades.BloodBottleForHeart(new Trades.Price(3, 12), new Trades.Price(1, 15), 9),
                        new VillagerTrades.EmeraldForItems(ModBlocks.VAMPIRE_ORCHID.get(), 4, 1, 3),
                        new Trades.VampireForestMapTrade(5, 3, 2)
                },
                2, new VillagerTrades.ItemListing[]{
                        new Trades.ItemsForHeart(new Trades.Price(25, 30), ModItems.PURE_BLOOD_1.get(), new Trades.Price(1, 1), 10, 1),
                        new Trades.ItemsForHeart(new Trades.Price(1, 5), ModBlocks.COFFIN_RED.get(), new Trades.Price(1, 1), 2, 2),
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
