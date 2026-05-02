package de.teamlapen.vampirism.common.core;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.tags.ModBiomeTags;
import de.teamlapen.vampirism.common.tags.ModStructureTags;
import de.teamlapen.vampirism.common.tags.ModVillagerTradeTags;
import de.teamlapen.vampirism.common.world.items.component.BottleBlood;
import de.teamlapen.vampirism.common.world.items.loot.BiomeMapFunction;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.TradeSets;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.Optional;

public class ModTrades {

    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_1_HEART_PURE_BLOOD = trade("vampire_expert/1/heart_pure_blood");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_1_ORCHID_EMERALD = trade("vampire_expert/1/orchid_emerald");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_1_EMERALD_MAP = trade("vampire_expert/1/emerald_map");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_2_HEART_PURE_BLOOD = trade("vampire_expert/2/pure_blood_heart");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_2_HEART_INFUSED_IRON = trade("vampire_expert/2/heart_infused_iron");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_2_HEART_CLOAK = trade("vampire_expert/2/heart_cloak");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_2_HEART_COFFIN = trade("vampire_expert/2/heart_coffin");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_3_HEART_PURE_BLOOD = trade("vampire_expert/3/heart_pure_blood");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_3_HEART_INFUSED_IRON = trade("vampire_expert/3/heart_infused_iron");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_3_HEART_HEART_SEEKER = trade("vampire_expert/3/heart_heart_seeker");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_3_HEART_HEART_STRIKER = trade("vampire_expert/3/heart_heart_striker");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_3_HEART_CLOAK = trade("vampire_expert/3/heart_cloak");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_4_HEART_PURE_BLOOD = trade("vampire_expert/4/heart_pure_blood");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_4_EMERALD_CRYPT_MAP = trade("vampire_expert/4/emerald_crypt_map");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_5_HEART_PURE_BLOOD = trade("vampire_expert/5/heart_pure_blood");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_5_HEART_HEART_SEEKER = trade("vampire_expert/5/heart_heart_seeker");
    public static final ResourceKey<VillagerTrade> VAMPIRE_EXPERT_5_HEART_HEART_STRIKER = trade("vampire_expert/5/heart_heart_striker");

    public static final ResourceKey<VillagerTrade> FARMER_1_EMERALD_GARLIC = trade("farmer/1/emerald_garlic");
    public static final ResourceKey<VillagerTrade> BUTCHER_3_EMERALD_HEART = trade("butcher/3/emerald_heart");
    public static final ResourceKey<VillagerTrade> MANSON_2_EMERALD_STONE = trade("manson/2/emerald_stone");
    public static final ResourceKey<VillagerTrade> MANSON_2_EMERALD_CHISELED = trade("manson/2/emerald_chiseled");

