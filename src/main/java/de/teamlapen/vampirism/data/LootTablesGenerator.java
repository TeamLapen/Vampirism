package de.teamlapen.vampirism.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModLootTables;
import de.teamlapen.vampirism.world.loot.*;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.conditions.KilledByPlayer;
import net.minecraft.loot.conditions.RandomChanceWithLooting;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.loot.functions.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootTablesGenerator extends LootTableProvider {

    /**
     * copied from {@link BlockLootTables}
     */
    public static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};


    public LootTablesGenerator(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Nonnull
    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {

        return ImmutableList.of(Pair.of(ModEntityLootTables::new, LootParameterSets.ENTITY), Pair.of(ModChestLootTables::new, LootParameterSets.CHEST), Pair.of(ModBlockLootTables::new, LootParameterSets.BLOCK), Pair.of(InjectLootTables::new, LootParameterSets.CHEST));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        for (ResourceLocation resourcelocation : Sets.difference(ModLootTables.getLootTables(), map.keySet())) {
            validationtracker.reportProblem("Missing built-in table: " + resourcelocation);
        }
        map.forEach((resourceLocation, lootTable) -> LootTableManager.validate(validationtracker, resourceLocation, lootTable));
    }

    private static class ModEntityLootTables extends EntityLootTables {
        @Override
        protected void addTables() {
            CompoundNBT splash = new CompoundNBT();
            splash.putBoolean("splash", true);

            LootTable.Builder advanced_hunter = LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("general").when(KilledByPlayer.killedByPlayer()).setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_blood_bottle).setWeight(4))
                            .add(ItemLootEntry.lootTableItem(ModItems.item_garlic).setWeight(4).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0, 1))))
                            .add(ItemLootEntry.lootTableItem(ModItems.holy_water_bottle_enhanced).setWeight(3).apply(SetNBT.setTag(splash)))
                            .add(ItemLootEntry.lootTableItem(ModItems.holy_water_bottle_ultimate).setWeight(1).apply(SetNBT.setTag(splash)))
                            .add(ItemLootEntry.lootTableItem(ModItems.holy_salt).setWeight(4).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0, 1))).apply(SetCount.setCount(RandomValueRange.between(1, 2)))))
                    .withPool(LootPool.lootPool().name("special").when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.05f, 0.01f)).setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_book).setWeight(1).apply(AddBookNbt.builder())));
            this.add(ModEntities.advanced_hunter, advanced_hunter);
            this.add(ModEntities.advanced_hunter_imob, advanced_hunter);
            LootTable.Builder advanced_vampire = LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("general").when(KilledByPlayer.killedByPlayer()).setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_blood_bottle).setWeight(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.blood_bottle).setWeight(1).apply(SetDamage.setDamage(RandomValueRange.between(0.5f, 1.0f))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(1f, 1f)))))
                    .withPool(LootPool.lootPool().name("special").when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.1f, 0.01f)).setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_book).setWeight(1).apply(AddBookNbt.builder())))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(1)).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.05f, 0.01f))
                            .add(ItemLootEntry.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))));
            this.add(ModEntities.advanced_vampire, advanced_vampire);
            this.add(ModEntities.advanced_vampire_imob, advanced_vampire);
            this.add(ModEntities.blinding_bat, LootTable.lootTable());
            this.add(ModEntities.converted_creature, LootTable.lootTable());
            this.add(ModEntities.converted_creature_imob, LootTable.lootTable());
            this.add(ModEntities.converted_sheep, LootTable.lootTable());
            this.add(ModEntities.converted_cow, LootTable.lootTable());
            this.add(ModEntities.converted_horse, LootTable.lootTable());
            this.add(ModEntities.converted_donkey, LootTable.lootTable());
            this.add(ModEntities.converted_mule, LootTable.lootTable());
            this.add(ModEntities.dummy_creature, LootTable.lootTable());
            this.add(ModEntities.hunter_trainer, LootTable.lootTable());
            LootTable.Builder vampire = LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("general").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.33f, 0.05f))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_fang).setWeight(1)))
                    .withPool(LootPool.lootPool().name("special").setRolls(ConstantRange.exactly(1)).when(StakeCondition.builder(LootContext.EntityTarget.KILLER_PLAYER)).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.5f, 0.05f))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_blood_bottle).setWeight(1)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(1)).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.02f, 0.01f))
                            .add(ItemLootEntry.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))));
            this.add(ModEntities.vampire, vampire);
            this.add(ModEntities.vampire_imob, vampire);
            this.add(ModEntities.vampire_baron, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("pure_blood_0").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(AdjustableLevelCondition.builder(0, LootContext.EntityTarget.THIS))
                            .add(ItemLootEntry.lootTableItem(ModItems.pure_blood_0).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_1").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(AdjustableLevelCondition.builder(1, LootContext.EntityTarget.THIS))
                            .add(ItemLootEntry.lootTableItem(ModItems.pure_blood_1).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_2").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(AdjustableLevelCondition.builder(2, LootContext.EntityTarget.THIS))
                            .add(ItemLootEntry.lootTableItem(ModItems.pure_blood_2).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_3").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(AdjustableLevelCondition.builder(3, LootContext.EntityTarget.THIS))
                            .add(ItemLootEntry.lootTableItem(ModItems.pure_blood_3).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_4").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(AdjustableLevelCondition.builder(4, LootContext.EntityTarget.THIS))
                            .add(ItemLootEntry.lootTableItem(ModItems.pure_blood_4).setWeight(1))));
            LootTable.Builder hunter = LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("general").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.33f, 0.005f))
                            .add(ItemLootEntry.lootTableItem(ModItems.human_heart).setWeight(1)))
                    .withPool(LootPool.lootPool().name("special").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.25f, 0.05f))
                            .add(ItemLootEntry.lootTableItem(ModItems.holy_salt).setWeight(1)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(1)).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.05f, 0.02f))
                            .add(ItemLootEntry.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))));
            this.add(ModEntities.hunter, hunter);
            this.add(ModEntities.hunter_imob, hunter);
            this.add(ModEntities.villager_angry, LootTable.lootTable());
            this.add(ModEntities.villager_converted, LootTable.lootTable());
            this.add(ModEntities.task_master_vampire, LootTable.lootTable());
            this.add(ModEntities.task_master_hunter, LootTable.lootTable());
            this.add(ModEntities.vampire_minion, LootTable.lootTable());
            this.add(ModEntities.hunter_minion, LootTable.lootTable());
        }

        @Nonnull
        @Override
        protected Iterable<EntityType<?>> getKnownEntities() {
            return ModEntities.getAllEntities();
        }
    }

    private static class ModChestLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            consumer.accept(ModLootTables.chest_hunter_trainer, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(RandomValueRange.between(5, 9))
                            .add(ItemLootEntry.lootTableItem(Items.IRON_INGOT).setWeight(40))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_blood_bottle).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.item_garlic).setWeight(40)))
                    .withPool(LootPool.lootPool().name("book").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_book).setWeight(50).apply(AddBookNbt.builder()))
                            .add(EmptyLootEntry.emptyItem().setWeight(95)))
                    .withPool(LootPool.lootPool().name("hunter_weapons").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.hunter_axe_ultimate).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(95)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_head_ultimate).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_chest_ultimate).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_legs_ultimate).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_feet_ultimate).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("hunter_coat").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.hunter_coat_head_ultimate).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.hunter_coat_chest_ultimate).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.hunter_coat_legs_ultimate).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.hunter_coat_feet_ultimate).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("holy_water").setRolls(ConstantRange.exactly(5))
                            .add(ItemLootEntry.lootTableItem(ModItems.holy_salt).setWeight(50))
                            .add(ItemLootEntry.lootTableItem(ModItems.holy_water_bottle_normal).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.holy_water_bottle_enhanced).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.holy_water_bottle_ultimate).setWeight(10))));
            consumer.accept(ModLootTables.chest_vampire_dungeon, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(7))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_fang).setWeight(35))
                            .add(ItemLootEntry.lootTableItem(ModItems.blood_bottle).setWeight(20).apply(SetDamage.setDamage(RandomValueRange.between(1f, 1f)))))
                    .withPool(LootPool.lootPool().name("book").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_book).setWeight(70).apply(AddBookNbt.builder()))
                            .add(EmptyLootEntry.emptyItem().setWeight(30)))
                    .withPool(LootPool.lootPool().name("equipment").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.heart_seeker_enhanced).setWeight(21).apply(SetDamage.setDamage(RandomValueRange.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(RandomValueRange.between(500f, 2000f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.heart_seeker_ultimate).setWeight(9).apply(SetDamage.setDamage(RandomValueRange.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(RandomValueRange.between(500f, 2000f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.heart_striker_enhanced).setWeight(21).apply(SetDamage.setDamage(RandomValueRange.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(RandomValueRange.between(500f, 2000f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.heart_striker_ultimate).setWeight(9).apply(SetDamage.setDamage(RandomValueRange.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(RandomValueRange.between(500f, 2000f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(40)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(3))
                            .add(ItemLootEntry.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))));
        }
    }

    private static class ModBlockLootTables extends BlockLootTables {
        @Override
        protected void addTables() {
            this.dropSelf(ModBlocks.alchemical_cauldron);
            this.dropSelf(ModBlocks.altar_infusion);
            this.dropSelf(ModBlocks.altar_inspiration);
            this.add(ModBlocks.altar_pillar, createSingleItemTable(ModBlocks.altar_pillar)
                    .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).when(SurvivesExplosion.survivesExplosion())
                            .add(ItemLootEntry.lootTableItem(Items.STONE_BRICKS).when(BlockStateProperty.hasBlockStateProperties(ModBlocks.altar_pillar).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "stone"))))
                            .add(ItemLootEntry.lootTableItem(Items.IRON_BLOCK).when(BlockStateProperty.hasBlockStateProperties(ModBlocks.altar_pillar).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "iron"))))
                            .add(ItemLootEntry.lootTableItem(Items.GOLD_BLOCK).when(BlockStateProperty.hasBlockStateProperties(ModBlocks.altar_pillar).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "gold"))))
                            .add(ItemLootEntry.lootTableItem(Items.BONE_BLOCK).when(BlockStateProperty.hasBlockStateProperties(ModBlocks.altar_pillar).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "bone"))))));
            this.dropSelf(ModBlocks.altar_tip);
            this.add(ModBlocks.coffin, block -> createSinglePropConditionTable(block, CoffinBlock.PART, CoffinBlock.CoffinPart.HEAD));
            this.dropSelf(ModBlocks.blood_container);
            this.dropSelf(ModBlocks.blood_grinder);
            this.dropSelf(ModBlocks.blood_pedestal);
            this.dropSelf(ModBlocks.potion_table);
            this.dropSelf(ModBlocks.blood_sieve);
            this.dropSelf(ModBlocks.castle_block_dark_brick);
            this.dropSelf(ModBlocks.castle_block_dark_brick_bloody);
            this.dropSelf(ModBlocks.castle_block_dark_stone);
            this.dropSelf(ModBlocks.castle_block_normal_brick);
            this.dropSelf(ModBlocks.castle_block_purple_brick);
            this.dropSelf(ModBlocks.castle_slab_dark_brick);
            this.dropSelf(ModBlocks.castle_slab_dark_stone);
            this.dropSelf(ModBlocks.castle_slab_purple_brick);
            this.dropSelf(ModBlocks.castle_stairs_dark_brick);
            this.dropSelf(ModBlocks.castle_stairs_dark_stone);
            this.dropSelf(ModBlocks.castle_stairs_purple_brick);
            this.dropSelf(ModBlocks.stripped_dark_spruce_log);
            this.dropSelf(ModBlocks.stripped_cursed_spruce_log);
            this.dropSelf(ModBlocks.dark_spruce_planks);
            this.dropSelf(ModBlocks.cursed_spruce_planks);
            this.dropSelf(ModBlocks.dark_spruce_trapdoor);
            this.dropSelf(ModBlocks.cursed_spruce_trapdoor);
            this.add(ModBlocks.dark_spruce_door, BlockLootTables::createDoorTable);
            this.add(ModBlocks.cursed_spruce_door, BlockLootTables::createDoorTable);
            this.dropSelf(ModBlocks.church_altar);
            this.dropSelf(ModBlocks.cursed_earth);
            this.dropSelf(ModBlocks.fire_place);
            this.add(ModBlocks.garlic, applyExplosionDecay(ModBlocks.garlic, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(ItemLootEntry.lootTableItem(ModItems.item_garlic)))
                    .withPool(LootPool.lootPool()
                            .when(BlockStateProperty.hasBlockStateProperties(ModBlocks.garlic).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(GarlicBlock.AGE, 7)))
                            .add(ItemLootEntry.lootTableItem(ModItems.item_garlic).apply(ApplyBonus.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))));
            this.dropSelf(ModBlocks.garlic_beacon_weak);
            this.dropSelf(ModBlocks.garlic_beacon_normal);
            this.dropSelf(ModBlocks.garlic_beacon_improved);
            this.dropSelf(ModBlocks.hunter_table);
            this.add(ModBlocks.med_chair, block -> createSinglePropConditionTable(block, MedChairBlock.PART, MedChairBlock.EnumPart.TOP));
            this.dropSelf(ModBlocks.sunscreen_beacon);
            this.add(ModBlocks.tent_main, createSingleItemTable(ModItems.item_tent)
                    .withPool(LootPool.lootPool().name("bonus").setRolls(ConstantRange.exactly(1)).when(TentSpawnerCondition.builder())
                            .add(ItemLootEntry.lootTableItem(Items.APPLE))
                            .add(ItemLootEntry.lootTableItem(Items.BREAD))
                            .add(ItemLootEntry.lootTableItem(Items.COAL))
                            .add(ItemLootEntry.lootTableItem(Blocks.OAK_PLANKS))));
            this.dropSelf(ModBlocks.totem_base);
            this.dropSelf(ModBlocks.totem_top_crafted);
            this.add(ModBlocks.totem_top_vampirism_vampire_crafted, createSingleItemTable(ModBlocks.totem_top_crafted));
            this.add(ModBlocks.totem_top_vampirism_hunter_crafted, createSingleItemTable(ModBlocks.totem_top_crafted));
            this.add(ModBlocks.totem_top, noDrop());
            this.add(ModBlocks.totem_top_vampirism_hunter, noDrop());
            this.add(ModBlocks.totem_top_vampirism_vampire, noDrop());

            this.dropSelf(ModBlocks.vampire_orchid);
            this.dropSelf(ModBlocks.weapon_table);
            this.add(ModBlocks.tent, noDrop());
            this.dropPottedContents(ModBlocks.potted_vampire_orchid);
            this.dropSelf(ModBlocks.dark_spruce_sapling);
            this.dropSelf(ModBlocks.cursed_spruce_sapling);
            this.add(ModBlocks.dark_spruce_leaves, (block) -> createLeavesDrops(block, ModBlocks.dark_spruce_sapling, DEFAULT_SAPLING_DROP_RATES));
            this.dropSelf(ModBlocks.chandelier);
            this.add(ModBlocks.candelabra_wall, createSingleItemTable(ModItems.item_candelabra));
            this.add(ModBlocks.candelabra, createSingleItemTable(ModItems.item_candelabra));
            this.add(ModBlocks.cross, (p_218567_0_) -> createSinglePropConditionTable(p_218567_0_, VampirismSplitBlock.PART, VampirismSplitBlock.Part.MAIN));
            this.dropSelf(ModBlocks.tombstone1);
            this.dropSelf(ModBlocks.tombstone2);
            this.dropSelf(ModBlocks.tombstone3);
            this.dropSelf(ModBlocks.grave_cage);
            this.add(ModBlocks.cursed_grass, createSingleItemTable(ModBlocks.cursed_earth));
            this.dropSelf(ModBlocks.dark_spruce_log);
            this.add(ModBlocks.cursed_roots, (block) -> {
                return createShearsDispatchTable(block, applyExplosionDecay(block, ItemLootEntry.lootTableItem(Items.STICK).apply(SetCount.setCount(RandomValueRange.between(0.0F, 2.0F)))));
            });
            this.dropPottedContents(ModBlocks.potted_cursed_roots);
            this.dropSelf(ModBlocks.cursed_spruce_log);
            this.add(ModBlocks.cursed_bark, noDrop());
            this.dropSelf(ModBlocks.dark_spruce_stairs);
            this.dropSelf(ModBlocks.cursed_spruce_stairs);
            this.dropSelf(ModBlocks.dark_spruce_wood);
            this.dropSelf(ModBlocks.cursed_spruce_wood);
            this.dropSelf(ModBlocks.stripped_dark_spruce_wood);
            this.dropSelf(ModBlocks.stripped_cursed_spruce_wood);
            this.dropSelf(ModBlocks.dark_spruce_sign);
            this.dropSelf(ModBlocks.cursed_spruce_sign);
            this.dropSelf(ModBlocks.dark_spruce_wall_sign);
            this.dropSelf(ModBlocks.cursed_spruce_wall_sign);
            this.dropSelf(ModBlocks.dark_spruce_pressure_place);
            this.dropSelf(ModBlocks.cursed_spruce_pressure_place);
            this.dropSelf(ModBlocks.dark_spruce_button);
            this.dropSelf(ModBlocks.cursed_spruce_button);
            this.dropSelf(ModBlocks.dark_spruce_slab);
            this.dropSelf(ModBlocks.cursed_spruce_slab);
            this.dropSelf(ModBlocks.dark_spruce_fence_gate);
            this.dropSelf(ModBlocks.cursed_spruce_fence_gate);
            this.dropSelf(ModBlocks.dark_spruce_fence);
            this.dropSelf(ModBlocks.cursed_spruce_fence);
            this.dropSelf(ModBlocks.vampire_rack);
            this.add(ModBlocks.throne, (p_218567_0_) -> createSinglePropConditionTable(p_218567_0_, VampirismSplitBlock.PART, VampirismSplitBlock.Part.MAIN));
            this.dropSelf(ModBlocks.alchemical_table);
        }

        @Nonnull
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ModBlocks.getAllBlocks();
        }
    }

    private static class InjectLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            consumer.accept(ModLootTables.abandoned_mineshaft, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(RandomValueRange.between(0f, 4f))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_fang).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.item_garlic).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.blood_bottle).setWeight(15).apply(SetDamage.setDamage(RandomValueRange.between(1f, 1f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_book).setWeight(5).apply(AddBookNbt.builder()))
                            .add(EmptyLootEntry.emptyItem().setWeight(40)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_head_ultimate).setWeight(3).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_chest_ultimate).setWeight(3).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_legs_ultimate).setWeight(3).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_feet_ultimate).setWeight(3).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(88)))
                    .withPool(LootPool.lootPool().name("hunter_weapons").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.hunter_axe_ultimate).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(95)))
                    .withPool(LootPool.lootPool().name("vampire_weapons").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.heart_seeker_enhanced).setWeight(20).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.heart_striker_enhanced).setWeight(20).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("holy_Water").setRolls(ConstantRange.exactly(2))
                            .add(ItemLootEntry.lootTableItem(ModItems.holy_salt).setWeight(50))
                            .add(ItemLootEntry.lootTableItem(ModItems.holy_water_bottle_normal).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.holy_water_bottle_enhanced).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.holy_water_bottle_ultimate).setWeight(10)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.desert_pyramid, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.item_garlic).setWeight(15))
                            .add(ItemLootEntry.lootTableItem(ModItems.blood_bottle).setWeight(20).apply(SetDamage.setDamage(RandomValueRange.between(0.6f, 0.6f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_book).setWeight(8).apply(AddBookNbt.builder()))
                            .add(EmptyLootEntry.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.jungle_temple, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(2))
                            .add(ItemLootEntry.lootTableItem(ModItems.item_garlic).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.blood_bottle).setWeight(20).apply(SetDamage.setDamage(RandomValueRange.between(1f, 1f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_book).setWeight(20).apply(AddBookNbt.builder()))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_fang).setWeight(20))
                            .add(EmptyLootEntry.emptyItem().setWeight(30)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_head_ultimate).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_chest_ultimate).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_legs_ultimate).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_feet_ultimate).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(72)))
                    .withPool(LootPool.lootPool().name("hunter_coat").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.hunter_coat_head_ultimate).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.hunter_coat_chest_ultimate).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.hunter_coat_legs_ultimate).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.hunter_coat_feet_ultimate).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(72)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.stronghold_corridor, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(2))
                            .add(ItemLootEntry.lootTableItem(ModItems.item_garlic).setWeight(50))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_book).setWeight(20).apply(AddBookNbt.builder()))
                            .add(EmptyLootEntry.emptyItem().setWeight(27)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_head_ultimate).setWeight(5).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_chest_ultimate).setWeight(5).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_legs_ultimate).setWeight(5).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.armor_of_swiftness_feet_ultimate).setWeight(5).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(80)))
                    .withPool(LootPool.lootPool().name("vampire_weapons").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.heart_seeker_enhanced).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.heart_striker_enhanced).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(80)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(3))
                            .add(ItemLootEntry.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.stronghold_library, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.vampire_book).setWeight(1).apply(AddBookNbt.builder())))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(3))
                            .add(ItemLootEntry.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
        }
    }

}
