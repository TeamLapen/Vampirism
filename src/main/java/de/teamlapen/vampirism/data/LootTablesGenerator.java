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
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
    protected void validate(Map<ResourceLocation, LootTable> map, @Nonnull ValidationContext validationtracker) {
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
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setWeight(4))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(4).apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1))))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get()).setWeight(3).apply(SetNbtFunction.setTag(splash)))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get()).setWeight(1).apply(SetNbtFunction.setTag(splash)))
                            .add(LootItem.lootTableItem(ModItems.HOLY_SALT.get()).setWeight(4).apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1))).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))))
                    .withPool(LootPool.lootPool().name("special").when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.05f, 0.01f)).setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(1).apply(AddBookNbt.builder())));
            this.add(ModEntities.ADVANCED_HUNTER.get(), advanced_hunter);
            this.add(ModEntities.ADVANCED_HUNTER_IMOB.get(), advanced_hunter);
            LootTable.Builder advanced_vampire = LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("general").when(LootItemKilledByPlayerCondition.killedByPlayer()).setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setWeight(1))
                            .add(LootItem.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(1).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.5f, 1.0f))).apply(LootingEnchantFunction.lootingMultiplier(ConstantValue.exactly(1f)))))
                    .withPool(LootPool.lootPool().name("special").when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.1f, 0.01f)).setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(1).apply(AddBookNbt.builder())))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.05f, 0.01f))
                            .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))));
            this.add(ModEntities.ADVANCED_VAMPIRE.get(), advanced_vampire);
            this.add(ModEntities.ADVANCED_VAMPIRE_IMOB.get(), advanced_vampire);
            this.add(ModEntities.BLINDING_BAT.get(), LootTable.lootTable());
            this.add(ModEntities.CONVERTED_CREATURE.get(), LootTable.lootTable());
            this.add(ModEntities.CONVERTED_CREATURE_IMOB.get(), LootTable.lootTable());
            this.add(ModEntities.CONVERTED_SHEEP.get(), LootTable.lootTable());
            this.add(ModEntities.CONVERTED_COW.get(), LootTable.lootTable());
            this.add(ModEntities.CONVERTED_HORSE.get(), LootTable.lootTable());
            this.add(ModEntities.CONVERTED_DONKEY.get(), LootTable.lootTable());
            this.add(ModEntities.CONVERTED_MULE.get(), LootTable.lootTable());
            this.add(ModEntities.DUMMY_CREATURE.get(), LootTable.lootTable());
            this.add(ModEntities.HUNTER_TRAINER.get(), LootTable.lootTable());
            LootTable.Builder vampire = LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("general").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.33f, 0.05f))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("special").setRolls(ConstantValue.exactly(1)).when(StakeCondition.builder(LootContext.EntityTarget.KILLER_PLAYER)).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.5f, 0.05f))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.02f, 0.01f))
                            .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))));
            this.add(ModEntities.VAMPIRE.get(), vampire);
            this.add(ModEntities.VAMPIRE_IMOB.get(), vampire);
            this.add(ModEntities.VAMPIRE_BARON.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("pure_blood_0").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(0, LootContext.EntityTarget.THIS))
                            .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_0.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_1").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(1, LootContext.EntityTarget.THIS))
                            .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_1.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_2").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(2, LootContext.EntityTarget.THIS))
                            .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_2.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_3").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(3, LootContext.EntityTarget.THIS))
                            .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_3.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_4").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(4, LootContext.EntityTarget.THIS))
                            .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_4.get()).setWeight(1))));
            LootTable.Builder hunter = LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("general").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.33f, 0.005f))
                            .add(LootItem.lootTableItem(ModItems.HUMAN_HEART.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("special").setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.25f, 0.05f))
                            .add(LootItem.lootTableItem(ModItems.HOLY_SALT.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.05f, 0.02f))
                            .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))));
            this.add(ModEntities.HUNTER.get(), hunter);
            this.add(ModEntities.HUNTER_IMOB.get(), hunter);
            this.add(ModEntities.VILLAGER_ANGRY.get(), LootTable.lootTable());
            this.add(ModEntities.VILLAGER_CONVERTED.get(), LootTable.lootTable());
            this.add(ModEntities.TASK_MASTER_VAMPIRE.get(), LootTable.lootTable());
            this.add(ModEntities.TASK_MASTER_HUNTER.get(), LootTable.lootTable());
            this.add(ModEntities.VAMPIRE_MINION.get(), LootTable.lootTable());
            this.add(ModEntities.HUNTER_MINION.get(), LootTable.lootTable());
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
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(40)))
                    .withPool(LootPool.lootPool().name("book").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(50).apply(AddBookNbt.builder()))
                            .add(EmptyLootItem.emptyItem().setWeight(95)))
                    .withPool(LootPool.lootPool().name("hunter_weapons").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_AXE_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(95)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("hunter_coat").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_HEAD_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_CHEST_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_LEGS_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_FEET_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("holy_water").setRolls(ConstantValue.exactly(5))
                            .add(LootItem.lootTableItem(ModItems.HOLY_SALT.get()).setWeight(50))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_BOTTLE_NORMAL.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get()).setWeight(10))));
            consumer.accept(ModLootTables.chest_vampire_dungeon, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(7))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(35))
                            .add(LootItem.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(20).apply(SetItemDamageFunction.setDamage(ConstantValue.exactly(1f)))))
                    .withPool(LootPool.lootPool().name("book").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(70).apply(AddBookNbt.builder()))
                            .add(EmptyLootItem.emptyItem().setWeight(30)))
                    .withPool(LootPool.lootPool().name("equipment").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HEART_SEEKER_ENHANCED.get()).setWeight(21).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(UniformGenerator.between(500f, 2000f))))
                            .add(LootItem.lootTableItem(ModItems.HEART_SEEKER_ULTIMATE.get()).setWeight(9).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(UniformGenerator.between(500f, 2000f))))
                            .add(LootItem.lootTableItem(ModItems.HEART_STRIKER_ENHANCED.get()).setWeight(21).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(UniformGenerator.between(500f, 2000f))))
                            .add(LootItem.lootTableItem(ModItems.HEART_STRIKER_ULTIMATE.get()).setWeight(9).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(UniformGenerator.between(500f, 2000f))))
                            .add(EmptyLootItem.emptyItem().setWeight(40)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(3))
                            .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))));
        }
    }

    private static class ModBlockLootTables extends BlockLoot {
        @Override
        protected void addTables() {
            this.dropSelf(ModBlocks.ALCHEMICAL_CAULDRON.get());
            this.dropSelf(ModBlocks.ALTAR_INFUSION.get());
            this.dropSelf(ModBlocks.ALTAR_INSPIRATION.get());
            this.add(ModBlocks.ALTAR_PILLAR.get(), createSingleItemTable(ModBlocks.ALTAR_PILLAR.get())
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(ExplosionCondition.survivesExplosion())
                            .add(LootItem.lootTableItem(Items.STONE_BRICKS).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.ALTAR_PILLAR.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "stone"))))
                            .add(LootItem.lootTableItem(Items.IRON_BLOCK).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.ALTAR_PILLAR.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "iron"))))
                            .add(LootItem.lootTableItem(Items.GOLD_BLOCK).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.ALTAR_PILLAR.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "gold"))))
                            .add(LootItem.lootTableItem(Items.BONE_BLOCK).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.ALTAR_PILLAR.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "bone"))))));
            this.dropSelf(ModBlocks.ALTAR_TIP.get());
            this.add(ModBlocks.COFFIN.get(), block -> createSinglePropConditionTable(block, CoffinBlock.PART, CoffinBlock.CoffinPart.HEAD));
            this.dropSelf(ModBlocks.BLOOD_CONTAINER.get());
            this.dropSelf(ModBlocks.BLOOD_GRINDER.get());
            this.dropSelf(ModBlocks.BLOOD_PEDESTAL.get());
            this.dropSelf(ModBlocks.POTION_TABLE.get());
            this.dropSelf(ModBlocks.BLOOD_SIEVE.get());
            this.dropSelf(ModBlocks.CASTLE_BLOCK_DARK_BRICK.get());
            this.dropSelf(ModBlocks.CASTLE_BLOCK_DARK_BRICK_BLOODY.get());
            this.dropSelf(ModBlocks.CASTLE_BLOCK_DARK_STONE.get());
            this.dropSelf(ModBlocks.CASTLE_BLOCK_NORMAL_BRICK.get());
            this.dropSelf(ModBlocks.CASTLE_BLOCK_PURPLE_BRICK.get());
            this.dropSelf(ModBlocks.CASTLE_SLAB_DARK_BRICK.get());
            this.dropSelf(ModBlocks.CASTLE_SLAB_DARK_STONE.get());
            this.dropSelf(ModBlocks.CASTLE_SLAB_PURPLE_BRICK.get());
            this.dropSelf(ModBlocks.CASTLE_STAIRS_DARK_BRICK.get());
            this.dropSelf(ModBlocks.CASTLE_STAIRS_DARK_STONE.get());
            this.dropSelf(ModBlocks.CASTLE_STAIRS_PURPLE_BRICK.get());
            this.dropSelf(ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get());
            this.dropSelf(ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get());
            this.dropSelf(ModBlocks.DARK_SPRUCE_PLANKS.get());
            this.dropSelf(ModBlocks.CURSED_SPRUCE_PLANKS.get());
            this.dropSelf(ModBlocks.DARK_SPRUCE_TRAPDOOR.get());
            this.dropSelf(ModBlocks.CURSED_SPRUCE_TRAPDOOR.get());
            this.add(ModBlocks.DARK_SPRUCE_DOOR.get(), BlockLoot::createDoorTable);
            this.add(ModBlocks.CURSED_SPRUCE_DOOR.get(), BlockLoot::createDoorTable);
            this.dropSelf(ModBlocks.ALTAR_CLEANSING.get());
            this.dropSelf(ModBlocks.CURSED_EARTH.get());
            this.dropSelf(ModBlocks.FIRE_PLACE.get());
            this.add(ModBlocks.GARLIC.get(), applyExplosionDecay(ModBlocks.GARLIC.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get())))
                    .withPool(LootPool.lootPool()
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.GARLIC.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(GarlicBlock.AGE, 7)))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))));
            this.dropSelf(ModBlocks.GARLIC_DIFFUSER_WEAK.get());
            this.dropSelf(ModBlocks.GARLIC_DIFFUSER_NORMAL.get());
            this.dropSelf(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get());
            this.dropSelf(ModBlocks.HUNTER_TABLE.get());
            this.add(ModBlocks.MED_CHAIR.get(), block ->    createSinglePropConditionTable(block, MedChairBlock.PART, MedChairBlock.EnumPart.BOTTOM));
            this.dropSelf(ModBlocks.SUNSCREEN_BEACON.get());
            this.add(ModBlocks.TENT_MAIN.get(), createSingleItemTable(ModItems.ITEM_TENT.get())
                    .withPool(LootPool.lootPool().name("bonus").setRolls(ConstantValue.exactly(1)).when(TentSpawnerCondition.builder())
                            .add(LootItem.lootTableItem(Items.APPLE))
                            .add(LootItem.lootTableItem(Items.BREAD))
                            .add(LootItem.lootTableItem(Items.COAL))
                            .add(LootItem.lootTableItem(Blocks.OAK_PLANKS))));
            this.dropSelf(ModBlocks.TOTEM_BASE.get());
            this.dropSelf(ModBlocks.TOTEM_TOP_CRAFTED.get());
            this.add(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED.get(), createSingleItemTable(ModBlocks.TOTEM_TOP_CRAFTED.get()));
            this.add(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED.get(), createSingleItemTable(ModBlocks.TOTEM_TOP_CRAFTED.get()));
            this.add(ModBlocks.TOTEM_TOP.get(), noDrop());
            this.add(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get(), noDrop());
            this.add(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get(), noDrop());

            this.dropSelf(ModBlocks.VAMPIRE_ORCHID.get());
            this.dropSelf(ModBlocks.WEAPON_TABLE.get());
            this.add(ModBlocks.TENT.get(), noDrop());
            this.dropPottedContents(ModBlocks.POTTED_VAMPIRE_ORCHID.get());
            this.dropSelf(ModBlocks.DARK_SPRUCE_SAPLING.get());
            this.dropSelf(ModBlocks.CURSED_SPRUCE_SAPLING.get());
            this.add(ModBlocks.DARK_SPRUCE_LEAVES.get(), (block) -> createLeavesDrops(block, ModBlocks.DARK_SPRUCE_SAPLING.get(), DEFAULT_SAPLING_DROP_RATES));
            this.dropSelf(ModBlocks.CHANDELIER.get());
            this.add(ModBlocks.CANDELABRA_WALL.get(), createSingleItemTable(ModItems.ITEM_CANDELABRA.get()));
            this.add(ModBlocks.CANDELABRA.get(), createSingleItemTable(ModItems.ITEM_CANDELABRA.get()));
            this.dropSelf(ModBlocks.CROSS.get());
            this.dropSelf(ModBlocks.TOMBSTONE1.get());
            this.dropSelf(ModBlocks.TOMBSTONE2.get());
            this.dropSelf(ModBlocks.TOMBSTONE3.get());
            this.dropSelf(ModBlocks.GRAVE_CAGE.get());
            this.add(ModBlocks.CURSED_GRASS.get(), createSingleItemTable(ModBlocks.CURSED_EARTH.get()));
            this.dropSelf(ModBlocks.DARK_SPRUCE_LOG.get());
            this.dropPottedContents(ModBlocks.POTTED_CURSED_ROOTS.get());
            this.dropSelf(ModBlocks.CURSED_SPRUCE_LOG.get());
            this.add(ModBlocks.CURSED_BARK.get(), noDrop());
            this.dropSelf(ModBlocks.DARK_SPRUCE_STAIRS.get());
            this.dropSelf(ModBlocks.CURSED_SPRUCE_STAIRS.get());
            this.dropSelf(ModBlocks.DARK_SPRUCE_WOOD.get());
            this.dropSelf(ModBlocks.CURSED_SPRUCE_WOOD.get());
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
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(15).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(1f, 1f))))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(5).apply(AddBookNbt.builder()))
                            .add(EmptyLootItem.emptyItem().setWeight(40)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get()).setWeight(3).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get()).setWeight(3).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get()).setWeight(3).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()).setWeight(3).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(88)))
                    .withPool(LootPool.lootPool().name("hunter_weapons").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_AXE_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(95)))
                    .withPool(LootPool.lootPool().name("vampire_weapons").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HEART_SEEKER_ENHANCED.get()).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HEART_STRIKER_ENHANCED.get()).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("holy_Water").setRolls(ConstantValue.exactly(2))
                            .add(LootItem.lootTableItem(ModItems.HOLY_SALT.get()).setWeight(50))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_BOTTLE_NORMAL.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get()).setWeight(10)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.desert_pyramid, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(15))
                            .add(LootItem.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.6f))))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(8).apply(AddBookNbt.builder()))
                            .add(EmptyLootItem.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.jungle_temple, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(2))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(1f, 1f))))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(20).apply(AddBookNbt.builder()))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(20))
                            .add(EmptyLootItem.emptyItem().setWeight(30)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(72)))
                    .withPool(LootPool.lootPool().name("hunter_coat").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_HEAD_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_CHEST_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_LEGS_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_FEET_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(72)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.stronghold_corridor, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(2))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(50))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(20).apply(AddBookNbt.builder()))
                            .add(EmptyLootItem.emptyItem().setWeight(27)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get()).setWeight(5).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get()).setWeight(5).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get()).setWeight(5).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()).setWeight(5).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(80)))
                    .withPool(LootPool.lootPool().name("vampire_weapons").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HEART_SEEKER_ENHANCED.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HEART_STRIKER_ENHANCED.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(80)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(3))
                            .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.stronghold_library, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(1).apply(AddBookNbt.builder())))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantValue.exactly(3))
                            .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
        }
    }

}
