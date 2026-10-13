package de.teamlapen.faction.data.provider.loot;

import de.teamlapen.faction.common.core.FactionLootTables;
import de.teamlapen.faction.common.tags.FactionItemTags;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Loot tables for {@link de.teamlapen.faction.common.factions.minions.management.CollectResourcesTask}.
 * <p>
 * Each specialized table has a base pool that is always rolled and better pools that require the minion to hold a matching tool of a certain tier.
 */
public class ModMinionTaskLootTableProvider implements LootTableSubProvider {

    private final HolderLookup.Provider registries;
    private final HolderGetter<Item> items;

    public ModMinionTaskLootTableProvider(HolderLookup.Provider registries) {
        this.registries = registries;
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        // general - dummy items
        output.accept(FactionLootTables.MINION_COLLECT_RESOURCES, LootTable.lootTable()
                .withPool(pool(1)
                        .add(item(Items.STICK, 2, 6, 20))
                        .add(item(Items.COBBLESTONE, 4, 12, 20))
                        .add(item(Items.WHEAT_SEEDS, 2, 6, 15))
                        .add(item(Items.COAL, 1, 4, 15))
                        .add(item(Items.IRON_NUGGET, 4, 12, 15))
                        .add(item(Items.GOLD_NUGGET, 2, 6, 10))
                        .add(item(Items.STRING, 1, 3, 5))));

        // wood - axe
        output.accept(FactionLootTables.MINION_COLLECT_WOOD, LootTable.lootTable()
                .withPool(pool(1)
                        .add(item(Items.STICK, 4, 10, 30))
                        .add(item(Items.OAK_SAPLING, 1, 3, 15))
                        .add(item(Items.SPRUCE_SAPLING, 1, 3, 10))
                        .add(item(Items.BIRCH_SAPLING, 1, 3, 10))
                        .add(item(Items.APPLE, 1, 2, 5)))
                .withPool(pool(1).when(tool(FactionItemTags.MINION_AXE.base()))
                        .add(item(Items.OAK_LOG, 4, 8, 30))
                        .add(item(Items.SPRUCE_LOG, 4, 8, 20))
                        .add(item(Items.BIRCH_LOG, 4, 8, 20))
                        .add(item(Items.JUNGLE_LOG, 2, 6, 10))
                        .add(item(Items.ACACIA_LOG, 2, 6, 10))
                        .add(item(Items.DARK_OAK_LOG, 2, 6, 10)))
                .withPool(pool(1).when(tool(FactionItemTags.MINION_AXE.tier1()))
                        .add(item(Items.OAK_LOG, 8, 16, 20))
                        .add(item(Items.CHERRY_LOG, 4, 8, 10))
                        .add(item(Items.MANGROVE_LOG, 4, 8, 10))
                        .add(item(Items.PALE_OAK_LOG, 4, 8, 10)))
                .withPool(pool(1).when(tool(FactionItemTags.MINION_AXE.tier2()))
                        .add(item(Items.CRIMSON_STEM, 4, 10, 10))
                        .add(item(Items.WARPED_STEM, 4, 10, 10))
                        .add(item(Items.BAMBOO_BLOCK, 4, 8, 5))));

        // mining - pickaxe
        output.accept(FactionLootTables.MINION_COLLECT_ORES, LootTable.lootTable()
                .withPool(pool(1)
                        .add(item(Items.COBBLESTONE, 8, 16, 30))
                        .add(item(Items.FLINT, 1, 4, 15))
                        .add(item(Items.IRON_NUGGET, 4, 12, 15))
                        .add(item(Items.GOLD_NUGGET, 2, 8, 10))
                        .add(item(Items.COAL, 1, 3, 15)))
                .withPool(pool(1).when(tool(FactionItemTags.MINION_PICKAXE.base()))
                        .add(item(Items.COAL, 3, 8, 30))
                        .add(item(Items.RAW_COPPER, 2, 6, 25))
                        .add(item(Items.RAW_IRON, 1, 4, 20))
                        .add(item(Items.QUARTZ, 2, 6, 10)))
                .withPool(pool(1).when(tool(FactionItemTags.MINION_PICKAXE.tier1()))
                        .add(item(Items.RAW_IRON, 2, 6, 30))
                        .add(item(Items.RAW_GOLD, 1, 4, 20))
                        .add(item(Items.REDSTONE, 4, 10, 20))
                        .add(item(Items.LAPIS_LAZULI, 4, 10, 15))
                        .add(item(Items.AMETHYST_SHARD, 2, 6, 10)))
                .withPool(pool(1).when(tool(FactionItemTags.MINION_PICKAXE.tier2()))
                        .add(item(Items.DIAMOND, 1, 2, 30))
                        .add(item(Items.EMERALD, 1, 3, 30))
                        .add(item(Items.OBSIDIAN, 2, 4, 20))
                        .add(item(Items.NETHERITE_SCRAP, 1, 1, 5))));

        // fishing - fishing rod, better loot with luck of the sea
        output.accept(FactionLootTables.MINION_COLLECT_FISH, LootTable.lootTable()
                .withPool(pool(1)
                        .add(item(Items.KELP, 2, 6, 25))
                        .add(item(Items.SEAGRASS, 2, 6, 15))
                        .add(item(Items.STICK, 1, 3, 10))
                        .add(item(Items.BONE, 1, 3, 10)))
                .withPool(pool(1).when(tool(FactionItemTags.MINION_FISHING_ROD))
                        .add(item(Items.COD, 2, 6, 40))
                        .add(item(Items.SALMON, 1, 4, 25))
                        .add(item(Items.TROPICAL_FISH, 1, 2, 10))
                        .add(item(Items.PUFFERFISH, 1, 2, 10))
                        .add(item(Items.INK_SAC, 1, 3, 10)))
                .withPool(pool(1).when(fishingRod(1))
                        .add(item(Items.SALMON, 3, 6, 30))
                        .add(item(Items.PRISMARINE_SHARD, 2, 6, 20))
                        .add(item(Items.PRISMARINE_CRYSTALS, 2, 6, 20))
                        .add(item(Items.NAUTILUS_SHELL, 1, 1, 10))
                        .add(item(Items.NAME_TAG, 1, 1, 5)))
                .withPool(pool(1).when(fishingRod(3))
                        .add(item(Items.NAUTILUS_SHELL, 1, 2, 20))
                        .add(item(Items.SADDLE, 1, 1, 10))
                        .add(item(Items.TURTLE_SCUTE, 1, 2, 10))
                        .add(item(Items.HEART_OF_THE_SEA, 1, 1, 2))));

        // plants - shovel
        output.accept(FactionLootTables.MINION_COLLECT_PLANTS, LootTable.lootTable()
                .withPool(pool(1)
                        .add(item(Items.WHEAT_SEEDS, 2, 8, 30))
                        .add(item(Items.SHORT_GRASS, 2, 6, 20))
                        .add(item(Items.DANDELION, 1, 3, 10))
                        .add(item(Items.POPPY, 1, 3, 10)))
                .withPool(pool(1).when(tool(FactionItemTags.MINION_SHOVEL.base()))
                        .add(item(Items.WHEAT, 2, 6, 25))
                        .add(item(Items.CARROT, 2, 6, 20))
                        .add(item(Items.POTATO, 2, 6, 20))
                        .add(item(Items.BEETROOT, 2, 6, 15))
                        .add(item(Items.SUGAR_CANE, 2, 6, 15)))
                .withPool(pool(1).when(tool(FactionItemTags.MINION_SHOVEL.tier1()))
                        .add(item(Items.PUMPKIN, 1, 3, 20))
                        .add(item(Items.MELON_SLICE, 4, 10, 20))
                        .add(item(Items.COCOA_BEANS, 2, 6, 15))
                        .add(item(Items.SWEET_BERRIES, 2, 6, 15))
                        .add(item(Items.CACTUS, 1, 4, 10)))
                .withPool(pool(1).when(tool(FactionItemTags.MINION_SHOVEL.tier2()))
                        .add(item(Items.GLOW_BERRIES, 2, 6, 20))
                        .add(item(Items.NETHER_WART, 2, 6, 20))
                        .add(item(Items.CHORUS_FRUIT, 1, 4, 10))
                        .add(item(Items.GOLDEN_CARROT, 1, 2, 5))));

        // mob loot - sword
        output.accept(FactionLootTables.MINION_COLLECT_MOB_LOOT, LootTable.lootTable()
                .withPool(pool(1)
                        .add(item(Items.ROTTEN_FLESH, 2, 6, 30))
                        .add(item(Items.BONE, 1, 4, 20))
                        .add(item(Items.FEATHER, 1, 4, 15))
                        .add(item(Items.LEATHER, 1, 2, 10)))
                .withPool(pool(1).when(tool(FactionItemTags.MINION_SWORD.base()))
                        .add(item(Items.STRING, 2, 6, 25))
                        .add(item(Items.ARROW, 4, 10, 20))
                        .add(item(Items.SPIDER_EYE, 1, 3, 15))
                        .add(item(Items.GUNPOWDER, 1, 4, 15))
                        .add(item(Items.BEEF, 1, 4, 15)))
                .withPool(pool(1).when(tool(FactionItemTags.MINION_SWORD.tier1()))
                        .add(item(Items.ENDER_PEARL, 1, 2, 20))
                        .add(item(Items.SLIME_BALL, 2, 6, 20))
                        .add(item(Items.BLAZE_ROD, 1, 3, 15))
                        .add(item(Items.PHANTOM_MEMBRANE, 1, 2, 10)))
                .withPool(pool(1).when(tool(FactionItemTags.MINION_SWORD.tier2()))
                        .add(item(Items.GHAST_TEAR, 1, 2, 20))));
    }

    private static LootPool.Builder pool(int rolls) {
        return LootPool.lootPool().setRolls(ConstantValue.exactly(rolls));
    }

    private static LootPoolSingletonContainer.Builder<?> item(ItemLike item, int min, int max, int weight) {
        LootPoolSingletonContainer.Builder<?> builder = LootItem.lootTableItem(item).setWeight(weight);
        if (max > 1) {
            builder.apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
        }
        return builder;
    }

    private LootItemCondition.Builder tool(TagKey<Item> tag) {
        return MatchTool.toolMatches(ItemPredicate.Builder.item().of(this.items, tag));
    }

    private LootItemCondition.Builder fishingRod(int minLuckOfTheSea) {
        var enchantment = this.registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LUCK_OF_THE_SEA);
        return MatchTool.toolMatches(ItemPredicate.Builder.item().of(this.items, FactionItemTags.MINION_FISHING_ROD)
                .withComponents(DataComponentMatchers.Builder.components()
                        .partial(DataComponentPredicates.ENCHANTMENTS, EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(enchantment, MinMaxBounds.Ints.atLeast(minLuckOfTheSea)))))
                        .build()));
    }
}
