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
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setWeight(4))
                            .add(ItemLootEntry.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(4).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0, 1))))
                            .add(ItemLootEntry.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get()).setWeight(3).apply(SetNBT.setTag(splash)))
                            .add(ItemLootEntry.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get()).setWeight(1).apply(SetNBT.setTag(splash)))
                            .add(ItemLootEntry.lootTableItem(ModItems.HOLY_SALT.get()).setWeight(4).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0, 1))).apply(SetCount.setCount(RandomValueRange.between(1, 2)))))
                    .withPool(LootPool.lootPool().name("special").when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.05f, 0.01f)).setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(1).apply(AddBookNbt.builder())));
            this.add(ModEntities.ADVANCED_HUNTER.get(), advanced_hunter);
            this.add(ModEntities.ADVANCED_HUNTER_IMOB.get(), advanced_hunter);
            LootTable.Builder advanced_vampire = LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("general").when(KilledByPlayer.killedByPlayer()).setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setWeight(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(1).apply(SetDamage.setDamage(RandomValueRange.between(0.5f, 1.0f))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(1f, 1f)))))
                    .withPool(LootPool.lootPool().name("special").when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.1f, 0.01f)).setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(1).apply(AddBookNbt.builder())))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(1)).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.05f, 0.01f))
                            .add(ItemLootEntry.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))));
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
                    .withPool(LootPool.lootPool().name("general").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.33f, 0.05f))
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("special").setRolls(ConstantRange.exactly(1)).when(StakeCondition.builder(LootContext.EntityTarget.KILLER_PLAYER)).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.5f, 0.05f))
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(1)).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.02f, 0.01f))
                            .add(ItemLootEntry.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))));
            this.add(ModEntities.VAMPIRE.get(), vampire);
            this.add(ModEntities.VAMPIRE_IMOB.get(), vampire);
            this.add(ModEntities.VAMPIRE_BARON.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("pure_blood_0").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(AdjustableLevelCondition.builder(0, LootContext.EntityTarget.THIS))
                            .add(ItemLootEntry.lootTableItem(ModItems.PURE_BLOOD_0.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_1").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(AdjustableLevelCondition.builder(1, LootContext.EntityTarget.THIS))
                            .add(ItemLootEntry.lootTableItem(ModItems.PURE_BLOOD_1.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_2").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(AdjustableLevelCondition.builder(2, LootContext.EntityTarget.THIS))
                            .add(ItemLootEntry.lootTableItem(ModItems.PURE_BLOOD_2.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_3").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(AdjustableLevelCondition.builder(3, LootContext.EntityTarget.THIS))
                            .add(ItemLootEntry.lootTableItem(ModItems.PURE_BLOOD_3.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("pure_blood_4").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(AdjustableLevelCondition.builder(4, LootContext.EntityTarget.THIS))
                            .add(ItemLootEntry.lootTableItem(ModItems.PURE_BLOOD_4.get()).setWeight(1))));
            LootTable.Builder hunter = LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("general").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.33f, 0.005f))
                            .add(ItemLootEntry.lootTableItem(ModItems.HUMAN_HEART.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("special").setRolls(ConstantRange.exactly(1)).when(KilledByPlayer.killedByPlayer()).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.25f, 0.05f))
                            .add(ItemLootEntry.lootTableItem(ModItems.HOLY_SALT.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(1)).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.05f, 0.02f))
                            .add(ItemLootEntry.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))));
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
                    .withPool(LootPool.lootPool().name("main").setRolls(RandomValueRange.between(5, 9))
                            .add(ItemLootEntry.lootTableItem(Items.IRON_INGOT).setWeight(40))
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(40)))
                    .withPool(LootPool.lootPool().name("book").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(50).apply(AddBookNbt.builder()))
                            .add(EmptyLootEntry.emptyItem().setWeight(95)))
                    .withPool(LootPool.lootPool().name("hunter_weapons").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.HUNTER_AXE_ULTIMATE.get()).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(95)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get()).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get()).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get()).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("hunter_coat").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.HUNTER_COAT_HEAD_ULTIMATE.get()).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.HUNTER_COAT_CHEST_ULTIMATE.get()).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.HUNTER_COAT_LEGS_ULTIMATE.get()).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.HUNTER_COAT_FEET_ULTIMATE.get()).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("holy_water").setRolls(ConstantRange.exactly(5))
                            .add(ItemLootEntry.lootTableItem(ModItems.HOLY_SALT.get()).setWeight(50))
                            .add(ItemLootEntry.lootTableItem(ModItems.HOLY_WATER_BOTTLE_NORMAL.get()).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get()).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get()).setWeight(10))));
            consumer.accept(ModLootTables.chest_vampire_dungeon, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(7))
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(35))
                            .add(ItemLootEntry.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(20).apply(SetDamage.setDamage(RandomValueRange.between(1f, 1f)))))
                    .withPool(LootPool.lootPool().name("book").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(70).apply(AddBookNbt.builder()))
                            .add(EmptyLootEntry.emptyItem().setWeight(30)))
                    .withPool(LootPool.lootPool().name("equipment").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.HEART_SEEKER_ENHANCED.get()).setWeight(21).apply(SetDamage.setDamage(RandomValueRange.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(RandomValueRange.between(500f, 2000f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.HEART_SEEKER_ULTIMATE.get()).setWeight(9).apply(SetDamage.setDamage(RandomValueRange.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(RandomValueRange.between(500f, 2000f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.HEART_STRIKER_ENHANCED.get()).setWeight(21).apply(SetDamage.setDamage(RandomValueRange.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(RandomValueRange.between(500f, 2000f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.HEART_STRIKER_ULTIMATE.get()).setWeight(9).apply(SetDamage.setDamage(RandomValueRange.between(0.6f, 0.99f))).apply(SetItemBloodCharge.builder(RandomValueRange.between(500f, 2000f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(40)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(3))
                            .add(ItemLootEntry.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))));
        }
    }

    private static class ModBlockLootTables extends BlockLootTables {
        @Override
        protected void addTables() {
            this.dropSelf(ModBlocks.ALCHEMICAL_CAULDRON.get());
            this.dropSelf(ModBlocks.ALTAR_INFUSION.get());
            this.dropSelf(ModBlocks.ALTAR_INSPIRATION.get());
            this.add(ModBlocks.ALTAR_PILLAR.get(), createSingleItemTable(ModBlocks.ALTAR_PILLAR.get())
                    .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).when(SurvivesExplosion.survivesExplosion())
                            .add(ItemLootEntry.lootTableItem(Items.STONE_BRICKS).when(BlockStateProperty.hasBlockStateProperties(ModBlocks.ALTAR_PILLAR.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "stone"))))
                            .add(ItemLootEntry.lootTableItem(Items.IRON_BLOCK).when(BlockStateProperty.hasBlockStateProperties(ModBlocks.ALTAR_PILLAR.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "iron"))))
                            .add(ItemLootEntry.lootTableItem(Items.GOLD_BLOCK).when(BlockStateProperty.hasBlockStateProperties(ModBlocks.ALTAR_PILLAR.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "gold"))))
                            .add(ItemLootEntry.lootTableItem(Items.BONE_BLOCK).when(BlockStateProperty.hasBlockStateProperties(ModBlocks.ALTAR_PILLAR.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AltarPillarBlock.TYPE_PROPERTY, "bone"))))));
            this.dropSelf(ModBlocks.ALTAR_TIP.get());
            CoffinBlock.COFFIN_BLOCKS.values().forEach(coffin -> this.add(coffin, block -> createSinglePropConditionTable(block, CoffinBlock.PART, CoffinBlock.CoffinPart.HEAD)));
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
            this.add(ModBlocks.DARK_SPRUCE_DOOR.get(), BlockLootTables::createDoorTable);
            this.add(ModBlocks.CURSED_SPRUCE_DOOR.get(), BlockLootTables::createDoorTable);
            this.dropSelf(ModBlocks.CHURCH_ALTAR.get());
            this.dropSelf(ModBlocks.CURSED_EARTH.get());
            this.dropSelf(ModBlocks.FIRE_PLACE.get());
            this.add(ModBlocks.GARLIC.get(), applyExplosionDecay(ModBlocks.GARLIC.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(ItemLootEntry.lootTableItem(ModItems.ITEM_GARLIC.get())))
                    .withPool(LootPool.lootPool()
                            .when(BlockStateProperty.hasBlockStateProperties(ModBlocks.GARLIC.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(GarlicBlock.AGE, 7)))
                            .add(ItemLootEntry.lootTableItem(ModItems.ITEM_GARLIC.get()).apply(ApplyBonus.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))));
            this.dropSelf(ModBlocks.GARLIC_BEACON_WEAK.get());
            this.dropSelf(ModBlocks.GARLIC_BEACON_NORMAL.get());
            this.dropSelf(ModBlocks.GARLIC_BEACON_IMPROVED.get());
            this.dropSelf(ModBlocks.HUNTER_TABLE.get());
            this.add(ModBlocks.MED_CHAIR.get(), block -> createSinglePropConditionTable(block, MedChairBlock.PART, MedChairBlock.EnumPart.TOP));
            this.dropSelf(ModBlocks.SUNSCREEN_BEACON.get());
            this.add(ModBlocks.TENT_MAIN.get(), createSingleItemTable(ModItems.ITEM_TENT.get())
                    .withPool(LootPool.lootPool().name("bonus").setRolls(ConstantRange.exactly(1)).when(TentSpawnerCondition.builder())
                            .add(ItemLootEntry.lootTableItem(Items.APPLE))
                            .add(ItemLootEntry.lootTableItem(Items.BREAD))
                            .add(ItemLootEntry.lootTableItem(Items.COAL))
                            .add(ItemLootEntry.lootTableItem(Blocks.OAK_PLANKS))));
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
            this.add(ModBlocks.CROSS.get(), (p_218567_0_) -> createSinglePropConditionTable(p_218567_0_, VampirismSplitBlock.PART, VampirismSplitBlock.Part.MAIN));
            this.dropSelf(ModBlocks.TOMBSTONE1.get());
            this.dropSelf(ModBlocks.TOMBSTONE2.get());
            this.dropSelf(ModBlocks.TOMBSTONE3.get());
            this.dropSelf(ModBlocks.GRAVE_CAGE.get());
            this.add(ModBlocks.CURSED_GRASS.get(), createSingleItemTable(ModBlocks.CURSED_EARTH.get()));
            this.dropSelf(ModBlocks.DARK_SPRUCE_LOG.get());
            this.add(ModBlocks.CURSED_ROOTS.get(), (block) -> {
                return createShearsDispatchTable(block, applyExplosionDecay(block, ItemLootEntry.lootTableItem(Items.STICK).apply(SetCount.setCount(RandomValueRange.between(0.0F, 2.0F)))));
            });
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
            this.dropSelf(ModBlocks.VAMPIRE_RACK.get());
            this.add(ModBlocks.THRONE.get(), (p_218567_0_) -> createSinglePropConditionTable(p_218567_0_, VampirismSplitBlock.PART, VampirismSplitBlock.Part.MAIN));
            this.dropSelf(ModBlocks.ALCHEMY_TABLE.get());
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
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(15).apply(SetDamage.setDamage(RandomValueRange.between(1f, 1f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(5).apply(AddBookNbt.builder()))
                            .add(EmptyLootEntry.emptyItem().setWeight(40)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get()).setWeight(3).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get()).setWeight(3).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get()).setWeight(3).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()).setWeight(3).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(88)))
                    .withPool(LootPool.lootPool().name("hunter_weapons").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.HUNTER_AXE_ULTIMATE.get()).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(95)))
                    .withPool(LootPool.lootPool().name("vampire_weapons").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.HEART_SEEKER_ENHANCED.get()).setWeight(20).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.HEART_STRIKER_ENHANCED.get()).setWeight(20).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("holy_Water").setRolls(ConstantRange.exactly(2))
                            .add(ItemLootEntry.lootTableItem(ModItems.HOLY_SALT.get()).setWeight(50))
                            .add(ItemLootEntry.lootTableItem(ModItems.HOLY_WATER_BOTTLE_NORMAL.get()).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get()).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get()).setWeight(10)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.desert_pyramid, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(15))
                            .add(ItemLootEntry.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(20).apply(SetDamage.setDamage(RandomValueRange.between(0.6f, 0.6f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(8).apply(AddBookNbt.builder()))
                            .add(EmptyLootEntry.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.jungle_temple, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(2))
                            .add(ItemLootEntry.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(20))
                            .add(ItemLootEntry.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(20).apply(SetDamage.setDamage(RandomValueRange.between(1f, 1f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(20).apply(AddBookNbt.builder()))
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(20))
                            .add(EmptyLootEntry.emptyItem().setWeight(30)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get()).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get()).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get()).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(72)))
                    .withPool(LootPool.lootPool().name("hunter_coat").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.HUNTER_COAT_HEAD_ULTIMATE.get()).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.HUNTER_COAT_CHEST_ULTIMATE.get()).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.HUNTER_COAT_LEGS_ULTIMATE.get()).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.HUNTER_COAT_FEET_ULTIMATE.get()).setWeight(7).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(72)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.stronghold_corridor, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(2))
                            .add(ItemLootEntry.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(50))
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(20).apply(AddBookNbt.builder()))
                            .add(EmptyLootEntry.emptyItem().setWeight(27)))
                    .withPool(LootPool.lootPool().name("swiftness_armor").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get()).setWeight(5).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get()).setWeight(5).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get()).setWeight(5).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()).setWeight(5).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(80)))
                    .withPool(LootPool.lootPool().name("vampire_weapons").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.HEART_SEEKER_ENHANCED.get()).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(ItemLootEntry.lootTableItem(ModItems.HEART_STRIKER_ENHANCED.get()).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.3f, 0.9f))))
                            .add(EmptyLootEntry.emptyItem().setWeight(80)))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(3))
                            .add(ItemLootEntry.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.stronghold_library, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(1).apply(AddBookNbt.builder())))
                    .withPool(LootPool.lootPool().name("refinement_item").setRolls(ConstantRange.exactly(3))
                            .add(ItemLootEntry.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(ItemLootEntry.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
        }
    }

}