    public static final ResourceKey<VillagerTrade> WANDERER_1_EMERALD_ORCHID = trade("wandering_trader/emerald_orchid");
    public static final ResourceKey<VillagerTrade> WANDERER_1_EMERALD_ROOTS = trade("wandering_trader/emerald_roots");
    public static final ResourceKey<VillagerTrade> WANDERER_1_EMERALD_GARLIC = trade("wandering_trader/emerald_garlic");
    public static final ResourceKey<VillagerTrade> WANDERER_1_EMERALD_DARK_SAPLING = trade("wandering_trader/emerald_dark_sapling");
    public static final ResourceKey<VillagerTrade> WANDERER_1_EMERALD_CURSED_SAPLING = trade("wandering_trader/emerald_cursed_sapling");
    public static final ResourceKey<VillagerTrade> WANDERER_1_EMERALD_EARTH = trade("wandering_trader/emerald_earth");

    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_1_EMERALD_GARLIC = trade("hunter_expert/1/emerald_garlic");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_1_EMERALD_MAP = trade("hunter_expert/1/emerald_map");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_2_EMERALD_FANG = trade("hunter_expert/2/emerald_fang");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_2_SOULS_ARROW_NORMAL = trade("hunter_expert/2/souls_arrow_normal");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_2_SOULS_ARROW_VAMPIRE_KILLER = trade("hunter_expert/2/souls_arrow_vampire_killer");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_2_SOULS_ARROW_SPITFIRE = trade("hunter_expert/2/souls_arrow_spitfire");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_2_SOULS_ARROW_TELEPORT = trade("hunter_expert/2/souls_arrow_teleport");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_2_SOUL_SWIFTNESS_HEAD = trade("hunter_expert/2/soul_swiftness_head");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_2_SOUL_SWIFTNESS_CHEST = trade("hunter_expert/2/soul_swiftness_chest");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_2_SOUL_SWIFTNESS_LEGS = trade("hunter_expert/2/soul_swiftness_legs");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_2_SOUL_SWIFTNESS_FEET = trade("hunter_expert/2/soul_swiftness_feet");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_3_EMERALD_BLOOD = trade("hunter_expert/3/emerald_blood");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_3_SOUL_COAT_HEAD = trade("hunter_expert/3/soul_coat_head");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_3_SOUL_COAT_CHEST = trade("hunter_expert/3/soul_coat_chest");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_3_SOUL_COAT_LEGS = trade("hunter_expert/3/soul_coat_legs");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_3_SOUL_COAT_FEET = trade("hunter_expert/3/soul_coat_feet");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_3_SOUL_SWIFTNESS_HEAD = trade("hunter_expert/3/soul_swiftness_head");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_3_SOUL_SWIFTNESS_CHEST = trade("hunter_expert/3/soul_swiftness_chest");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_3_SOUL_SWIFTNESS_LEGS = trade("hunter_expert/3/soul_swiftness_legs");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_3_SOUL_SWIFTNESS_FEET = trade("hunter_expert/3/soul_swiftness_feet");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_4_SOUL_COAT_HEAD = trade("hunter_expert/4/soul_coat_head");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_4_SOUL_COAT_CHEST = trade("hunter_expert/4/soul_coat_chest");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_4_SOUL_COAT_LEGS = trade("hunter_expert/4/soul_coat_legs");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_4_SOUL_COAT_FEET = trade("hunter_expert/4/soul_coat_feet");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_4_SOUL_SWIFTNESS_HEAD = trade("hunter_expert/4/soul_swiftness_head");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_4_SOUL_SWIFTNESS_CHEST = trade("hunter_expert/4/soul_swiftness_chest");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_4_SOUL_SWIFTNESS_LEGS = trade("hunter_expert/4/soul_swiftness_legs");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_4_SOUL_SWIFTNESS_FEET = trade("hunter_expert/4/soul_swiftness_feet");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_5_SOUL_COAT_HEAD = trade("hunter_expert/5/soul_coat_head");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_5_SOUL_COAT_CHEST = trade("hunter_expert/5/soul_coat_chest");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_5_SOUL_COAT_LEGS = trade("hunter_expert/5/soul_coat_legs");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_5_SOUL_COAT_FEET = trade("hunter_expert/5/soul_coat_feet");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_5_SOUL_AXE = trade("hunter_expert/5/soul_axe");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_5_SOUL_CROSSBOW = trade("hunter_expert/5/soul_crossbow");
    public static final ResourceKey<VillagerTrade> HUNTER_EXPERT_5_SOUL_CROSSBOW_DOUBLE = trade("hunter_expert/5/soul_crossbow_double");

    public static final ResourceKey<VillagerTrade> PRIEST_1_EMERALD_SALT = trade("priest/1/emerald_salt");
    public static final ResourceKey<VillagerTrade> PRIEST_1_EMERALD_GARLIC = trade("priest/1/emerald_garlic");
    public static final ResourceKey<VillagerTrade> PRIEST_1_CANDLE_EMERALD = trade("priest/1/candle_emerald");
    public static final ResourceKey<VillagerTrade> PRIEST_2_EMERALD_HONEY = trade("priest/2/emerald_honey");
    public static final ResourceKey<VillagerTrade> PRIEST_2_HOLY_EMERALD = trade("priest/2/holy_emerald");
    public static final ResourceKey<VillagerTrade> PRIEST_3_EMERALD_GOLD = trade("priest/3/emerald_gold");
    public static final ResourceKey<VillagerTrade> PRIEST_3_HOLY_EMERALD = trade("priest/3/holy_emerald");
    public static final ResourceKey<VillagerTrade> PRIEST_3_EMERALD_CRUCIFIX = trade("priest/3/emerald_crucifix");
    public static final ResourceKey<VillagerTrade> PRIEST_4_HOLY_EMERALD = trade("priest/4/holy_emerald");
    public static final ResourceKey<VillagerTrade> PRIEST_4_EMERALD_CRUCIFIX = trade("priest/4/emerald_crucifix");
    public static final ResourceKey<VillagerTrade> PRIEST_5_CANDELABRA_EMERALD = trade("priest/5/candelabra_emerald");
    public static final ResourceKey<VillagerTrade> PRIEST_5_EMERALD_CRUCIFIX = trade("priest/5/emerald_crucifix");

