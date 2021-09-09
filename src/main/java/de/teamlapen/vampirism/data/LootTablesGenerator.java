package de.teamlapen.vampirism.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.blocks.AltarPillarBlock;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.GarlicBlock;
import de.teamlapen.vampirism.blocks.MedChairBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModLootTables;
import de.teamlapen.vampirism.world.loot.*;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class LootTablesGenerator extends LootTableProvider {

    /**
     * copied from {@link BlockLoot}
     */
    public static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};


    public LootTablesGenerator(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Nonnull
    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {

        return ImmutableList.of(Pair.of(ModEntityLootTables::new, LootContextParamSets.ENTITY), Pair.of(ModChestLootTables::new, LootContextParamSets.CHEST), Pair.of(ModBlockLootTables::new, LootContextParamSets.BLOCK), Pair.of(InjectLootTables::new, LootContextParamSets.CHEST));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        for (ResourceLocation resourcelocation : Sets.difference(ModLootTables.getLootTables(), map.keySet())) {
            validationtracker.reportProblem("Missing built-in table: " + resourcelocation);
        }
        map.forEach((resourceLocation, lootTable) -> LootTables.validate(validationtracker, resourceLocation, lootTable));
    }

    private static class ModEntityLootTables extends EntityLoot {
        @Override
        protected void addTables() {
            CompoundTag splash = new CompoundTag();
            splash.putBoolean("splash", true);

            LootTable.Builder advanced_hunter = LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("general").when(LootItemKilledByPlayerCondition.killedByPlayer()).setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.vampire_blood_bottle).setWeight(4))
                            .add(LootItem.lootTableItem(ModItems.item_garlic).setWeight(4).apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1))))
                            .add(LootItem.lootTableItem(ModItems.holy_water_bottle_enhanced).setWeight(3).apply(SetNbtFunction.setTag(splash)))
                            .add(LootItem.lootTableItem(ModItems.holy_water_bottle_ultimate).setWeight(1).apply(SetNbtFunction.setTag(splash)))
                            .add(LootItem.lootTableItem(ModItems.holy_salt).setWeight(4).apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1))).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))))
                    .withPool(LootPool.lootPool().name("special").when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.05f, 0.01f)).setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.vampire_book).setWeight(1).apply(AddBookNbt.builder())));
            this.add(ModEntities.advanced_hunter, advanced_hunter);
            this.add(ModEntities.advanced_hunter_imob, advanced_hunter);
            LootTable.Builder advanced_vampire = LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("general").when(LootItemKilledByPlayerCondition.killedByPlayer()).setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.vampire_blood_bottle).setWeight(1))
                            .add(LootItem.lootTableItem(ModItems.blood_bottle).setWeight(1).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.5f, 1.0f))).apply(LootingEnchantFunction.lootingMultiplier(ConstantValue.exactly(1f)))))
                    .withPool(LootPool.lootPool().name("special").when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.1f, 0.01f)).setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.vampire_book).setWeight(1).apply(AddBookNbt.builder())))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.05f, 0.01f))
                            .add(LootItem.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))));
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
                    .withPool(LootPool.lootPool().name("general").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.33f, 0.05f))
                            .add(LootItem.lootTableItem(ModItems.vampire_fang).setWeight(1)))
                    .withPool(LootPool.lootPool().name("special").setRolls(ConstantValue.exactly(1)).when(StakeCondition.builder(LootContext.EntityTarget.KILLER_PLAYER)).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.5f, 0.05f))
                            .add(LootItem.lootTableItem(ModItems.vampire_blood_bottle).setWeight(1)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.02f, 0.01f))
                            .add(LootItem.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))));
            this.add(ModEntities.vampire, vampire);
            this.add(ModEntities.vampire_imob, vampire);
            this.add(ModEntities.vampire_baron, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("pure_blood_0").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(0, LootContext.EntityTarget.THIS))
                            .add(LootItem.lootTableItem(ModItems.pure_blood_0).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_1").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(1, LootContext.EntityTarget.THIS))
                            .add(LootItem.lootTableItem(ModItems.pure_blood_1).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_2").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(2, LootContext.EntityTarget.THIS))
                            .add(LootItem.lootTableItem(ModItems.pure_blood_2).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_3").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(3, LootContext.EntityTarget.THIS))
                            .add(LootItem.lootTableItem(ModItems.pure_blood_3).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_4").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(4, LootContext.EntityTarget.THIS))
                            .add(LootItem.lootTableItem(ModItems.pure_blood_4).setWeight(1))));
            LootTable.Builder hunter = LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("general").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.33f, 0.005f))
                            .add(LootItem.lootTableItem(ModItems.human_heart).setWeight(1)))
                    .withPool(LootPool.lootPool().name("special").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.25f, 0.05f))
                            .add(LootItem.lootTableItem(ModItems.holy_salt).setWeight(1)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.05f, 0.02f))
                            .add(LootItem.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))));
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
                    .withPool(LootPool.lootPool().name("main").setRolls(UniformGenerator.between(5, 9))
                            .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(40))
                            .add(LootItem.lootTableItem(ModItems.vampire_blood_bottle).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.item_garlic).setWeight(40)))
                    .withPool(LootPool.lootPool().name("book").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.vampire_book).setWeight(50).apply(AddBookNbt.builder()))
                            .add(EmptyLootItem.emptyItem().setWeight(95)))
                    .withPool(LootPool.lootPool().name("hunter_weapons").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.hunter_axe_ultimate).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(95)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_head_ultimate).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_chest_ultimate).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_legs_ultimate).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_feet_ultimate).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("hunter_coat").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.hunter_coat_head_ultimate).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.hunter_coat_chest_ultimate).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.hunter_coat_legs_ultimate).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.hunter_coat_feet_ultimate).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("obsidian").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.obsidian_armor_head_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.obsidian_armor_chest_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.obsidian_armor_legs_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.obsidian_armor_feet_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(72)))
                    .withPool(LootPool.lootPool().name("holy_water").setRolls(ConstantValue.exactly(5))
                            .add(LootItem.lootTableItem(ModItems.holy_salt).setWeight(50))
                            .add(LootItem.lootTableItem(ModItems.holy_water_bottle_normal).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.holy_water_bottle_enhanced).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.holy_water_bottle_ultimate).setWeight(10))));
            consumer.accept(ModLootTables.chest_vampire_dungeon, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(7))
                            .add(LootItem.lootTableItem(ModItems.vampire_fang).setWeight(35))
                            .add(LootItem.lootTableItem(ModItems.blood_bottle).setWeight(20).apply(SetItemDamageFunction.setDamage(ConstantValue.exactly(1f)))))
                    .withPool(LootPool.lootPool().name("book").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.vampire_book).setWeight(70).apply(AddBookNbt.builder()))
                            .add(EmptyLootItem.emptyItem().setWeight(30)))
                    .withPool(LootPool.lootPool().name("equipment").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.heart_seeker_enhanced).setWeight(21).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(UniformGenerator.between(500f, 2000f))))
                            .add(LootItem.lootTableItem(ModItems.heart_seeker_ultimate).setWeight(9).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(UniformGenerator.between(500f, 2000f))))
                            .add(LootItem.lootTableItem(ModItems.heart_striker_enhanced).setWeight(21).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(UniformGenerator.between(500f, 2000f))))
                            .add(LootItem.lootTableItem(ModItems.heart_striker_ultimate).setWeight(9).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(UniformGenerator.between(500f, 2000f))))
                            .add(EmptyLootItem.emptyItem().setWeight(40)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(3))
                            .add(LootItem.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))));
        }
    }

    private static class ModBlockLootTables extends BlockLoot {
        @Override
        protected void addTables() {
            this.dropSelf(ModBlocks.alchemical_cauldron);
            this.dropSelf(ModBlocks.altar_infusion);
            this.dropSelf(ModBlocks.altar_inspiration);
            this.add(ModBlocks.altar_pillar, createSingleItemTable(ModBlocks.altar_pillar)
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(ExplosionCondition.survivesExplosion())
                            .add(LootItem.lootTableItem(Items.STONE_BRICKS).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.altar_pillar).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "stone"))))
                            .add(LootItem.lootTableItem(Items.IRON_BLOCK).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.altar_pillar).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "iron"))))
                            .add(LootItem.lootTableItem(Items.GOLD_BLOCK).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.altar_pillar).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "gold"))))
                            .add(LootItem.lootTableItem(Items.BONE_BLOCK).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.altar_pillar).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "bone"))))));
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
            this.dropSelf(ModBlocks.church_altar);
            this.dropSelf(ModBlocks.cursed_earth);
            this.dropSelf(ModBlocks.fire_place);
            this.add(ModBlocks.garlic, applyExplosionDecay(ModBlocks.garlic, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(LootItem.lootTableItem(ModItems.item_garlic)))
                    .withPool(LootPool.lootPool()
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.garlic).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(GarlicBlock.AGE, 7)))
                            .add(LootItem.lootTableItem(ModItems.item_garlic).apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))));
            this.dropSelf(ModBlocks.garlic_diffusor_weak);
            this.dropSelf(ModBlocks.garlic_diffusor_normal);
            this.dropSelf(ModBlocks.garlic_diffusor_improved);
            this.dropSelf(ModBlocks.hunter_table);
            this.add(ModBlocks.med_chair, block -> createSinglePropConditionTable(block, MedChairBlock.PART, MedChairBlock.EnumPart.TOP));
            this.dropSelf(ModBlocks.sunscreen_beacon);
            this.add(ModBlocks.tent_main, createSingleItemTable(ModItems.item_tent)
                    .withPool(LootPool.lootPool().name("bonus").setRolls(ConstantValue.exactly(1)).when(TentSpawnerCondition.builder())
                            .add(LootItem.lootTableItem(Items.APPLE))
                            .add(LootItem.lootTableItem(Items.BREAD))
                            .add(LootItem.lootTableItem(Items.COAL))
                            .add(LootItem.lootTableItem(Blocks.OAK_PLANKS))));
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
            this.dropSelf(ModBlocks.bloody_spruce_sapling);
            this.dropSelf(ModBlocks.bloody_spruce_log);
            this.add(ModBlocks.vampire_spruce_leaves, (block) -> createLeavesDrops(block, ModBlocks.bloody_spruce_sapling, DEFAULT_SAPLING_DROP_RATES));
            this.add(ModBlocks.bloody_spruce_leaves, (block) -> createLeavesDrops(block, ModBlocks.bloody_spruce_sapling, DEFAULT_SAPLING_DROP_RATES));
            this.dropSelf(ModBlocks.chandelier);
            this.add(ModBlocks.candelabra_wall, createSingleItemTable(ModItems.item_candelabra));
            this.add(ModBlocks.candelabra, createSingleItemTable(ModItems.item_candelabra));
            this.dropSelf(ModBlocks.cross);
            this.dropSelf(ModBlocks.tombstone1);
            this.dropSelf(ModBlocks.tombstone2);
            this.dropSelf(ModBlocks.tombstone3);
            this.dropSelf(ModBlocks.grave_cage);
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
                    .withPool(LootPool.lootPool().name("main").setRolls(UniformGenerator.between(0f, 4f))
                            .add(LootItem.lootTableItem(ModItems.vampire_fang).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.item_garlic).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.blood_bottle).setWeight(15).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(1f, 1f))))
                            .add(LootItem.lootTableItem(ModItems.vampire_book).setWeight(5).apply(AddBookNbt.builder()))
                            .add(EmptyLootItem.emptyItem().setWeight(40)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_head_ultimate).setWeight(3).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_chest_ultimate).setWeight(3).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_legs_ultimate).setWeight(3).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_feet_ultimate).setWeight(3).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(88)))
                    .withPool(LootPool.lootPool().name("hunter_weapons").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.hunter_axe_ultimate).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(95)))
                    .withPool(LootPool.lootPool().name("vampire_weapons").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.heart_seeker_enhanced).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.heart_striker_enhanced).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("holy_Water").setRolls(ConstantValue.exactly(2))
                            .add(LootItem.lootTableItem(ModItems.holy_salt).setWeight(50))
                            .add(LootItem.lootTableItem(ModItems.holy_water_bottle_normal).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.holy_water_bottle_enhanced).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.holy_water_bottle_ultimate).setWeight(10)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.desert_pyramid, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.item_garlic).setWeight(15))
                            .add(LootItem.lootTableItem(ModItems.blood_bottle).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.6f))))
                            .add(LootItem.lootTableItem(ModItems.vampire_book).setWeight(8).apply(AddBookNbt.builder()))
                            .add(EmptyLootItem.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("obsidian_armor").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.obsidian_armor_head_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.obsidian_armor_chest_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.obsidian_armor_legs_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.obsidian_armor_feet_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(72)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.jungle_temple, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(2))
                            .add(LootItem.lootTableItem(ModItems.item_garlic).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.blood_bottle).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(1f, 1f))))
                            .add(LootItem.lootTableItem(ModItems.vampire_book).setWeight(20).apply(AddBookNbt.builder()))
                            .add(LootItem.lootTableItem(ModItems.vampire_fang).setWeight(20))
                            .add(EmptyLootItem.emptyItem().setWeight(30)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_head_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_chest_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_legs_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_feet_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(72)))
                    .withPool(LootPool.lootPool().name("hunter_coat").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.hunter_coat_head_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.hunter_coat_chest_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.hunter_coat_legs_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.hunter_coat_feet_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(72)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.stronghold_corridor, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(2))
                            .add(LootItem.lootTableItem(ModItems.item_garlic).setWeight(50))
                            .add(LootItem.lootTableItem(ModItems.vampire_book).setWeight(20).apply(AddBookNbt.builder()))
                            .add(EmptyLootItem.emptyItem().setWeight(27)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_head_ultimate).setWeight(5).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_chest_ultimate).setWeight(5).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_legs_ultimate).setWeight(5).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.armor_of_swiftness_feet_ultimate).setWeight(5).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(80)))
                    .withPool(LootPool.lootPool().name("obsidian_armor").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.obsidian_armor_head_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.obsidian_armor_chest_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.obsidian_armor_legs_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.obsidian_armor_feet_ultimate).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(72)))
                    .withPool(LootPool.lootPool().name("vampire_weapons").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.heart_seeker_enhanced).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.heart_striker_enhanced).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(80)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(3))
                            .add(LootItem.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.stronghold_library, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.vampire_book).setWeight(1).apply(AddBookNbt.builder())))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(3))
                            .add(LootItem.lootTableItem(ModItems.amulet).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.ring).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.obi_belt).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
        }
    }

}
