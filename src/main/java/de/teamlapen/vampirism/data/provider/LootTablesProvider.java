package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.mixin.accessor.VanillaBlockLootAccessor;
import de.teamlapen.vampirism.world.loot.conditions.AdjustableLevelCondition;
import de.teamlapen.vampirism.world.loot.conditions.StakeCondition;
import de.teamlapen.vampirism.world.loot.conditions.TentSpawnerCondition;
import de.teamlapen.vampirism.world.loot.functions.AddBookNbtFunction;
import de.teamlapen.vampirism.world.loot.functions.RefinementSetFunction;
import de.teamlapen.vampirism.world.loot.functions.SetItemBloodChargeFunction;
import de.teamlapen.vampirism.world.loot.functions.SetOilFunction;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class LootTablesProvider {

    /**
     * from {@link net.minecraft.data.loot.packs.VanillaBlockLoot} but halved
     */
    public static final float[] DEFAULT_SAPLING_DROP_RATES = new float[] {0.025F, 0.03125f, 0.041666668f, 0.05f};

    public static LootTableProvider getProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProviderFuture) {
        return new LootTableProvider(output, ModLootTables.getLootTables(),
                List.of(
                        new LootTableProvider.SubProviderEntry(ModEntityLootTables::new, LootContextParamSets.ENTITY),
                        new LootTableProvider.SubProviderEntry(ModChestLootTables::new, LootContextParamSets.CHEST),
                        new LootTableProvider.SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK),
                        new LootTableProvider.SubProviderEntry(InjectLootTables::new, LootContextParamSets.CHEST)),
                lookupProviderFuture);
    }

    private static class ModEntityLootTables extends EntityLootSubProvider {

        protected ModEntityLootTables() {
            super(FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected @NotNull Stream<EntityType<?>> getKnownEntityTypes() {
            return ModEntities.getAllEntities().stream();
        }

        @Override
        public void generate() {
            CompoundTag splash = new CompoundTag();
            splash.putBoolean("splash", true);

            LootTable.Builder advanced_hunter = LootTable.lootTable()
                    .withPool(LootPool.lootPool().when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .setRolls(UniformGenerator.between(0, 1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setWeight(4))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(4).apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1))))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_SPLASH_BOTTLE_ENHANCED.get()).setWeight(3))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_SPLASH_BOTTLE_ULTIMATE.get()).setWeight(1))
                            .add(LootItem.lootTableItem(ModItems.PURE_SALT_WATER.get()).setWeight(4).apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1))).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))))
                    .withPool(LootPool.lootPool().when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.1f, 0.015f)).setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(1).apply(AddBookNbtFunction.builder())));
            this.add(ModEntities.ADVANCED_HUNTER.get(), advanced_hunter);
            this.add(ModEntities.ADVANCED_HUNTER_IMOB.get(), advanced_hunter);
            LootTable.Builder advanced_vampire = LootTable.lootTable()
                    .withPool(LootPool.lootPool().when(LootItemKilledByPlayerCondition.killedByPlayer()).setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setWeight(1))
                            .add(LootItem.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(1).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.5f, 1.0f))).apply(LootingEnchantFunction.lootingMultiplier(ConstantValue.exactly(1f)))))
                    .withPool(LootPool.lootPool().when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.1f, 0.015f)).setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(1).apply(AddBookNbtFunction.builder())))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.05f, 0.01f))
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
            this.add(ModEntities.CONVERTED_CAMEL.get(), LootTable.lootTable());
            this.add(ModEntities.CONVERTED_DONKEY.get(), LootTable.lootTable());
            this.add(ModEntities.CONVERTED_MULE.get(), LootTable.lootTable());
            this.add(ModEntities.CONVERTED_FOX.get(), LootTable.lootTable());
            this.add(ModEntities.CONVERTED_GOAT.get(), LootTable.lootTable());
            this.add(ModEntities.DUMMY_CREATURE.get(), LootTable.lootTable());
            this.add(ModEntities.HUNTER_TRAINER.get(), LootTable.lootTable());
            LootTable.Builder vampire = LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.33f, 0.05f))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(StakeCondition.builder(LootContext.EntityTarget.KILLER_PLAYER)).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.5f, 0.05f))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.02f, 0.01f))
                            .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))));
            this.add(ModEntities.VAMPIRE.get(), vampire);
            this.add(ModEntities.VAMPIRE_IMOB.get(), vampire);
            this.add(ModEntities.VAMPIRE_BARON.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(0, LootContext.EntityTarget.THIS))
                            .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_0.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(1, LootContext.EntityTarget.THIS))
                            .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_1.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(2, LootContext.EntityTarget.THIS))
                            .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_2.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(3, LootContext.EntityTarget.THIS))
                            .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_3.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(4, LootContext.EntityTarget.THIS))
                            .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_4.get()).setWeight(1))));
            LootTable.Builder hunter = LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.33f, 0.005f))
                            .add(LootItem.lootTableItem(ModItems.HUMAN_HEART.get()).setWeight(1)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.05f, 0.02f))
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
            this.add(ModEntities.GHOST.get(), LootTable.lootTable());
        }
    }

    private static class ModChestLootTables implements LootTableSubProvider {

        @Override
        public void generate(HolderLookup.@NotNull Provider holderProvider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
            consumer.accept(ModLootTables.CHEST_HUNTER_TRAINER, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(5, 9))
                            .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(40))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(40)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(50).apply(AddBookNbtFunction.builder()))
                            .add(EmptyLootItem.emptyItem().setWeight(95)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_AXE_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(95)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_HEAD_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_CHEST_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_LEGS_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_FEET_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(5))
                            .add(LootItem.lootTableItem(ModItems.PURE_SALT.get()).setWeight(50))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_BOTTLE_NORMAL.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get()).setWeight(10))));
            consumer.accept(ModLootTables.CHEST_VAMPIRE_DUNGEON, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(7))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(35))
                            .add(LootItem.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(20).apply(SetItemDamageFunction.setDamage(ConstantValue.exactly(1f)))))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(70).apply(AddBookNbtFunction.builder()))
                            .add(EmptyLootItem.emptyItem().setWeight(30)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HEART_SEEKER_ENHANCED.get()).setWeight(21).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodChargeFunction.builder(UniformGenerator.between(500f, 2000f))))
                            .add(LootItem.lootTableItem(ModItems.HEART_SEEKER_ULTIMATE.get()).setWeight(9).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodChargeFunction.builder(UniformGenerator.between(500f, 2000f))))
                            .add(LootItem.lootTableItem(ModItems.HEART_STRIKER_ENHANCED.get()).setWeight(21).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodChargeFunction.builder(UniformGenerator.between(500f, 2000f))))
                            .add(LootItem.lootTableItem(ModItems.HEART_STRIKER_ULTIMATE.get()).setWeight(9).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodChargeFunction.builder(UniformGenerator.between(500f, 2000f))))
                            .add(EmptyLootItem.emptyItem().setWeight(40)))
                    .withPool(accessories(ConstantValue.exactly(3))));
            consumer.accept(ModLootTables.CHEST_VAMPIRE_HUT, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(3, 5))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0,0.6f)))))
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(0, 1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(70).apply(AddBookNbtFunction.builder())))
                    .withPool(accessories(ConstantValue.exactly(2)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HEART_SEEKER_ENHANCED.get()).setWeight(21).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodChargeFunction.builder(UniformGenerator.between(500f, 2000f))))
                            .add(LootItem.lootTableItem(ModItems.HEART_SEEKER_ULTIMATE.get()).setWeight(9).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodChargeFunction.builder(UniformGenerator.between(500f, 2000f))))
                            .add(LootItem.lootTableItem(ModItems.HEART_STRIKER_ENHANCED.get()).setWeight(21).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodChargeFunction.builder(UniformGenerator.between(500f, 2000f))))
                            .add(LootItem.lootTableItem(ModItems.HEART_STRIKER_ULTIMATE.get()).setWeight(9).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.99f))).apply(SetItemBloodChargeFunction.builder(UniformGenerator.between(500f, 2000f))))
                            .add(EmptyLootItem.emptyItem().setWeight(60)))
            );
            consumer.accept(ModLootTables.CHEST_VAMPIRE_ALTAR, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(5, 8))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0,0.6f)))))
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(10, 18))
                            .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(16))
                            .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(22).apply(SetItemDamageFunction.setDamage(ConstantValue.exactly(1f)))))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_0.get()).setWeight(25))
                            .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_1.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_2.get()).setWeight(15))
                            .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_3.get()).setWeight(10))
                            .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_4.get()).setWeight(5))
                            .add(EmptyLootItem.emptyItem().setWeight(25))
                    )
            );
            consumer.accept(ModLootTables.CHEST_CRYPT, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 12))
                            .add(LootItem.lootTableItem(ModItems.OBLIVION_POTION.get()).setWeight(5))
                            .add(LootItem.lootTableItem(ModItems.BLOOD_INFUSED_IRON_INGOT.get()).setWeight(25))
                            .add(LootItem.lootTableItem(ModItems.BLOOD_INFUSED_ENHANCED_IRON_INGOT.get()).setWeight(15))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(40))
                            .add(LootItem.lootTableItem(ModBlocks.VAMPIRE_ORCHID.get()).setWeight(30))
                            .add(LootItem.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0, 1f)))))
                    .withPool(accessories(UniformGenerator.between(0, 1)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).apply(AddBookNbtFunction.builder()).setWeight(30))
                            .add(EmptyLootItem.emptyItem().setWeight(70)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HEART_STRIKER_NORMAL.get()).setWeight(30))
                            .add(LootItem.lootTableItem(ModItems.HEART_STRIKER_ENHANCED.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.HEART_STRIKER_ULTIMATE.get()).setWeight(10))
                            .add(LootItem.lootTableItem(ModItems.HEART_SEEKER_NORMAL.get()).setWeight(30))
                            .add(LootItem.lootTableItem(ModItems.HEART_SEEKER_ENHANCED.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.HEART_SEEKER_ULTIMATE.get()).setWeight(10))
                            .add(EmptyLootItem.emptyItem().setWeight(200)))
            );
            consumer.accept(ModLootTables.CHEST_HUNTER_OUTPOST_TENT, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(5, 20))
                            .add(LootItem.lootTableItem(Items.BREAD).setWeight(10))
                            .add(LootItem.lootTableItem(Items.BEEF).setWeight(5))
                            .add(LootItem.lootTableItem(Items.CHICKEN).setWeight(5))
                            .add(LootItem.lootTableItem(Items.MUTTON).setWeight(5))
                            .add(LootItem.lootTableItem(Items.PORKCHOP).setWeight(5))
                            .add(LootItem.lootTableItem(Items.RABBIT).setWeight(5))
                            .add(LootItem.lootTableItem(Items.WHEAT).setWeight(15))
                            .add(LootItem.lootTableItem(Items.POTATO).setWeight(10))
                            .add(LootItem.lootTableItem(Items.CARROT).setWeight(10))
                            .add(LootItem.lootTableItem(Items.APPLE).setWeight(10))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(5))
                            .add(LootItem.lootTableItem(ModItems.GARLIC_BREAD.get()).setWeight(5))
                            .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(5))
                            .add(LootItem.lootTableItem(Items.EMERALD).setWeight(5))
                            .add(LootItem.lootTableItem(ModItems.PURIFIED_GARLIC.get()).setWeight(5))
                            .add(LootItem.lootTableItem(ModItems.PURE_SALT.get()).setWeight(5)))
            );
            consumer.accept(ModLootTables.CHEST_HUNTER_OUTPOST_ALCHEMY, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(2, 5))
                            .add(LootItem.lootTableItem(ModItems.OIL_BOTTLE.get()).setWeight(20).apply(SetOilFunction.random()))
                            .add(LootItem.lootTableItem(ModItems.OIL_BOTTLE.get()).setWeight(20).apply(SetOilFunction.setOil(ModOils.VAMPIRE_BLOOD)))
                            .add(LootItem.lootTableItem(Items.GLASS_BOTTLE).setWeight(10)))
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(3, 6))
                            .add(LootItem.lootTableItem(Items.BLAZE_ROD).setWeight(20))
                            .add(LootItem.lootTableItem(Items.GUNPOWDER).setWeight(20))
                            .add(LootItem.lootTableItem(Items.REDSTONE).setWeight(20))));
            consumer.accept(ModLootTables.CHEST_HUNTER_OUTPOST_SMITH, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(7, 20))
                            .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(30))
                            .add(LootItem.lootTableItem(Items.RAW_IRON).setWeight(30))
                            .add(LootItem.lootTableItem(Items.IRON_NUGGET).setWeight(10))
                            .add(LootItem.lootTableItem(Items.COPPER_INGOT).setWeight(5))
                            .add(LootItem.lootTableItem(Items.RAW_COPPER).setWeight(5))
                            .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(10))
                            .add(LootItem.lootTableItem(Items.RAW_GOLD).setWeight(10))
                            .add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(5)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(Items.NETHERITE_INGOT).setWeight(1))
                            .add(EmptyLootItem.emptyItem().setWeight(19)))
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(2, 4))
                            .add(LootItem.lootTableItem(Items.LAVA_BUCKET).setWeight(10))
                            .add(LootItem.lootTableItem(Items.BUCKET).setWeight(5))
                            .add(LootItem.lootTableItem(Items.COAL).setWeight(20)))
            );
            consumer.accept(ModLootTables.CHEST_HUNTER_OUTPOST_TOWER_FOOD, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(4, 9))
                            .add(LootItem.lootTableItem(Items.BREAD).setWeight(15))
                            .add(LootItem.lootTableItem(Items.COOKED_BEEF).setWeight(5))
                            .add(LootItem.lootTableItem(Items.COOKED_CHICKEN).setWeight(5))
                            .add(LootItem.lootTableItem(Items.COOKED_MUTTON).setWeight(5))
                            .add(LootItem.lootTableItem(Items.COOKED_PORKCHOP).setWeight(5))
                            .add(LootItem.lootTableItem(Items.APPLE).setWeight(10))
                    )
            );
            consumer.accept(ModLootTables.CHEST_HUNTER_OUTPOST_TOWER_BASIC, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 3))
                            .add(LootItem.lootTableItem(ModItems.BASIC_CROSSBOW.get()).setWeight(30))
                            .add(LootItem.lootTableItem(ModItems.BASIC_DOUBLE_CROSSBOW.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.ENHANCED_CROSSBOW.get()).setWeight(30))
                            .add(LootItem.lootTableItem(ModItems.ENHANCED_DOUBLE_CROSSBOW.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_AXE_NORMAL.get()).setWeight(60))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_AXE_ENHANCED.get()).setWeight(40))
                            .add(EmptyLootItem.emptyItem().setWeight(90))
                    )
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(5, 9))
                            .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(20))
                            .add(LootItem.lootTableItem(Items.GOLD_BLOCK).setWeight(10))
                            .add(LootItem.lootTableItem(ModItems.GARLIC_BREAD.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.OBLIVION_POTION.get()).setWeight(10))
                            .add(LootItem.lootTableItem(ModItems.ITEM_ALCHEMICAL_FIRE.get()).setWeight(15))
                    )
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(0, 1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).apply(AddBookNbtFunction.builder()).setWeight(20))
                            .add(EmptyLootItem.emptyItem().setWeight(80))));
            consumer.accept(ModLootTables.CHEST_HUNTER_OUTPOST_TOWER_SPECIAL, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.BASIC_CROSSBOW.get()).setWeight(30))
                            .add(LootItem.lootTableItem(ModItems.BASIC_DOUBLE_CROSSBOW.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.BASIC_TECH_CROSSBOW.get()).setWeight(10))
                            .add(LootItem.lootTableItem(ModItems.ENHANCED_CROSSBOW.get()).setWeight(30))
                            .add(LootItem.lootTableItem(ModItems.ENHANCED_DOUBLE_CROSSBOW.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.ENHANCED_TECH_CROSSBOW.get()).setWeight(10))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_AXE_NORMAL.get()).setWeight(60))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_AXE_ENHANCED.get()).setWeight(40))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_AXE_ULTIMATE.get()).setWeight(20))
                            .add(EmptyLootItem.emptyItem().setWeight(80))
                    )
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(5, 9))
                            .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(20))
                            .add(LootItem.lootTableItem(Items.GOLD_BLOCK).setWeight(10))
                            .add(LootItem.lootTableItem(ModItems.GARLIC_BREAD.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.OBLIVION_POTION.get()).setWeight(10))
                            .add(LootItem.lootTableItem(ModItems.ITEM_ALCHEMICAL_FIRE.get()).setWeight(15))
                    )
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(100)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_HEAD_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_CHEST_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_LEGS_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_FEET_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(100)))
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(0, 1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).apply(AddBookNbtFunction.builder()).setWeight(50))
                            .add(EmptyLootItem.emptyItem().setWeight(50)))
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 3))
                            .add(LootItem.lootTableItem(Items.SPYGLASS).setWeight(50))
                            .add(LootItem.lootTableItem(Items.PAPER).setWeight(50))
                            .add(LootItem.lootTableItem(Items.FEATHER).setWeight(50)))
            );
        }

        protected static LootPool.Builder accessories(NumberProvider rolls) {
            return LootPool.lootPool().setRolls(rolls)
                    .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                    .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                    .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)));
        }
    }

    private static class ModBlockLootTables extends BlockLootSubProvider {

        protected ModBlockLootTables() {
            super(VanillaBlockLootAccessor.getEXPLOSION_RESISTANT(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
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
            CoffinBlock.COFFIN_BLOCKS.values().forEach(coffin -> this.add(coffin, block -> createSinglePropConditionTable(block, CoffinBlock.PART, CoffinBlock.CoffinPart.HEAD)));
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
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get())))
                    .withPool(LootPool.lootPool()
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.GARLIC.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(GarlicBlock.AGE, 7)))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.FORTUNE, 0.5714286F, 3))))));
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
            this.add(ModBlocks.TOMBSTONE3.get(), context -> createSinglePropConditionTable(context, VampirismSplitBlock.PART, VampirismSplitBlock.Part.MAIN));
            this.dropSelf(ModBlocks.GRAVE_CAGE.get());
            this.add(ModBlocks.CURSED_GRASS.get(), block -> createSingleItemTableWithSilkTouch(block, ModBlocks.CURSED_EARTH.get()));
            this.dropSelf(ModBlocks.DARK_SPRUCE_LOG.get());
            this.dropPottedContents(ModBlocks.POTTED_CURSED_ROOTS.get());
            this.dropSelf(ModBlocks.CURSED_SPRUCE_LOG.get());
            this.dropSelf(ModBlocks.CURSED_SPRUCE_LOG_CURED.get());
            this.add(ModBlocks.DIRECT_CURSED_BARK.get(), noDrop());
            this.dropSelf(ModBlocks.DARK_SPRUCE_STAIRS.get());
            this.dropSelf(ModBlocks.CURSED_SPRUCE_STAIRS.get());
            this.dropSelf(ModBlocks.DARK_SPRUCE_WOOD.get());
            this.dropSelf(ModBlocks.CURSED_SPRUCE_WOOD.get());
            this.dropSelf(ModBlocks.CURSED_SPRUCE_WOOD_CURED.get());
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
            this.add(ModBlocks.THRONE.get(), (p_218567_0_) -> createSinglePropConditionTable(p_218567_0_, VampirismSplitBlock.PART, VampirismSplitBlock.Part.MAIN));
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
            this.add(ModBlocks.CURSED_HANGING_ROOTS.get(), ModBlockLootTables::createShearsOnlyDrop);
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
            this.dropMountedCandle(ModBlocks.CANDLE_STICK.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_NORMAL.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_WHITE.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_ORANGE.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_MAGENTA.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_LIGHT_BLUE.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_YELLOW.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_LIME.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_PINK.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_GRAY.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_LIGHT_GRAY.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_CYAN.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_PURPLE.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_BLUE.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_BROWN.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_GREEN.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_RED.get());
            this.dropMountedCandle(ModBlocks.CANDLE_STICK_BLACK.get());
            this.dropSelf(ModBlocks.VAMPIRE_SOUL_LANTERN.get());
        }

        @NotNull
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ModBlocks.getAllBlocks();
        }

        private void dropMountedCandle(CandleStickBlock block) {
            this.add(block, (block1) -> this.createHolderCandleItemTable(block.getCandle().get()));
        }

        protected LootTable.Builder createHolderCandleItemTable(@Nullable ItemLike pItem) {
            var table = LootTable.lootTable()
                    .withPool(this.applyExplosionCondition(ModBlocks.CANDLE_STICK.get(), LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModBlocks.CANDLE_STICK.get()))));
            if (pItem != null) {
                table = table.withPool(this.applyExplosionCondition(pItem, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(pItem))));
            }
            return table;
        }
    }

    private static class InjectLootTables implements LootTableSubProvider {
        @Override
        public void generate(HolderLookup.@NotNull Provider holderProvider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
            consumer.accept(ModLootTables.ABANDONED_MINESHAFT, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(0f, 4f))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(15).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(1f, 1f))))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(5).apply(AddBookNbtFunction.builder()))
                            .add(EmptyLootItem.emptyItem().setWeight(40)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get()).setWeight(3).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get()).setWeight(3).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get()).setWeight(3).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()).setWeight(3).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(88)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_AXE_ULTIMATE.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(95)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HEART_SEEKER_ENHANCED.get()).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HEART_STRIKER_ENHANCED.get()).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(2))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_BOTTLE_NORMAL.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get()).setWeight(10)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.DESERT_PYRAMID, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(15))
                            .add(LootItem.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.6f, 0.6f))))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(8).apply(AddBookNbtFunction.builder()))
                            .add(EmptyLootItem.emptyItem().setWeight(60)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.JUNGLE_TEMPLE, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(2))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(20))
                            .add(LootItem.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(20).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(1f, 1f))))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(20).apply(AddBookNbtFunction.builder()))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(20))
                            .add(EmptyLootItem.emptyItem().setWeight(30)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(72)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_HEAD_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_CHEST_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_LEGS_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HUNTER_COAT_FEET_ULTIMATE.get()).setWeight(7).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(72)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.STRONGHOLD_CORRIDOR, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(2))
                            .add(LootItem.lootTableItem(ModItems.ITEM_GARLIC.get()).setWeight(50))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(20).apply(AddBookNbtFunction.builder()))
                            .add(EmptyLootItem.emptyItem().setWeight(27)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get()).setWeight(5).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get()).setWeight(5).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get()).setWeight(5).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()).setWeight(5).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(80)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.HEART_SEEKER_ENHANCED.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(LootItem.lootTableItem(ModItems.HEART_STRIKER_ENHANCED.get()).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.3f, 0.9f))))
                            .add(EmptyLootItem.emptyItem().setWeight(80)))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(3))
                            .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
            consumer.accept(ModLootTables.STRONGHOLD_LIBRARY, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(1).apply(AddBookNbtFunction.builder())))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(3))
                            .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(VReference.VAMPIRE_FACTION))))
            );
        }
    }
}
