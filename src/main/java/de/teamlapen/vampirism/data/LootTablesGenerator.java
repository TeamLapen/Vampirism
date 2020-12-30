package de.teamlapen.vampirism.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.blocks.AltarPillarBlock;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.GarlicBlock;
import de.teamlapen.vampirism.blocks.MedChairBlock;
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
            validationtracker.addProblem("Missing built-in table: " + resourcelocation);
        }
        map.forEach((resourceLocation, lootTable) -> LootTableManager.validateLootTable(validationtracker, resourceLocation, lootTable));
    }

    private static class ModEntityLootTables extends EntityLootTables {
        @Override
        protected void addTables() {
            CompoundNBT splash = new CompoundNBT();
            splash.putBoolean("splash", true);

            LootTable.Builder advanced_hunter = LootTable.builder()
                    .addLootPool(LootPool.builder().name("general").acceptCondition(KilledByPlayer.builder()).rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_blood_bottle).weight(4))
                            .addEntry(ItemLootEntry.builder(ModItems.item_garlic).weight(4).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0, 1))))
                            .addEntry(ItemLootEntry.builder(ModItems.holy_water_bottle_enhanced).weight(3).acceptFunction(SetNBT.builder(splash)))
                            .addEntry(ItemLootEntry.builder(ModItems.holy_water_bottle_ultimate).weight(1).acceptFunction(SetNBT.builder(splash)))
                            .addEntry(ItemLootEntry.builder(ModItems.holy_salt).weight(4).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0, 1))).acceptFunction(SetCount.builder(RandomValueRange.of(1, 2)))))
                    .addLootPool(LootPool.builder().name("special").acceptCondition(RandomChanceWithLooting.builder(0.05f, 0.01f)).rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_book).weight(1).acceptFunction(AddBookNbt.builder())));
            this.registerLootTable(ModEntities.advanced_hunter, advanced_hunter);
            this.registerLootTable(ModEntities.advanced_hunter_imob, advanced_hunter);
            LootTable.Builder advanced_vampire = LootTable.builder()
                    .addLootPool(LootPool.builder().name("general").acceptCondition(KilledByPlayer.builder()).rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_blood_bottle).weight(1))
                            .addEntry(ItemLootEntry.builder(ModItems.blood_bottle).weight(1).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.5f, 1.0f))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(1f, 1f)))))
                    .addLootPool(LootPool.builder().name("special").acceptCondition(RandomChanceWithLooting.builder(0.05f, 0.01f)).rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_book).weight(1).acceptFunction(AddBookNbt.builder())));
            this.registerLootTable(ModEntities.advanced_vampire, advanced_vampire);
            this.registerLootTable(ModEntities.advanced_vampire_imob, advanced_vampire);
            this.registerLootTable(ModEntities.blinding_bat, LootTable.builder());
            this.registerLootTable(ModEntities.converted_creature, LootTable.builder());
            this.registerLootTable(ModEntities.converted_creature_imob, LootTable.builder());
            this.registerLootTable(ModEntities.converted_sheep, LootTable.builder());
            this.registerLootTable(ModEntities.converted_horse, LootTable.builder());
            this.registerLootTable(ModEntities.dummy_creature, LootTable.builder());
            this.registerLootTable(ModEntities.hunter_trainer, LootTable.builder());
            LootTable.Builder vampire = LootTable.builder()
                    .addLootPool(LootPool.builder().name("general").rolls(ConstantRange.of(1)).acceptCondition(KilledByPlayer.builder()).acceptCondition(RandomChanceWithLooting.builder(0.33f, 0.05f))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_fang).weight(1)))
                    .addLootPool(LootPool.builder().name("special").rolls(ConstantRange.of(1)).acceptCondition(StakeCondition.builder(LootContext.EntityTarget.KILLER_PLAYER)).acceptCondition(RandomChanceWithLooting.builder(0.5f, 0.05f))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_blood_bottle).weight(1)));
            this.registerLootTable(ModEntities.vampire, vampire);
            this.registerLootTable(ModEntities.vampire_imob, vampire);
            this.registerLootTable(ModEntities.vampire_baron, LootTable.builder()
                    .addLootPool(LootPool.builder().name("pure_blood_0").rolls(ConstantRange.of(1)).acceptCondition(KilledByPlayer.builder()).acceptCondition(AdjustableLevelCondition.builder(0, LootContext.EntityTarget.THIS))
                            .addEntry(ItemLootEntry.builder(ModItems.pure_blood_0).weight(1)))
                    .addLootPool(LootPool.builder().name("pure_blood_1").rolls(ConstantRange.of(1)).acceptCondition(KilledByPlayer.builder()).acceptCondition(AdjustableLevelCondition.builder(1, LootContext.EntityTarget.THIS))
                            .addEntry(ItemLootEntry.builder(ModItems.pure_blood_1).weight(1)))
                    .addLootPool(LootPool.builder().name("pure_blood_2").rolls(ConstantRange.of(1)).acceptCondition(KilledByPlayer.builder()).acceptCondition(AdjustableLevelCondition.builder(2, LootContext.EntityTarget.THIS))
                            .addEntry(ItemLootEntry.builder(ModItems.pure_blood_2).weight(1)))
                    .addLootPool(LootPool.builder().name("pure_blood_3").rolls(ConstantRange.of(1)).acceptCondition(KilledByPlayer.builder()).acceptCondition(AdjustableLevelCondition.builder(3, LootContext.EntityTarget.THIS))
                            .addEntry(ItemLootEntry.builder(ModItems.pure_blood_3).weight(1)))
                    .addLootPool(LootPool.builder().name("pure_blood_4").rolls(ConstantRange.of(1)).acceptCondition(KilledByPlayer.builder()).acceptCondition(AdjustableLevelCondition.builder(4, LootContext.EntityTarget.THIS))
                            .addEntry(ItemLootEntry.builder(ModItems.pure_blood_4).weight(1))));
            LootTable.Builder hunter = LootTable.builder()
                    .addLootPool(LootPool.builder().name("general").rolls(ConstantRange.of(1)).acceptCondition(KilledByPlayer.builder()).acceptCondition(RandomChanceWithLooting.builder(0.33f, 0.005f))
                            .addEntry(ItemLootEntry.builder(ModItems.human_heart).weight(1)))
                    .addLootPool(LootPool.builder().name("special").rolls(ConstantRange.of(1)).acceptCondition(KilledByPlayer.builder()).acceptCondition(RandomChanceWithLooting.builder(0.25f, 0.05f))
                            .addEntry(ItemLootEntry.builder(ModItems.holy_salt).weight(1)));
            this.registerLootTable(ModEntities.hunter, hunter);
            this.registerLootTable(ModEntities.hunter_imob, hunter);
            this.registerLootTable(ModEntities.villager_angry, LootTable.builder());
            this.registerLootTable(ModEntities.villager_converted, LootTable.builder());
            this.registerLootTable(ModEntities.task_master_vampire, LootTable.builder());
            this.registerLootTable(ModEntities.task_master_hunter, LootTable.builder());
            this.registerLootTable(ModEntities.vampire_minion,LootTable.builder());
            this.registerLootTable(ModEntities.hunter_minion,LootTable.builder());
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
            consumer.accept(ModLootTables.chest_hunter_trainer, LootTable.builder()
                    .addLootPool(LootPool.builder().name("main").rolls(RandomValueRange.of(5, 9))
                            .addEntry(ItemLootEntry.builder(Items.IRON_INGOT).weight(40))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_blood_bottle).weight(20))
                            .addEntry(ItemLootEntry.builder(ModItems.item_garlic).weight(40)))
                    .addLootPool(LootPool.builder().name("book").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_book).weight(50).acceptFunction(AddBookNbt.builder()))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(95)))
                    .addLootPool(LootPool.builder().name("hunter_weapons").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.hunter_axe_ultimate).weight(10).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(95)))
                    .addLootPool(LootPool.builder().name("swiftness_armor").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_head_ultimate).weight(10).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_chest_ultimate).weight(10).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_legs_ultimate).weight(10).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_feet_ultimate).weight(10).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(60)))
                    .addLootPool(LootPool.builder().name("hunter_coat").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.hunter_coat_head_ultimate).weight(10).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.hunter_coat_chest_ultimate).weight(10).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.hunter_coat_legs_ultimate).weight(10).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.hunter_coat_feet_ultimate).weight(10).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(60)))
                    .addLootPool(LootPool.builder().name("obsidian").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.obsidian_armor_head_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.obsidian_armor_chest_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.obsidian_armor_legs_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.obsidian_armor_feet_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(72)))
                    .addLootPool(LootPool.builder().name("holy_water").rolls(ConstantRange.of(5))
                            .addEntry(ItemLootEntry.builder(ModItems.holy_salt).weight(50))
                            .addEntry(ItemLootEntry.builder(ModItems.holy_water_bottle_normal).weight(20))
                            .addEntry(ItemLootEntry.builder(ModItems.holy_water_bottle_enhanced).weight(20))
                            .addEntry(ItemLootEntry.builder(ModItems.holy_water_bottle_ultimate).weight(10))));
            consumer.accept(ModLootTables.chest_vampire_dungeon, LootTable.builder()
                    .addLootPool(LootPool.builder().name("main").rolls(ConstantRange.of(7))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_fang).weight(35))
                            .addEntry(ItemLootEntry.builder(ModItems.blood_bottle).weight(20).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(1f, 1f)))))
                    .addLootPool(LootPool.builder().name("book").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_book).weight(70).acceptFunction(AddBookNbt.builder()))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(30)))
                    .addLootPool(LootPool.builder().name("equipment").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.heart_seeker_enhanced).weight(21).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.6f, 0.99f))).acceptFunction(SetItemBloodCharge.builder(RandomValueRange.of(500f, 2000f))))
                            .addEntry(ItemLootEntry.builder(ModItems.heart_seeker_ultimate).weight(9).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.6f, 0.99f))).acceptFunction(SetItemBloodCharge.builder(RandomValueRange.of(500f, 2000f))))
                            .addEntry(ItemLootEntry.builder(ModItems.heart_striker_enhanced).weight(21).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.6f, 0.99f))).acceptFunction(SetItemBloodCharge.builder(RandomValueRange.of(500f, 2000f))))
                            .addEntry(ItemLootEntry.builder(ModItems.heart_striker_ultimate).weight(9).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.6f, 0.99f))).acceptFunction(SetItemBloodCharge.builder(RandomValueRange.of(500f, 2000f))))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(40))));
        }
    }

    private static class ModBlockLootTables extends BlockLootTables {
        @Override
        protected void addTables() {
            this.registerDropSelfLootTable(ModBlocks.alchemical_cauldron);
            this.registerDropSelfLootTable(ModBlocks.altar_infusion);
            this.registerDropSelfLootTable(ModBlocks.altar_inspiration);
            this.registerLootTable(ModBlocks.altar_pillar, dropping(ModBlocks.altar_pillar)
                    .addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(SurvivesExplosion.builder())
                            .addEntry(ItemLootEntry.builder(Items.STONE_BRICKS).acceptCondition(BlockStateProperty.builder(ModBlocks.altar_pillar).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withStringProp(AltarPillarBlock.TYPE_PROPERTY, "stone"))))
                            .addEntry(ItemLootEntry.builder(Items.IRON_BLOCK).acceptCondition(BlockStateProperty.builder(ModBlocks.altar_pillar).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withStringProp(AltarPillarBlock.TYPE_PROPERTY, "iron"))))
                            .addEntry(ItemLootEntry.builder(Items.GOLD_BLOCK).acceptCondition(BlockStateProperty.builder(ModBlocks.altar_pillar).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withStringProp(AltarPillarBlock.TYPE_PROPERTY, "gold"))))
                            .addEntry(ItemLootEntry.builder(Items.BONE_BLOCK).acceptCondition(BlockStateProperty.builder(ModBlocks.altar_pillar).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withStringProp(AltarPillarBlock.TYPE_PROPERTY, "bone"))))));
            this.registerDropSelfLootTable(ModBlocks.altar_tip);
            this.registerLootTable(ModBlocks.coffin, block -> droppingWhen(block, CoffinBlock.PART, CoffinBlock.CoffinPart.HEAD));
            this.registerDropSelfLootTable(ModBlocks.blood_container);
            this.registerDropSelfLootTable(ModBlocks.blood_grinder);
            this.registerDropSelfLootTable(ModBlocks.blood_pedestal);
            this.registerDropSelfLootTable(ModBlocks.potion_table);
            this.registerDropSelfLootTable(ModBlocks.blood_sieve);
            this.registerDropSelfLootTable(ModBlocks.castle_block_dark_brick);
            this.registerDropSelfLootTable(ModBlocks.castle_block_dark_brick_bloody);
            this.registerDropSelfLootTable(ModBlocks.castle_block_dark_stone);
            this.registerDropSelfLootTable(ModBlocks.castle_block_normal_brick);
            this.registerDropSelfLootTable(ModBlocks.castle_block_purple_brick);
            this.registerDropSelfLootTable(ModBlocks.castle_slab_dark_brick);
            this.registerDropSelfLootTable(ModBlocks.castle_slab_dark_stone);
            this.registerDropSelfLootTable(ModBlocks.castle_slab_purple_brick);
            this.registerDropSelfLootTable(ModBlocks.castle_stairs_dark_brick);
            this.registerDropSelfLootTable(ModBlocks.castle_stairs_dark_stone);
            this.registerDropSelfLootTable(ModBlocks.castle_stairs_purple_brick);
            this.registerDropSelfLootTable(ModBlocks.church_altar);
            this.registerDropSelfLootTable(ModBlocks.cursed_earth);
            this.registerDropSelfLootTable(ModBlocks.fire_place);
            this.registerLootTable(ModBlocks.garlic, withExplosionDecay(ModBlocks.garlic, LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .addEntry(ItemLootEntry.builder(ModItems.item_garlic)))
                    .addLootPool(LootPool.builder()
                            .acceptCondition(BlockStateProperty.builder(ModBlocks.garlic).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(GarlicBlock.AGE, 7)))
                            .addEntry(ItemLootEntry.builder(ModItems.item_garlic).acceptFunction(ApplyBonus.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3))))));
            this.registerDropSelfLootTable(ModBlocks.garlic_beacon_weak);
            this.registerDropSelfLootTable(ModBlocks.garlic_beacon_normal);
            this.registerDropSelfLootTable(ModBlocks.garlic_beacon_improved);
            this.registerDropSelfLootTable(ModBlocks.hunter_table);
            this.registerLootTable(ModBlocks.med_chair, block -> droppingWhen(block, MedChairBlock.PART, MedChairBlock.EnumPart.TOP));
            this.registerDropSelfLootTable(ModBlocks.sunscreen_beacon);
            this.registerLootTable(ModBlocks.tent_main, dropping(ModItems.item_tent)
                    .addLootPool(LootPool.builder().name("bonus").rolls(ConstantRange.of(1)).acceptCondition(TentSpawnerCondition.builder())
                            .addEntry(ItemLootEntry.builder(Items.APPLE))
                            .addEntry(ItemLootEntry.builder(Items.BREAD))
                            .addEntry(ItemLootEntry.builder(Items.COAL))
                            .addEntry(ItemLootEntry.builder(Blocks.OAK_PLANKS))));
            this.registerDropSelfLootTable(ModBlocks.totem_base);
            this.registerDropSelfLootTable(ModBlocks.totem_top_crafted);
            this.registerLootTable(ModBlocks.totem_top_vampirism_vampire_crafted, dropping(ModBlocks.totem_top_crafted));
            this.registerLootTable(ModBlocks.totem_top_vampirism_hunter_crafted, dropping(ModBlocks.totem_top_crafted));
            this.registerLootTable(ModBlocks.totem_top, blockNoDrop());
            this.registerLootTable(ModBlocks.totem_top_vampirism_hunter, blockNoDrop());
            this.registerLootTable(ModBlocks.totem_top_vampirism_vampire, blockNoDrop());

            this.registerDropSelfLootTable(ModBlocks.vampire_orchid);
            this.registerDropSelfLootTable(ModBlocks.weapon_table);
            this.registerLootTable(ModBlocks.tent, blockNoDrop());
            this.registerFlowerPot(ModBlocks.potted_vampire_orchid);
            this.registerDropSelfLootTable(ModBlocks.bloody_spruce_sapling);
            this.registerDropSelfLootTable(ModBlocks.bloody_spruce_log);
            this.registerLootTable(ModBlocks.vampire_spruce_leaves, (block) -> droppingWithChancesAndSticks(block, ModBlocks.bloody_spruce_sapling, DEFAULT_SAPLING_DROP_RATES));
            this.registerLootTable(ModBlocks.bloody_spruce_leaves, (block) -> droppingWithChancesAndSticks(block, ModBlocks.bloody_spruce_sapling, DEFAULT_SAPLING_DROP_RATES));
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
            consumer.accept(ModLootTables.abandoned_mineshaft, LootTable.builder()
                    .addLootPool(LootPool.builder().name("main").rolls(RandomValueRange.of(0f, 4f))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_fang).weight(20))
                            .addEntry(ItemLootEntry.builder(ModItems.item_garlic).weight(20))
                            .addEntry(ItemLootEntry.builder(ModItems.blood_bottle).weight(15).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(1f, 1f))))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_book).weight(5).acceptFunction(AddBookNbt.builder()))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(40)))
                    .addLootPool(LootPool.builder().name("swiftness_armor").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_head_ultimate).weight(3).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_chest_ultimate).weight(3).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_legs_ultimate).weight(3).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_feet_ultimate).weight(3).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(88)))
                    .addLootPool(LootPool.builder().name("hunter_weapons").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.hunter_axe_ultimate).weight(10).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(95)))
                    .addLootPool(LootPool.builder().name("vampire_weapons").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.heart_seeker_enhanced).weight(20).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.heart_striker_enhanced).weight(20).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(60)))
                    .addLootPool(LootPool.builder().name("holy_Water").rolls(ConstantRange.of(2))
                            .addEntry(ItemLootEntry.builder(ModItems.holy_salt).weight(50))
                            .addEntry(ItemLootEntry.builder(ModItems.holy_water_bottle_normal).weight(20))
                            .addEntry(ItemLootEntry.builder(ModItems.holy_water_bottle_enhanced).weight(20))
                            .addEntry(ItemLootEntry.builder(ModItems.holy_water_bottle_ultimate).weight(10))));
            consumer.accept(ModLootTables.desert_pyramid, LootTable.builder()
                    .addLootPool(LootPool.builder().name("main").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.item_garlic).weight(15))
                            .addEntry(ItemLootEntry.builder(ModItems.blood_bottle).weight(20).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.6f, 0.6f))))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_book).weight(8).acceptFunction(AddBookNbt.builder()))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(60)))
                    .addLootPool(LootPool.builder().name("obsidian_armor").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.obsidian_armor_head_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.obsidian_armor_chest_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.obsidian_armor_legs_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.obsidian_armor_feet_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(72))));
            consumer.accept(ModLootTables.jungle_temple, LootTable.builder()
                    .addLootPool(LootPool.builder().name("main").rolls(ConstantRange.of(2))
                            .addEntry(ItemLootEntry.builder(ModItems.item_garlic).weight(20))
                            .addEntry(ItemLootEntry.builder(ModItems.blood_bottle).weight(20).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(1f, 1f))))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_book).weight(20).acceptFunction(AddBookNbt.builder()))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_fang).weight(20))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(30)))
                    .addLootPool(LootPool.builder().name("swiftness_armor").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_head_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_chest_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_legs_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_feet_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(72)))
                    .addLootPool(LootPool.builder().name("hunter_coat").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.hunter_coat_head_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.hunter_coat_chest_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.hunter_coat_legs_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.hunter_coat_feet_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(72))));
            consumer.accept(ModLootTables.stronghold_corridor, LootTable.builder()
                    .addLootPool(LootPool.builder().name("main").rolls(ConstantRange.of(2))
                            .addEntry(ItemLootEntry.builder(ModItems.item_garlic).weight(50))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_book).weight(20).acceptFunction(AddBookNbt.builder()))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(27)))
                    .addLootPool(LootPool.builder().name("swiftness_armor").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_head_ultimate).weight(5).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_chest_ultimate).weight(5).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_legs_ultimate).weight(5).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.armor_of_swiftness_feet_ultimate).weight(5).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(80)))
                    .addLootPool(LootPool.builder().name("obsidian_armor").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.obsidian_armor_head_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.obsidian_armor_chest_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.obsidian_armor_legs_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.obsidian_armor_feet_ultimate).weight(7).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(72)))
                    .addLootPool(LootPool.builder().name("vampire_weapons").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.heart_seeker_enhanced).weight(10).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(ItemLootEntry.builder(ModItems.heart_striker_enhanced).weight(10).acceptFunction(SetDamage.func_215931_a(RandomValueRange.of(0.3f, 0.9f))))
                            .addEntry(EmptyLootEntry.func_216167_a().weight(80)))
            );
            consumer.accept(ModLootTables.stronghold_library, LootTable.builder()
                    .addLootPool(LootPool.builder().name("main").rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ModItems.vampire_book).weight(1).acceptFunction(AddBookNbt.builder()))));
        }
    }

}
