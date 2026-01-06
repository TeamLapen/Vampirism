package de.teamlapen.vampirism.common.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import de.teamlapen.faction.common.core.FactionBlocks;
import de.teamlapen.faction.common.util.MapUtil;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.tags.ModPoiTypeTags;
import de.teamlapen.vampirism.common.tags.ModStructureTags;
import de.teamlapen.vampirism.common.util.ColorListsUtil;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.entity.villager.VampirismTrades;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.attribute.AttributeTypes;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.timeline.Timeline;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

public class ModVillage {
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, REFERENCE.MODID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, REFERENCE.MODID);
    public static final DeferredRegister<EnvironmentAttribute<?>> SCHEDULES = DeferredRegister.create(Registries.ENVIRONMENT_ATTRIBUTE, REFERENCE.MODID);

    public static final DeferredHolder<PoiType, PoiType> HUNTER_TOTEM = POI_TYPES.register("hunter_totem", () -> new PoiType(getAllStates(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED.get()), 1, 1));
    public static final DeferredHolder<PoiType, PoiType> VAMPIRE_TOTEM = POI_TYPES.register("vampire_totem", () -> new PoiType(getAllStates(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED.get()), 1, 1));
    public static final DeferredHolder<PoiType, PoiType> NO_FACTION_TOTEM = POI_TYPES.register("no_faction_totem", () -> new PoiType(getAllStates(FactionBlocks.TOTEM_TOP.get(), FactionBlocks.TOTEM_TOP_CRAFTED.get()), 1, 1));
    public static final DeferredHolder<PoiType, PoiType> ALTAR_CLEANSING = POI_TYPES.register("altar_cleansing", () -> new PoiType(getAllStates(ModBlocks.ALTAR_CLEANSING.get()), 1, 1));
    public static final DeferredHolder<PoiType, PoiType> CREEPER_REPELLENT = POI_TYPES.register("creeper_repellent", () -> new PoiType(getAllStates(ModBlocks.VAMPIRE_SOUL_LANTERN.get()), 1, 1));

    public static final DeferredHolder<EnvironmentAttribute<?>, EnvironmentAttribute<Activity>> CONVERTED_DEFAULT = SCHEDULES.register("converted_default", () -> EnvironmentAttribute.builder(AttributeTypes.ACTIVITY).defaultValue(Activity.IDLE).build());

    public static final ResourceKey<Timeline> VAMPIRE_VILLAGER_SCHEDULE = ResourceKey.create(Registries.TIMELINE, VIdentifier.mod("vampire_villager_schedule"));

    public static final DeferredHolder<VillagerProfession, VillagerProfession> VAMPIRE_EXPERT = PROFESSIONS.register("vampire_expert", () -> new VillagerProfession(Component.translatable(REFERENCE.MODID + ":vampire_expert"), (holder) -> holder.is(ModPoiTypeTags.IS_VAMPIRE), (holder) -> holder.is(ModPoiTypeTags.IS_VAMPIRE), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_CARTOGRAPHER));
    public static final DeferredHolder<VillagerProfession, VillagerProfession> HUNTER_EXPERT = PROFESSIONS.register("hunter_expert", () -> new VillagerProfession(Component.translatable(REFERENCE.MODID + ":hunter_expert"), (holder) -> holder.is(ModPoiTypeTags.IS_HUNTER), (holder) -> holder.is(ModPoiTypeTags.IS_HUNTER), ImmutableSet.of(), ImmutableSet.of(ModBlocks.HUNTER_TABLE.get(), ModBlocks.WEAPON_TABLE.get(), ModBlocks.GARLIC.get()), SoundEvents.VILLAGER_WORK_ARMORER));
    @SuppressWarnings("ConstantConditions")
    public static final DeferredHolder<VillagerProfession, VillagerProfession> PRIEST = PROFESSIONS.register("priest", () -> new VillagerProfession(Component.translatable(REFERENCE.MODID + ":priest"), holder -> holder.is(ALTAR_CLEANSING.getKey()), holder -> holder.is(ALTAR_CLEANSING.getKey()), ImmutableSet.of(), ImmutableSet.of(), ModSounds.BLESSING_MUSIC.get()));

    static void register(IEventBus bus) {
        POI_TYPES.register(bus);
        PROFESSIONS.register(bus);
        SCHEDULES.register(bus);
    }

    static void createTimelines(BootstrapContext<Timeline> bootstrapContext) {
        bootstrapContext.register(VAMPIRE_VILLAGER_SCHEDULE, Timeline.builder()
                .setPeriodTicks(24000)
                .addTrack(CONVERTED_DEFAULT.get(),
                        builder -> builder
                                .addKeyframe(10, Activity.REST)
                                .addKeyframe(12000, Activity.IDLE)
                                .addKeyframe(14000, Activity.WORK)
                                .addKeyframe(21000, Activity.MEET)
                                .addKeyframe(23000, Activity.IDLE)
                )
                .build());
    }

    private static Set<BlockState> getAllStates(Block @NotNull ... blocks) {
        return Arrays.stream(blocks).flatMap(block -> block.getStateDefinition().getPossibleStates().stream()).collect(ImmutableSet.toImmutableSet());
    }

    public static void villagerTradeSetup() {
        addTrades(VAMPIRE_EXPERT.getKey(), getVampireExpertTrades());
        addTrades(HUNTER_EXPERT.getKey(), getHunterExpertTrades());
        addTrades(PRIEST.getKey(), getPriestTrades());

        injectTrades(VillagerProfession.FARMER, getFarmerTrades());
        injectTrades(VillagerProfession.BUTCHER, getButcherTrades());
        injectTrades(VillagerProfession.MASON, getMasonTrades());

        ArrayList<Pair<VillagerTrades.ItemListing[], Integer>> pairs = new ArrayList<>(VillagerTrades.WANDERING_TRADER_TRADES);
        pairs.set(1, Pair.of(Stream.concat(Arrays.stream(pairs.get(1).getLeft()), Arrays.stream(getWanderingTraderTrades().get(1))).toArray(VillagerTrades.ItemListing[]::new), pairs.get(1).getRight()));
        VillagerTrades.WANDERING_TRADER_TRADES = pairs;
    }

    public static Int2ObjectMap<VillagerTrades.ItemListing[]> getVampireExpertTrades() {
        return new Int2ObjectOpenHashMap<>(ImmutableMap.of(
                1,
                new VillagerTrades.ItemListing[]{
                        new VampirismTrades.ItemsForHeart(new VampirismTrades.Price(10, 15), ModItems.PURE_BLOOD_0.get(), 1, 2, 2),
                        // When buying, the player only gets one bottle and not the amount set in the offer for some reason
                        //new VampirismTrades.BloodBottleForHeart(new VampirismTrades.Price(3, 12), new VampirismTrades.Price(1, 15), 9, 4, 1),
                        new VillagerTrades.EmeraldForItems(ModBlocks.VAMPIRE_ORCHID.get(), 9, 8, 2),
                        new VampirismTrades.VampireForestMapTrade(7, MapUtil.getModTranslation("vampire_forest"), MapDecorationTypes.TARGET_POINT, 12, 2)
                },
                2,
                new VillagerTrades.ItemListing[]{
                        new VampirismTrades.ItemsForHeart(new VampirismTrades.Price(25, 30), ModItems.PURE_BLOOD_1.get(), 1, 2, 5),
                        new VampirismTrades.ItemsForHeart(new VampirismTrades.Price(10, 25), ModItems.BLOOD_INFUSED_IRON_INGOT.get(), new VampirismTrades.Price(1, 3), 8, 5),
                        new VampirismTrades.ItemsForHeart(new VampirismTrades.Price(5, 20), ColorListsUtil.itemListToItemStacks(ColorListsUtil.VAMPIRE_CLOAKS.values().stream().toList()), 1, 4, 5),
                        new VampirismTrades.ItemsForHeart(new VampirismTrades.Price(3, 15), ColorListsUtil.itemListToItemStacks(ColorListsUtil.COFFINS.stream().map(Block::asItem).toList()), 1, 4, 5),
                },
                3,
                new VillagerTrades.ItemListing[]{
                        new VampirismTrades.ItemsForHeart(new VampirismTrades.Price(30, 40), ModItems.PURE_BLOOD_2.get(), 1, 2, 10),
                        new VampirismTrades.ItemsForHeart(new VampirismTrades.Price(15, 30), ModItems.BLOOD_INFUSED_IRON_INGOT.get(), new VampirismTrades.Price(1, 2), 12, 10),
                        new VampirismTrades.MultipleItemsForCurrency(new VampirismTrades.ItemsForCurrency[]{
                                new VampirismTrades.ItemsForHeart(new VampirismTrades.Price(42, 64), ModItems.HEART_SEEKER_ENHANCED.get(), 1, 1, 10),
                                new VampirismTrades.ItemsForHeart(new VampirismTrades.Price(42, 64), ModItems.HEART_STRIKER_ENHANCED.get(), 1, 1, 10)
                        }),
                        new VampirismTrades.ItemsForHeart(new VampirismTrades.Price(10, 30), ColorListsUtil.itemListToItemStacks(ColorListsUtil.VAMPIRE_CLOAKS.values().stream().toList()), 1, 4, 10)
                },
                4,
                new VillagerTrades.ItemListing[]{
                        new VampirismTrades.ItemsForHeart(new VampirismTrades.Price(20, 30), ModItems.PURE_BLOOD_3.get(), 1, 2, 15),
                        new VampirismTrades.TreasureMapForEmeralds(14, ModStructureTags.ON_CRYPT_MAPS, MapUtil.getModTranslation("crypt"), ModMapDecorations.CRYPT, 12, 15)
                },
                5,
                new VillagerTrades.ItemListing[]{
                        new VampirismTrades.ItemsForHeart(new VampirismTrades.Price(30, 40), ModItems.PURE_BLOOD_4.get(), 1, 2, 30),
                        new VampirismTrades.ItemsForHeart(new VampirismTrades.Price(80, 96), ModItems.HEART_SEEKER_ULTIMATE.get(), 1, 3, 30),
                        new VampirismTrades.ItemsForHeart(new VampirismTrades.Price(80, 96), ModItems.HEART_STRIKER_ULTIMATE.get(), 1, 3, 30)
                }
        ));
    }

    public static Int2ObjectMap<VillagerTrades.ItemListing[]> getHunterExpertTrades() {
        return new Int2ObjectOpenHashMap<>(ImmutableMap.of(
                1,
                new VillagerTrades.ItemListing[]{
                        new VillagerTrades.EmeraldForItems(ModBlocks.GARLIC.asItem(), 22, 16, 2),
                        new VampirismTrades.BiomeMapForEmeralds(7, MapUtil.getModTranslation("vampire_forest"), MapDecorationTypes.TARGET_POINT, 12, 1)
                },
                2,
                new VillagerTrades.ItemListing[]{
                        new VillagerTrades.EmeraldForItems(ModItems.VAMPIRE_FANG.get(), 9, 16, 10),
                        new VampirismTrades.MultipleItemsForCurrency(new VampirismTrades.ItemsForCurrency[]{
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(10, 20), ModItems.CROSSBOW_ARROW_NORMAL.get(), new VampirismTrades.Price(8, 16), 12, 5),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(15, 30), ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get(), new VampirismTrades.Price(8, 16), 12, 5),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(20, 35), ModItems.CROSSBOW_ARROW_SPITFIRE.get(), new VampirismTrades.Price(8, 16), 12, 5),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(25, 40), ModItems.CROSSBOW_ARROW_TELEPORT.get(), new VampirismTrades.Price(6, 12), 12, 5)
                        }),
                        new VampirismTrades.MultipleItemsForCurrency(new VampirismTrades.ItemsForCurrency[]{
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(30, 40), ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), 1, 8, 5),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(25, 35), ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), 1, 7, 5),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(10, 15), ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get(), 1, 5, 5),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(20, 30), ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), 1, 6, 5)
                        })
                },
                3,
                new VillagerTrades.ItemListing[]{
                        new VillagerTrades.EmeraldForItems(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 5, 16, 20),
                        new VampirismTrades.MultipleItemsForCurrency(new VampirismTrades.ItemsForCurrency[]{
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(30, 40), ModItems.HUNTER_COAT_CHEST_NORMAL.get(), 1, 8, 10),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(25, 35), ModItems.HUNTER_COAT_LEGS_NORMAL.get(), 1, 7, 10),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(10, 15), ModItems.HUNTER_COAT_FEET_NORMAL.get(), 1, 5, 10),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(20, 30), ModItems.HUNTER_COAT_HEAD_NORMAL.get(), 1, 6, 10)
                        }),
                        new VampirismTrades.MultipleItemsForCurrency(new VampirismTrades.ItemsForCurrency[]{
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(30, 40), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), 1, 8, 10),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(25, 35), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), 1, 7, 10),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(10, 15), ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get(), 1, 5, 10),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(20, 30), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), 1, 6, 10)
                        })
                },
                4,
                new VillagerTrades.ItemListing[]{
                        new VampirismTrades.MultipleItemsForCurrency(new VampirismTrades.ItemsForCurrency[]{
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(30, 40), ModItems.HUNTER_COAT_CHEST_ENHANCED.get(), 1, 8, 15),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(25, 35), ModItems.HUNTER_COAT_LEGS_ENHANCED.get(), 1, 7, 15),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(10, 15), ModItems.HUNTER_COAT_FEET_ENHANCED.get(), 1, 5, 15),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(20, 30), ModItems.HUNTER_COAT_HEAD_ENHANCED.get(), 1, 6, 15)
                        }),
                        new VampirismTrades.MultipleItemsForCurrency(new VampirismTrades.ItemsForCurrency[]{
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(30, 45), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get(), 1, 8, 15),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(25, 45), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get(), 1, 7, 15),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(15, 30), ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get(), 1, 5, 15),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(20, 30), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get(), 1, 6, 15)
                        })
                },
                5,
                new VillagerTrades.ItemListing[]{
                        new VampirismTrades.MultipleItemsForCurrency(new VampirismTrades.ItemsForCurrency[]{
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(30, 55), ModItems.HUNTER_COAT_CHEST_ULTIMATE.get(), 1, 8, 30),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(25, 55), ModItems.HUNTER_COAT_LEGS_ULTIMATE.get(), 1, 7, 30),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(20, 35), ModItems.HUNTER_COAT_FEET_ULTIMATE.get(), 1, 5, 30),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(20, 35), ModItems.HUNTER_COAT_HEAD_ULTIMATE.get(), 1, 6, 30)
                        }),
                        new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(80, 96), ModItems.HUNTER_AXE_ULTIMATE.get(), 1, 3, 30),
                        new VampirismTrades.MultipleItemsForCurrency(new VampirismTrades.ItemsForCurrency[]{
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(25, 50), ModItems.ENHANCED_CROSSBOW.get(), 1, 3, 30),
                                new VampirismTrades.ItemsForSouls(new VampirismTrades.Price(32, 64), ModItems.ENHANCED_DOUBLE_CROSSBOW.get(), 1, 3, 30)
                        }),
                }
        ));
    }

    public static Int2ObjectMap<VillagerTrades.ItemListing[]> getPriestTrades() {
        return new Int2ObjectOpenHashMap<>(ImmutableMap.of(
                1,
                new VillagerTrades.ItemListing[]{
                        new VillagerTrades.EmeraldForItems(ModItems.PURE_SALT.get(), 25, 12, 2),
                        new VillagerTrades.EmeraldForItems(ModBlocks.GARLIC.asItem(), 22, 16, 2),
                        new VillagerTrades.ItemsForEmeralds(Items.CANDLE, 1, 4, 12, 1)
                },
                2,
                new VillagerTrades.ItemListing[]{
                        new VillagerTrades.EmeraldForItems(Items.HONEYCOMB, 12, 12, 10),
                        new VillagerTrades.ItemsForEmeralds(ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), 2, 5, 5)
                },
                3,
                new VillagerTrades.ItemListing[]{
                        new VillagerTrades.EmeraldForItems(Items.GOLD_INGOT, 3, 12, 20),
                        new VillagerTrades.ItemsForEmeralds(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), 3, 4, 10),
                        new VampirismTrades.ItemsForEmeraldPrice(new VampirismTrades.Price(2, 4), ModItems.CRUCIFIX_NORMAL.get(), 1, 8, 10)
                },
                4,
                new VillagerTrades.ItemListing[]{
                        new VillagerTrades.ItemsForEmeralds(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get(), 4, 3, 15),
                        new VampirismTrades.ItemsForEmeraldPrice(new VampirismTrades.Price(8, 16), ModItems.CRUCIFIX_ENHANCED.get(), 1, 4, 15)
                },
                5,
                new VillagerTrades.ItemListing[]{
                        new VillagerTrades.ItemsForEmeralds(ModBlocks.CANDELABRA_NORMAL.asItem(), 2, 1, 12, 30),
                        new VampirismTrades.ItemsForEmeraldPrice(new VampirismTrades.Price(24, 32), ModItems.CRUCIFIX_ULTIMATE.get(), 1, 4, 30)
                }
        ));
    }

    public static Int2ObjectMap<VillagerTrades.ItemListing[]> getFarmerTrades() {
        return new Int2ObjectOpenHashMap<>(ImmutableMap.of(
                1,
                new VillagerTrades.ItemListing[]{
                        nonVampire(new VillagerTrades.EmeraldForItems(ModBlocks.GARLIC.asItem(), 22, 16, 2))
                }
        ));
    }

    public static Int2ObjectMap<VillagerTrades.ItemListing[]> getButcherTrades() {
        return new Int2ObjectOpenHashMap<>(ImmutableMap.of(
                3,
                new VillagerTrades.ItemListing[]{
                        onlyVampire(new VillagerTrades.EmeraldForItems(ModItems.HUMAN_HEART.get(), 6, 16, 20))
                }
        ));
    }

    public static Int2ObjectMap<VillagerTrades.ItemListing[]> getMasonTrades() {
        return new Int2ObjectOpenHashMap<>(ImmutableMap.of(
                2,
                new VillagerTrades.ItemListing[]{
                        onlyVampire(new VillagerTrades.EmeraldForItems(ModBlocks.DARK_STONE.get(), 16, 16, 10)),
                        onlyVampire(new VillagerTrades.ItemsForEmeralds(ModBlocks.CHISELED_DARK_STONE_BRICKS.get(), 1, 4, 16, 5))
                }
        ));
    }

    public static Int2ObjectMap<VillagerTrades.ItemListing[]> getWanderingTraderTrades() {
        return new Int2ObjectOpenHashMap<>(ImmutableMap.of(
                1,
                new VillagerTrades.ItemListing[]{
                        new VillagerTrades.ItemsForEmeralds(ModBlocks.VAMPIRE_ORCHID.asItem(), 4, 1, 4, 1),
                        new VillagerTrades.ItemsForEmeralds(ModBlocks.CURSED_ROOTS.asItem(), 2, 1, 4, 1),
                        new VillagerTrades.ItemsForEmeralds(ModBlocks.GARLIC.asItem(), 1, 1, 12, 1),
                        new VillagerTrades.ItemsForEmeralds(ModBlocks.DARK_SPRUCE_SAPLING.asItem(), 8, 1, 6, 1),
                        new VillagerTrades.ItemsForEmeralds(ModBlocks.CURSED_SPRUCE_SAPLING.asItem(), 8, 1, 6, 1),
                        new VillagerTrades.ItemsForEmeralds(ModBlocks.CURSED_EARTH.asItem(), 4, 4, 4, 1),
                }
        ));
    }

    public static void addTrades(ResourceKey<VillagerProfession> profession, Int2ObjectMap<VillagerTrades.ItemListing[]> trades) {
        VillagerTrades.TRADES.computeIfAbsent(profession, existingTrades -> new Int2ObjectOpenHashMap<>()).putAll(trades);
    }

    public static void injectTrades(ResourceKey<VillagerProfession> profession, Int2ObjectMap<VillagerTrades.ItemListing[]> trades) {
        trades.forEach((level, existingTrades) -> VillagerTrades.TRADES.get(profession).put((int) level, Stream.concat(Arrays.stream(VillagerTrades.TRADES.get(profession).get((int) level)), Arrays.stream(trades.get((int) level))).toArray(VillagerTrades.ItemListing[]::new)));
    }

    public static VampirismTrades.ConditionalTrade nonVampire(VillagerTrades.ItemListing trade) {
        return new VampirismTrades.ConditionalTrade(trade, trader -> !Helper.isVampire(trader));
    }

    public static VampirismTrades.ConditionalTrade onlyVampire(VillagerTrades.ItemListing trade) {
        return new VampirismTrades.ConditionalTrade(trade, Helper::isVampire);
    }
}