    public static final ResourceKey<VillagerTrade> VAMPIRE_VILLAGER_HEARTS_EMERALDS = trade("vampire_villager/heart_emeralds");
    public static final ResourceKey<VillagerTrade> VAMPIRE_VILLAGER_EMERALDS_HEARTS = trade("vampire_villager/emeralds_hearts");
    public static final ResourceKey<VillagerTrade> VAMPIRE_VILLAGER_BLOOD_BOTTLES = trade("vampire_villager/blood_bottles");

    public static final ResourceKey<TradeSet> VAMPIRE_EXPERT_LEVEL_1 = tradeSet("vampire_expert/level_1");
    public static final ResourceKey<TradeSet> VAMPIRE_EXPERT_LEVEL_2 = tradeSet("vampire_expert/level_2");
    public static final ResourceKey<TradeSet> VAMPIRE_EXPERT_LEVEL_3 = tradeSet("vampire_expert/level_3");
    public static final ResourceKey<TradeSet> VAMPIRE_EXPERT_LEVEL_4 = tradeSet("vampire_expert/level_4");
    public static final ResourceKey<TradeSet> VAMPIRE_EXPERT_LEVEL_5 = tradeSet("vampire_expert/level_5");

    public static final ResourceKey<TradeSet> HUNTER_EXPERT_LEVEL_1 = tradeSet("hunter_expert/level_1");
    public static final ResourceKey<TradeSet> HUNTER_EXPERT_LEVEL_2 = tradeSet("hunter_expert/level_2");
    public static final ResourceKey<TradeSet> HUNTER_EXPERT_LEVEL_3 = tradeSet("hunter_expert/level_3");
    public static final ResourceKey<TradeSet> HUNTER_EXPERT_LEVEL_4 = tradeSet("hunter_expert/level_4");
    public static final ResourceKey<TradeSet> HUNTER_EXPERT_LEVEL_5 = tradeSet("hunter_expert/level_5");

    public static final ResourceKey<TradeSet> PRIEST_LEVEL_1 = tradeSet("priest/level_1");
    public static final ResourceKey<TradeSet> PRIEST_LEVEL_2 = tradeSet("priest/level_2");
    public static final ResourceKey<TradeSet> PRIEST_LEVEL_3 = tradeSet("priest/level_3");
    public static final ResourceKey<TradeSet> PRIEST_LEVEL_4 = tradeSet("priest/level_4");
    public static final ResourceKey<TradeSet> PRIEST_LEVEL_5 = tradeSet("priest/level_5");

    public static final ResourceKey<TradeSet> VAMPIRE_VILLAGER = tradeSet("vampire_villager");


    public static ResourceKey<VillagerTrade> trade(String path) {
        return ResourceKey.create(Registries.VILLAGER_TRADE, VIdentifier.mod(path));
    }
    public static ResourceKey<TradeSet> tradeSet(String path) {
        return ResourceKey.create(Registries.TRADE_SET, VIdentifier.mod(path));
    }

    static void bootstrapTradeSets(BootstrapContext<TradeSet> context) {
        TradeSets.register(context, VAMPIRE_EXPERT_LEVEL_1, ModVillagerTradeTags.VAMPIRE_EXPERT_LEVEL_1);
        TradeSets.register(context, VAMPIRE_EXPERT_LEVEL_2, ModVillagerTradeTags.VAMPIRE_EXPERT_LEVEL_2);
        TradeSets.register(context, VAMPIRE_EXPERT_LEVEL_3, ModVillagerTradeTags.VAMPIRE_EXPERT_LEVEL_3);
        TradeSets.register(context, VAMPIRE_EXPERT_LEVEL_4, ModVillagerTradeTags.VAMPIRE_EXPERT_LEVEL_4);
        TradeSets.register(context, VAMPIRE_EXPERT_LEVEL_5, ModVillagerTradeTags.VAMPIRE_EXPERT_LEVEL_5);
        TradeSets.register(context, HUNTER_EXPERT_LEVEL_1, ModVillagerTradeTags.HUNTER_EXPERT_LEVEL_1);
        TradeSets.register(context, HUNTER_EXPERT_LEVEL_2, ModVillagerTradeTags.HUNTER_EXPERT_LEVEL_2);
        TradeSets.register(context, HUNTER_EXPERT_LEVEL_3, ModVillagerTradeTags.HUNTER_EXPERT_LEVEL_3);
        TradeSets.register(context, HUNTER_EXPERT_LEVEL_4, ModVillagerTradeTags.HUNTER_EXPERT_LEVEL_4);
        TradeSets.register(context, HUNTER_EXPERT_LEVEL_5, ModVillagerTradeTags.HUNTER_EXPERT_LEVEL_5);
        TradeSets.register(context, PRIEST_LEVEL_1, ModVillagerTradeTags.PRIEST_LEVEL_1);
        TradeSets.register(context, PRIEST_LEVEL_2, ModVillagerTradeTags.PRIEST_LEVEL_2);
        TradeSets.register(context, PRIEST_LEVEL_3, ModVillagerTradeTags.PRIEST_LEVEL_3);
        TradeSets.register(context, PRIEST_LEVEL_4, ModVillagerTradeTags.PRIEST_LEVEL_4);
        TradeSets.register(context, PRIEST_LEVEL_5, ModVillagerTradeTags.PRIEST_LEVEL_5);
        TradeSets.register(context, VAMPIRE_VILLAGER, ModVillagerTradeTags.VAMPIRE_VILLAGER, ConstantValue.exactly(1));
    }

