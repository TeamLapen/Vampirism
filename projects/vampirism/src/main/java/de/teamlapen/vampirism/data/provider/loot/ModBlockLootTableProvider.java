package de.teamlapen.vampirism.data.provider.loot;

import de.teamlapen.faction.common.core.FactionBlocks;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.util.ColorListsUtil;
import de.teamlapen.vampirism.common.world.blocks.*;
import de.teamlapen.vampirism.common.world.blocks.base.BaseSplitBlock;
import de.teamlapen.vampirism.data.loot.conditions.TentSpawnerCondition;
import de.teamlapen.vampirism.misc.mixin.accessor.VanillaBlockLootAccessor;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.util.Unit;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    /**
     * Copied from {@link net.minecraft.data.loot.BlockLootSubProvider} but halved
     */
    public static final float[] DARK_LEAVES_SAPLING_CHANCES = new float[] { 0.025F, 0.03125f, 0.041666668f, 0.05f };

    public ModBlockLootTableProvider(HolderLookup.Provider lookupProvider) {
        super(VanillaBlockLootAccessor.getExplosionResistantBlocks(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.ALCHEMICAL_CAULDRON.get());
        this.dropSelf(ModBlocks.ALTAR_INFUSION.get());
        this.dropSelf(ModBlocks.ALTAR_INSPIRATION.get());
        this.add(ModBlocks.ALTAR_PILLAR.get(), createSingleItemTable(ModBlocks.ALTAR_PILLAR.get())
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(ExplosionCondition.survivesExplosion())
                        .add(LootItem.lootTableItem(Items.STONE_BRICKS).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.ALTAR_PILLAR.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.PILLAR_TYPE, "stone"))))
                        .add(LootItem.lootTableItem(Items.IRON_BLOCK).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.ALTAR_PILLAR.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.PILLAR_TYPE, "iron"))))
                        .add(LootItem.lootTableItem(Items.GOLD_BLOCK).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.ALTAR_PILLAR.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.PILLAR_TYPE, "gold"))))
                        .add(LootItem.lootTableItem(Items.BONE_BLOCK).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.ALTAR_PILLAR.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.PILLAR_TYPE, "bone"))))));
        this.dropSelf(ModBlocks.ALTAR_TIP.get());
        ColorListsUtil.COFFINS.forEach(coffin -> this.add(coffin, block -> createSinglePropConditionTable(block, CoffinBlock.PART, CoffinBlock.CoffinPart.HEAD)));
        this.dropSelf(ModBlocks.BLOOD_CONTAINER.get());
        this.dropSelf(ModBlocks.BLOOD_GRINDER.get());
        this.dropSelf(ModBlocks.BLOOD_PEDESTAL.get());
        this.dropSelf(ModBlocks.POTION_TABLE.get());
        this.dropSelf(ModBlocks.BLOOD_SIEVE.get());
        this.dropSelf(ModBlocks.DARK_STONE_BRICKS.get());
        this.dropSelf(ModBlocks.BLOODY_DARK_STONE_BRICKS.get());
        this.dropSelf(ModBlocks.DARK_STONE.get());
        this.dropSelf(ModBlocks.DARK_STONE_BRICK_SLAB.get());
        this.dropSelf(ModBlocks.DARK_STONE_SLAB.get());
        this.dropSelf(ModBlocks.DARK_STONE_BRICK_STAIRS.get());
        this.dropSelf(ModBlocks.DARK_STONE_STAIRS.get());
        this.dropSelf(ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get());
        this.dropSelf(ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get());
        this.dropSelf(ModBlocks.DARK_SPRUCE_PLANKS.get());
        this.dropSelf(ModBlocks.CURSED_SPRUCE_PLANKS.get());
        this.dropSelf(ModBlocks.DARK_SPRUCE_TRAPDOOR.get());
        this.dropSelf(ModBlocks.CURSED_SPRUCE_TRAPDOOR.get());
        this.add(ModBlocks.DARK_SPRUCE_DOOR.get(), this::createDoorTable);
        this.add(ModBlocks.CURSED_SPRUCE_DOOR.get(), this::createDoorTable);
        this.dropSelf(ModBlocks.ALTAR_CLEANSING.get());
        this.dropSelf(ModBlocks.CURSED_EARTH.get());
        this.dropSelf(ModBlocks.FIRE_PLACE.get());
        this.add(ModBlocks.GARLIC.get(), applyExplosionDecay(ModBlocks.GARLIC.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModBlocks.GARLIC.get())))
                .withPool(LootPool.lootPool()
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.GARLIC.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(GarlicBlock.AGE, 7)))
                        .add(LootItem.lootTableItem(ModBlocks.GARLIC.get()).apply(ApplyBonusCount.addBonusBinomialDistributionCount(this.registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))))));
        this.dropSelf(ModBlocks.GARLIC_DIFFUSER_WEAK.get());
        this.dropSelf(ModBlocks.GARLIC_DIFFUSER_NORMAL.get());
        this.dropSelf(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get());
        this.dropSelf(ModBlocks.HUNTER_TABLE.get());
        this.add(ModBlocks.MED_CHAIR.get(), block -> createSinglePropConditionTable(block, MedChairBlock.PART, MedChairBlock.EnumPart.BOTTOM));
        this.dropSelf(ModBlocks.SUNSCREEN_BEACON.get());
        this.add(ModBlocks.TENT_MAIN.get(), createSingleItemTable(ModItems.ITEM_TENT.get())
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(TentSpawnerCondition.builder())
                        .add(LootItem.lootTableItem(Items.APPLE))
                        .add(LootItem.lootTableItem(Items.BREAD))
                        .add(LootItem.lootTableItem(Items.COAL))
                        .add(LootItem.lootTableItem(Blocks.OAK_PLANKS))));
        this.add(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED.get(), createSingleItemTable(FactionBlocks.TOTEM_TOP_CRAFTED.get()));
        this.add(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED.get(), createSingleItemTable(FactionBlocks.TOTEM_TOP_CRAFTED.get()));
        this.add(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get(), noDrop());
        this.add(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get(), noDrop());

        this.dropSelf(ModBlocks.VAMPIRE_ORCHID.get());
        this.dropSelf(ModBlocks.WEAPON_TABLE.get());
        this.add(ModBlocks.TENT.get(), noDrop());
        this.dropPottedContents(ModBlocks.POTTED_VAMPIRE_ORCHID.get());
        this.dropSelf(ModBlocks.DARK_SPRUCE_SAPLING.get());
        this.dropSelf(ModBlocks.CURSED_SPRUCE_SAPLING.get());
        this.add(ModBlocks.DARK_SPRUCE_LEAVES.get(), (block) -> createLeavesDrops(block, ModBlocks.DARK_SPRUCE_SAPLING.get(), DARK_LEAVES_SAPLING_CHANCES));
        this.add(ModBlocks.CROSS.get(), (p_218567_0_) -> createSinglePropConditionTable(p_218567_0_, BaseSplitBlock.PART, BaseSplitBlock.Part.MAIN));
        this.dropSelf(ModBlocks.TOMBSTONE1.get());
        this.dropSelf(ModBlocks.TOMBSTONE2.get());
        this.add(ModBlocks.TOMBSTONE3.get(), context -> createSinglePropConditionTable(context, BaseSplitBlock.PART, BaseSplitBlock.Part.MAIN));
        this.dropSelf(ModBlocks.GRAVE_CAGE.get());
        this.add(ModBlocks.CURSED_GRASS.get(), block -> createSingleItemTableWithSilkTouch(block, ModBlocks.CURSED_EARTH.get()));
        this.dropSelf(ModBlocks.DARK_SPRUCE_LOG.get());
        this.dropPottedContents(ModBlocks.POTTED_CURSED_ROOTS.get());
        this.dropCursedSpruce(ModBlocks.CURSED_SPRUCE_LOG.get());
        this.add(ModBlocks.DIRECT_CURSED_BARK.get(), noDrop());
        this.dropSelf(ModBlocks.DARK_SPRUCE_STAIRS.get());
        this.dropSelf(ModBlocks.CURSED_SPRUCE_STAIRS.get());
        this.dropSelf(ModBlocks.DARK_SPRUCE_WOOD.get());
        this.dropCursedSpruce(ModBlocks.CURSED_SPRUCE_WOOD.get());
        this.dropSelf(ModBlocks.STRIPPED_DARK_SPRUCE_WOOD.get());
        this.dropSelf(ModBlocks.STRIPPED_CURSED_SPRUCE_WOOD.get());
        this.dropSelf(ModBlocks.DARK_SPRUCE_SIGN.get());
        this.dropSelf(ModBlocks.CURSED_SPRUCE_SIGN.get());
        this.dropSelf(ModBlocks.DARK_SPRUCE_WALL_SIGN.get());
        this.dropSelf(ModBlocks.CURSED_SPRUCE_WALL_SIGN.get());
        this.dropSelf(ModBlocks.DARK_SPRUCE_PRESSURE_PLACE.get());
        this.dropSelf(ModBlocks.CURSED_SPRUCE_PRESSURE_PLACE.get());
        this.dropSelf(ModBlocks.DARK_SPRUCE_BUTTON.get());
        this.dropSelf(ModBlocks.CURSED_SPRUCE_BUTTON.get());
        this.dropSelf(ModBlocks.DARK_SPRUCE_SLAB.get());
        this.dropSelf(ModBlocks.CURSED_SPRUCE_SLAB.get());
        this.dropSelf(ModBlocks.DARK_SPRUCE_FENCE_GATE.get());
        this.dropSelf(ModBlocks.CURSED_SPRUCE_FENCE_GATE.get());
        this.dropSelf(ModBlocks.DARK_SPRUCE_FENCE.get());
        this.dropSelf(ModBlocks.CURSED_SPRUCE_FENCE.get());
        this.add(ModBlocks.CURSED_ROOTS.get(), (block) -> createShearsDispatchTable(block, applyExplosionDecay(block, LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))))));
        this.dropSelf(ModBlocks.VAMPIRE_RACK.get());
        this.add(ModBlocks.THRONE.get(), (p_218567_0_) -> createSinglePropConditionTable(p_218567_0_, BaseSplitBlock.PART, BaseSplitBlock.Part.MAIN));
        this.dropSelf(ModBlocks.ALCHEMY_TABLE.get());
        this.add(ModBlocks.DIAGONAL_CURSED_BARK.get(), noDrop());
        this.dropSelf(ModBlocks.DARK_SPRUCE_HANGING_SIGN.get());
        this.dropSelf(ModBlocks.CURSED_SPRUCE_HANGING_SIGN.get());
        this.dropOther(ModBlocks.CURSED_EARTH_PATH.get(), ModBlocks.CURSED_EARTH.get());
        this.dropSelf(ModBlocks.CRACKED_DARK_STONE_BRICKS.get());
        this.add(ModBlocks.DARK_STONE.get(), block -> createSingleItemTableWithSilkTouch(block, ModBlocks.COBBLED_DARK_STONE.get()));
        this.dropSelf(ModBlocks.DARK_STONE_STAIRS.get());
        this.dropSelf(ModBlocks.DARK_STONE_SLAB.get());
        this.dropSelf(ModBlocks.DARK_STONE_WALL.get());
        this.dropSelf(ModBlocks.DARK_STONE_BRICKS.get());
        this.dropSelf(ModBlocks.DARK_STONE_BRICK_STAIRS.get());
        this.dropSelf(ModBlocks.DARK_STONE_BRICK_SLAB.get());
        this.dropSelf(ModBlocks.DARK_STONE_BRICK_WALL.get());
        this.dropSelf(ModBlocks.COBBLED_DARK_STONE.get());
        this.dropSelf(ModBlocks.COBBLED_DARK_STONE_STAIRS.get());
        this.dropSelf(ModBlocks.COBBLED_DARK_STONE_SLAB.get());
        this.dropSelf(ModBlocks.COBBLED_DARK_STONE_WALL.get());
        this.dropSelf(ModBlocks.POLISHED_DARK_STONE.get());
        this.dropSelf(ModBlocks.POLISHED_DARK_STONE_STAIRS.get());
        this.dropSelf(ModBlocks.POLISHED_DARK_STONE_SLAB.get());
        this.dropSelf(ModBlocks.POLISHED_DARK_STONE_WALL.get());
        this.dropSelf(ModBlocks.DARK_STONE_TILES.get());
        this.dropSelf(ModBlocks.DARK_STONE_TILES_STAIRS.get());
        this.dropSelf(ModBlocks.DARK_STONE_TILES_SLAB.get());
        this.dropSelf(ModBlocks.DARK_STONE_TILES_WALL.get());
        this.dropSelf(ModBlocks.CRACKED_DARK_STONE_TILES.get());
        this.dropSelf(ModBlocks.CHISELED_DARK_STONE_BRICKS.get());
        this.otherWhenSilkTouch(ModBlocks.INFESTED_DARK_STONE.get(), ModBlocks.DARK_STONE.get());
        this.dropSelf(ModBlocks.BAT_CAGE.get());
        this.add(ModBlocks.CURSED_HANGING_ROOTS.get(), this::createShearsOnlyDrop);
        this.add(ModBlocks.MOTHER.get(),
                createSingleItemTable(ModItems.MOTHER_CORE.get())
                        .withPool(applyExplosionCondition(ModBlocks.MOTHER_TROPHY.get(), LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModBlocks.MOTHER_TROPHY.get()))))
                        .withPool(applyExplosionCondition(ModItems.SOUL_ORB_VAMPIRE.get(), LootPool.lootPool().name("souls").setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(ModItems.SOUL_ORB_VAMPIRE.get()).setQuality(10))))
                        .withPool(LootPool.lootPool().name("bonus").setRolls(UniformGenerator.between(1, 4))
                                .add(applyExplosionCondition(ModItems.PURE_BLOOD_4.get(), LootItem.lootTableItem(ModItems.PURE_BLOOD_4.get()).setQuality(2)))
                                .add(applyExplosionCondition(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), LootItem.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setQuality(10)))));
        this.dropSelf(ModBlocks.MOTHER_TROPHY.get());
        this.dropSelf(ModBlocks.FOG_DIFFUSER.get());
        this.dropPottedContents(ModBlocks.POTTED_DARK_SPRUCE_SAPLING.get());
        this.dropPottedContents(ModBlocks.POTTED_CURSED_SPRUCE_SAPLING.get());
        this.dropSelf(ModBlocks.BLOOD_INFUSED_IRON_BLOCK.get());
        this.dropSelf(ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK.get());
        this.add(ModBlocks.VAMPIRE_BEACON.get(), this::createNameableBlockEntityTable);
        this.dropSelf(ModBlocks.PURPLE_STONE_BRICKS.get());
        this.dropSelf(ModBlocks.PURPLE_STONE_BRICK_STAIRS.get());
        this.dropSelf(ModBlocks.PURPLE_STONE_BRICK_SLAB.get());
        this.dropSelf(ModBlocks.PURPLE_STONE_BRICK_WALL.get());
        this.dropSelf(ModBlocks.PURPLE_STONE_TILES.get());
        this.dropSelf(ModBlocks.PURPLE_STONE_TILES_STAIRS.get());
        this.dropSelf(ModBlocks.PURPLE_STONE_TILES_SLAB.get());
        this.dropSelf(ModBlocks.PURPLE_STONE_TILES_WALL.get());
        this.dropSelf(ModBlocks.VAMPIRE_SOUL_LANTERN.get());
        this.dropSelf(ModBlocks.INFUSER.get());

        ColorListsUtil.STANDING_AND_WALL_CANDLE_STICKS.forEach(pair -> this.dropSelf(pair.getFirst()));
        ColorListsUtil.STANDING_AND_WALL_CANDELABRAS.forEach(pair -> this.dropSelf(pair.getFirst()));
        ColorListsUtil.HANGING_CHANDELIERS.forEach(this::dropSelf);
    }

    private void dropCursedSpruce(CursedSpruceBlock block) {
        this.add(block, LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(
                                        LootItem.lootTableItem(block.asItem())
                                                .when(this.hasSilkTouch())
                                                .apply(SetComponentsFunction.setComponent(ModDataComponents.ACTIVE.get(), Unit.INSTANCE))
                                                .otherwise(LootItem.lootTableItem(block.asItem()))
                                )
                )
        );
    }


    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.getAllBlocks();
    }
}