    static void bootstrap(BootstrapContext<VillagerTrade> context) {
        bootstrapVampireExpert(context);
        bootstrapHunterExpert(context);
        bootstrapPriest(context);

        register(context, FARMER_1_EMERALD_GARLIC, trade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(ModBlocks.GARLIC.asItem(), 22),16, 10));
        register(context, BUTCHER_3_EMERALD_HEART, trade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(ModItems.HUMAN_HEART.get(), 6),16, 20));
        register(context, MANSON_2_EMERALD_STONE, trade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(ModBlocks.DARK_STONE.asItem(), 16),16, 10));
        register(context, MANSON_2_EMERALD_CHISELED, trade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(ModBlocks.CHISELED_DARK_STONE_BRICKS.asItem(), 4),16, 10));

        register(context, WANDERER_1_EMERALD_ORCHID, trade(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(ModBlocks.VAMPIRE_ORCHID.asItem(), 1), 4, 5));
        register(context, WANDERER_1_EMERALD_ROOTS, trade(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(ModBlocks.CURSED_ROOTS.asItem(), 1), 4, 5));
        register(context, WANDERER_1_EMERALD_GARLIC, trade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(ModBlocks.GARLIC.asItem(), 1), 12, 5));
        register(context, WANDERER_1_EMERALD_DARK_SAPLING, trade(new TradeCost(Items.EMERALD, 8), new ItemStackTemplate(ModBlocks.DARK_SPRUCE_SAPLING.asItem(), 1), 6, 5));
        register(context, WANDERER_1_EMERALD_CURSED_SAPLING, trade(new TradeCost(Items.EMERALD, 8), new ItemStackTemplate(ModBlocks.CURSED_SPRUCE_SAPLING.asItem(), 1), 6, 5));
        register(context, WANDERER_1_EMERALD_EARTH, trade(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(ModBlocks.CURSED_EARTH.asItem(), 4), 4, 5));
        register(context, VAMPIRE_VILLAGER_HEARTS_EMERALDS, trade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(ModItems.HUMAN_HEART.get(), 8), 12, 5));
        register(context, VAMPIRE_VILLAGER_EMERALDS_HEARTS, trade(new TradeCost(ModItems.HUMAN_HEART, 1), new ItemStackTemplate(Items.EMERALD, 3), 12, 5));
        register(context, VAMPIRE_VILLAGER_BLOOD_BOTTLES, trade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(ModItems.BLOOD_BOTTLE.get(), 1, DataComponentPatch.builder().set(ModDataComponents.BOTTLE_BLOOD.get(), new BottleBlood(5)).build()), 12, 5));
    }

    static void bootstrapPriest(BootstrapContext<VillagerTrade> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        register(context, PRIEST_1_EMERALD_SALT, trade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(ModItems.PURE_SALT.get(), 25),12, 2));
        register(context, PRIEST_1_EMERALD_GARLIC, trade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(ModBlocks.GARLIC.asItem(), 22),16, 2));
        register(context, PRIEST_1_CANDLE_EMERALD, trade(new TradeCost(Items.CANDLE, 1), new ItemStackTemplate(Items.EMERALD, 4),12, 1));
        register(context, PRIEST_2_HOLY_EMERALD, trade(new TradeCost(ModItems.HOLY_WATER_BOTTLE_NORMAL, 1), new ItemStackTemplate(Items.EMERALD, 2),5, 5));
        register(context, PRIEST_2_EMERALD_HONEY, trade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.HONEYCOMB, 12),12, 10));
        register(context, PRIEST_3_HOLY_EMERALD, trade(new TradeCost(ModItems.HOLY_WATER_BOTTLE_ENHANCED, 1), new ItemStackTemplate(Items.EMERALD, 3),5, 5));
        register(context, PRIEST_3_EMERALD_GOLD, trade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.GOLD_INGOT, 3),12, 20));
        register(context, PRIEST_3_EMERALD_CRUCIFIX, trade(new TradeCost(Items.EMERALD, UniformGenerator.between(2, 4)), new ItemStackTemplate(ModItems.CRUCIFIX_NORMAL, 1),8, 10));
        register(context, PRIEST_4_HOLY_EMERALD, trade(new TradeCost(ModItems.HOLY_WATER_BOTTLE_ULTIMATE, 1), new ItemStackTemplate(Items.EMERALD, 4),5, 15));
        register(context, PRIEST_5_CANDELABRA_EMERALD, trade(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(ModItems.CANDELABRA, 1),12, 30));
        register(context, PRIEST_4_EMERALD_CRUCIFIX, trade(new TradeCost(Items.EMERALD, UniformGenerator.between(6, 16)), new ItemStackTemplate(ModItems.CRUCIFIX_NORMAL, 1),4, 20));
        register(context, PRIEST_5_EMERALD_CRUCIFIX, trade(new TradeCost(Items.EMERALD, UniformGenerator.between(24, 32)), new ItemStackTemplate(ModItems.CRUCIFIX_NORMAL, 1),4, 30));


    }

    static void bootstrapHunterExpert(BootstrapContext<VillagerTrade> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        register(context, HUNTER_EXPERT_1_EMERALD_GARLIC, trade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(ModBlocks.GARLIC.asItem(), 22), 16,2));
        register(context, HUNTER_EXPERT_1_EMERALD_MAP, new VillagerTrade(new TradeCost(Items.EMERALD, 7),Optional.of(new TradeCost(Items.COMPASS, 1)), new ItemStackTemplate(Items.MAP), 12,2, 0.05f, Optional.empty(), List.of(BiomeMapFunction.makeBiomeMap()
                .setDestination(ModBiomeTags.HasFaction.IS_VAMPIRE_BIOME)
                .setMapDecoration(ModMapDecorations.CRYPT)
                .setSearchRadius(100)
                .setSkipKnownStructures(true)
                .build(), SetNameFunction.setName(Component.translatable("filled_map.vampirism.vampire_forest"), SetNameFunction.Target.ITEM_NAME).build(),
                FilteredFunction.filtered(new ItemPredicate.Builder()
                        .of(items, Items.FILLED_MAP)
                        .withComponents(DataComponentMatchers.Builder.components().any(DataComponents.MAP_ID).build()).build()).onFail(Optional.of(DiscardItem.discardItem().build())).build())));

        register(context, HUNTER_EXPERT_2_EMERALD_FANG, trade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(ModItems.VAMPIRE_FANG.get(), 9),16, 10));
        register(context, HUNTER_EXPERT_2_SOULS_ARROW_NORMAL, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(10,20)), new ItemStackTemplate(ModItems.CROSSBOW_ARROW_NORMAL.get()),12, 5, Optional.empty(), List.of(SetItemCountFunction.setCount(UniformGenerator.between(8,16)).build())));
        register(context, HUNTER_EXPERT_2_SOULS_ARROW_VAMPIRE_KILLER, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(15,30)), new ItemStackTemplate(ModItems.CROSSBOW_ARROW_NORMAL.get()),12, 5, Optional.empty(), List.of(SetItemCountFunction.setCount(UniformGenerator.between(8,16)).build())));
        register(context, HUNTER_EXPERT_2_SOULS_ARROW_SPITFIRE, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(20,35)), new ItemStackTemplate(ModItems.CROSSBOW_ARROW_NORMAL.get()),12, 5, Optional.empty(), List.of(SetItemCountFunction.setCount(UniformGenerator.between(8,16)).build())));
        register(context, HUNTER_EXPERT_2_SOULS_ARROW_TELEPORT, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(25,40)), new ItemStackTemplate(ModItems.CROSSBOW_ARROW_NORMAL.get()),12, 5, Optional.empty(), List.of(SetItemCountFunction.setCount(UniformGenerator.between(6,12)).build())));

        register(context, HUNTER_EXPERT_2_SOUL_SWIFTNESS_HEAD, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(20,30)), new ItemStackTemplate(ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get()),7, 10));
        register(context, HUNTER_EXPERT_2_SOUL_SWIFTNESS_CHEST, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(30,40)), new ItemStackTemplate(ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get()),7, 10));
        register(context, HUNTER_EXPERT_2_SOUL_SWIFTNESS_LEGS, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(25,35)), new ItemStackTemplate(ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get()),7, 10));
        register(context, HUNTER_EXPERT_2_SOUL_SWIFTNESS_FEET, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(10,15)), new ItemStackTemplate(ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get()),7, 10));

        register(context, HUNTER_EXPERT_3_EMERALD_BLOOD, trade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(ModItems.BLOOD_BOTTLE.get(), 5, DataComponentPatch.builder().set(ModDataComponents.BOTTLE_BLOOD.get(), new BottleBlood(5)).build()),16, 20));

        register(context, HUNTER_EXPERT_3_SOUL_SWIFTNESS_HEAD, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(20,30)), new ItemStackTemplate(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get()),7, 10));
        register(context, HUNTER_EXPERT_3_SOUL_SWIFTNESS_CHEST, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(30,40)), new ItemStackTemplate(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get()),7, 10));
        register(context, HUNTER_EXPERT_3_SOUL_SWIFTNESS_LEGS, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(25,35)), new ItemStackTemplate(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get()),7, 10));
        register(context, HUNTER_EXPERT_3_SOUL_SWIFTNESS_FEET, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(10,15)), new ItemStackTemplate(ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get()),7, 10));

        register(context, HUNTER_EXPERT_3_SOUL_COAT_HEAD, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(20,30)), new ItemStackTemplate(ModItems.HUNTER_COAT_HEAD_NORMAL.get()),7, 10));
        register(context, HUNTER_EXPERT_3_SOUL_COAT_CHEST, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(30,40)), new ItemStackTemplate(ModItems.HUNTER_COAT_CHEST_NORMAL.get()),7, 10));
        register(context, HUNTER_EXPERT_3_SOUL_COAT_LEGS, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(25,35)), new ItemStackTemplate(ModItems.HUNTER_COAT_LEGS_NORMAL.get()),7, 10));
        register(context, HUNTER_EXPERT_3_SOUL_COAT_FEET, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(10,15)), new ItemStackTemplate(ModItems.HUNTER_COAT_FEET_NORMAL.get()),7, 10));

        register(context, HUNTER_EXPERT_4_SOUL_SWIFTNESS_HEAD, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(20,30)), new ItemStackTemplate(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get()),7, 10));
        register(context, HUNTER_EXPERT_4_SOUL_SWIFTNESS_CHEST, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(30,40)), new ItemStackTemplate(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get()),7, 10));
        register(context, HUNTER_EXPERT_4_SOUL_SWIFTNESS_LEGS, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(25,35)), new ItemStackTemplate(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get()),7, 10));
        register(context, HUNTER_EXPERT_4_SOUL_SWIFTNESS_FEET, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(10,15)), new ItemStackTemplate(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()),7, 10));

        register(context, HUNTER_EXPERT_4_SOUL_COAT_HEAD, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(20,30)), new ItemStackTemplate(ModItems.HUNTER_COAT_HEAD_ENHANCED.get()),7, 10));
        register(context, HUNTER_EXPERT_4_SOUL_COAT_CHEST, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(30,40)), new ItemStackTemplate(ModItems.HUNTER_COAT_CHEST_ENHANCED.get()),7, 10));
        register(context, HUNTER_EXPERT_4_SOUL_COAT_LEGS, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(25,35)), new ItemStackTemplate(ModItems.HUNTER_COAT_LEGS_ENHANCED.get()),7, 10));
        register(context, HUNTER_EXPERT_4_SOUL_COAT_FEET, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(10,15)), new ItemStackTemplate(ModItems.HUNTER_COAT_FEET_ENHANCED.get()),7, 10));

        register(context, HUNTER_EXPERT_5_SOUL_COAT_HEAD, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(20,30)), new ItemStackTemplate(ModItems.HUNTER_COAT_HEAD_ULTIMATE.get()),7, 10));
        register(context, HUNTER_EXPERT_5_SOUL_COAT_CHEST, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(30,40)), new ItemStackTemplate(ModItems.HUNTER_COAT_CHEST_ULTIMATE.get()),7, 10));
        register(context, HUNTER_EXPERT_5_SOUL_COAT_LEGS, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(25,35)), new ItemStackTemplate(ModItems.HUNTER_COAT_LEGS_ULTIMATE.get()),7, 10));
        register(context, HUNTER_EXPERT_5_SOUL_COAT_FEET, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(10,15)), new ItemStackTemplate(ModItems.HUNTER_COAT_FEET_ULTIMATE.get()),7, 10));

        register(context, HUNTER_EXPERT_5_SOUL_AXE, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(80,96)), new ItemStackTemplate(ModItems.HUNTER_AXE_ULTIMATE.get()),3, 30));
        register(context, HUNTER_EXPERT_5_SOUL_CROSSBOW, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(25,50)), new ItemStackTemplate(ModItems.ENHANCED_CROSSBOW.get()),3, 30));
        register(context, HUNTER_EXPERT_5_SOUL_CROSSBOW_DOUBLE, trade(new TradeCost(ModItems.SOUL_ORB_VAMPIRE, UniformGenerator.between(32,64)), new ItemStackTemplate(ModItems.ENHANCED_DOUBLE_CROSSBOW.get()),3, 30));



    }


    static void bootstrapVampireExpert(BootstrapContext<VillagerTrade> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        register(context, VAMPIRE_EXPERT_1_HEART_PURE_BLOOD, trade(new TradeCost(ModItems.HUMAN_HEART.get(), UniformGenerator.between(10,15)), new ItemStackTemplate(ModItems.PURE_BLOOD_0), 2,2));
        register(context, VAMPIRE_EXPERT_1_ORCHID_EMERALD, trade(new TradeCost(ModBlocks.VAMPIRE_ORCHID.get(), 9), new ItemStackTemplate(Items.EMERALD), 8,2));
        register(context, VAMPIRE_EXPERT_1_EMERALD_MAP, new VillagerTrade(new TradeCost(Items.EMERALD, 7), Optional.of(new TradeCost(Items.COMPASS, 1)), new ItemStackTemplate(Items.MAP), 8,2,0.05f,Optional.empty(), List.of(BiomeMapFunction.makeBiomeMap()
                        .setDestination(ModBiomeTags.HasFaction.IS_VAMPIRE_BIOME)
                        .setMapDecoration(ModMapDecorations.CRYPT)
                        .setSearchRadius(100)
                        .setSkipKnownStructures(true)
                        .build(), SetNameFunction.setName(Component.translatable("filled_map.vampirism.vampire_forest"), SetNameFunction.Target.ITEM_NAME).build(),
                FilteredFunction.filtered(new ItemPredicate.Builder()
                        .of(items, Items.FILLED_MAP)
                        .withComponents(DataComponentMatchers.Builder.components().any(DataComponents.MAP_ID).build()).build()).onFail(Optional.of(DiscardItem.discardItem().build())).build())));
        register(context, VAMPIRE_EXPERT_2_HEART_PURE_BLOOD, trade(new TradeCost(ModItems.HUMAN_HEART.get(), UniformGenerator.between(25,30)), new ItemStackTemplate(ModItems.PURE_BLOOD_1), 2,5));
        register(context, VAMPIRE_EXPERT_2_HEART_INFUSED_IRON, trade(new TradeCost(ModItems.HUMAN_HEART.get(), UniformGenerator.between(25,30)), new ItemStackTemplate(ModItems.BLOOD_INFUSED_IRON_INGOT), 8,5, Optional.empty(), List.of(SetItemCountFunction.setCount(UniformGenerator.between(1,3)).build())));
        register(context, VAMPIRE_EXPERT_2_HEART_CLOAK, trade(new TradeCost(ModItems.HUMAN_HEART.get(), UniformGenerator.between(5,20)), new ItemStackTemplate(ModItems.VAMPIRE_CLOAK_BLACK), 4,5)); // TODO use ColorListsUtil.VAMPIRE_CLOAKS.values()
        register(context, VAMPIRE_EXPERT_2_HEART_COFFIN, trade(new TradeCost(ModItems.HUMAN_HEART.get(), UniformGenerator.between(3,15)), new ItemStackTemplate(ModBlocks.COFFIN_BLACK.asItem()), 4,5)); // TODO use ColorListsUtil.COFFINS.values()
        register(context, VAMPIRE_EXPERT_3_HEART_PURE_BLOOD, trade(new TradeCost(ModItems.HUMAN_HEART.get(), UniformGenerator.between(30,40)), new ItemStackTemplate(ModItems.PURE_BLOOD_2), 2,10));
        register(context, VAMPIRE_EXPERT_3_HEART_INFUSED_IRON, trade(new TradeCost(ModItems.HUMAN_HEART.get(), UniformGenerator.between(15,30)), new ItemStackTemplate(ModItems.BLOOD_INFUSED_IRON_INGOT), 12,10, Optional.empty(), List.of(SetItemCountFunction.setCount(UniformGenerator.between(1,2)).build())));
        register(context, VAMPIRE_EXPERT_3_HEART_HEART_SEEKER, trade(new TradeCost(ModItems.HUMAN_HEART.get(), UniformGenerator.between(42,64)), new ItemStackTemplate(ModItems.HEART_SEEKER_ENHANCED), 3,10));
        register(context, VAMPIRE_EXPERT_3_HEART_HEART_STRIKER, trade(new TradeCost(ModItems.HUMAN_HEART.get(), UniformGenerator.between(42,64)), new ItemStackTemplate(ModItems.HEART_STRIKER_ENHANCED), 3,10));
        register(context, VAMPIRE_EXPERT_3_HEART_CLOAK, trade(new TradeCost(ModItems.HUMAN_HEART.get(), UniformGenerator.between(10,30)), new ItemStackTemplate(ModItems.VAMPIRE_CLOAK_BLACK), 4,10)); // TODO use ColorListsUtil.VAMPIRE_CLOAKS.values()
        register(context, VAMPIRE_EXPERT_4_HEART_PURE_BLOOD, trade(new TradeCost(ModItems.HUMAN_HEART.get(), UniformGenerator.between(20,30)), new ItemStackTemplate(ModItems.PURE_BLOOD_3), 2,15));
        register(context, VAMPIRE_EXPERT_5_HEART_PURE_BLOOD, trade(new TradeCost(ModItems.HUMAN_HEART.get(), UniformGenerator.between(30,40)), new ItemStackTemplate(ModItems.PURE_BLOOD_4), 2,30));
        register(context, VAMPIRE_EXPERT_5_HEART_HEART_SEEKER, trade(new TradeCost(ModItems.HUMAN_HEART.get(), UniformGenerator.between(80,96)), new ItemStackTemplate(ModItems.HEART_SEEKER_ULTIMATE), 3,30));
        register(context, VAMPIRE_EXPERT_5_HEART_HEART_STRIKER, trade(new TradeCost(ModItems.HUMAN_HEART.get(), UniformGenerator.between(80,96)), new ItemStackTemplate(ModItems.HEART_STRIKER_ULTIMATE), 3,30));
        register(context, VAMPIRE_EXPERT_4_EMERALD_CRYPT_MAP, new VillagerTrade(new TradeCost(Items.EMERALD, 14),Optional.of(new TradeCost(Items.COMPASS, 1)), new ItemStackTemplate(Items.MAP), 12, 15,0.05f,Optional.empty(), List.of(ExplorationMapFunction.makeExplorationMap()
                        .setDestination(ModStructureTags.ON_CRYPT_MAPS)
                        .setMapDecoration(ModMapDecorations.CRYPT)
                        .setSearchRadius(100)
                        .setSkipKnownStructures(true)
                        .build(), SetNameFunction.setName(Component.translatable("filled_map.vampirism.crypt"), SetNameFunction.Target.ITEM_NAME).build(),
                FilteredFunction.filtered(new ItemPredicate.Builder()
                        .of(items, Items.FILLED_MAP)
                        .withComponents(DataComponentMatchers.Builder.components().any(DataComponents.MAP_ID).build()).build()).onFail(Optional.of(DiscardItem.discardItem().build())).build())));
    }

    public static Holder.Reference<VillagerTrade> register(
            BootstrapContext<VillagerTrade> context, ResourceKey<VillagerTrade> resourceKey, VillagerTrade villagerTrade
    ) {
        return context.register(resourceKey, villagerTrade);
    }

    private static VillagerTrade trade(TradeCost wants,
                                       ItemStackTemplate gives,
                                       int maxUses,
                                       int xp,
                                       Optional<LootItemCondition> merchantPredicate,
                                       List<LootItemFunction> givenItemModifiers) {
        return new VillagerTrade(wants, gives, maxUses, xp, 0.05f, Optional.empty(), List.of());
    }

    private static VillagerTrade trade(TradeCost wants,
                                       ItemStackTemplate gives,
                                       int maxUses,
                                       int xp) {
        return new VillagerTrade(wants, gives, maxUses, xp, 0.05f, Optional.empty(), List.of());
    }


}
