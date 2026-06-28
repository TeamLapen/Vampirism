package de.teamlapen.vampirism.data.provider.loot;

import de.teamlapen.vampirism.api.world.items.components.IBottleBlood;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.data.loot.conditions.AdjustableLevelCondition;
import de.teamlapen.vampirism.data.loot.conditions.StakeCondition;
import de.teamlapen.vampirism.data.loot.functions.RefinementSetFunction;
import de.teamlapen.vampirism.data.loot.functions.SetBloodFunction;
import de.teamlapen.vampirism.data.loot.functions.SetVampireBookFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.stream.Stream;

import static de.teamlapen.vampirism.common.core.ModEntities.*;

public class ModEntityLootTableProvider extends EntityLootSubProvider {
    
    public ModEntityLootTableProvider(HolderLookup.Provider lookupProvider) {
        super(FeatureFlags.REGISTRY.allFlags(), lookupProvider);
    }

    @Override
    public void generate() {
        LootTable.Builder vampire = LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.33f, 0.05f))
                        .add(LootItem.lootTableItem(ModItems.VAMPIRE_FANG.get()).setWeight(1)))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(StakeCondition.builder(LootContext.EntityTarget.ATTACKING_PLAYER)).when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.5f, 0.05f))
                        .add(LootItem.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setWeight(1)))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.02f, 0.01f))
                        .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(ModFactions.VAMPIRE)))
                        .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(ModFactions.VAMPIRE)))
                        .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(ModFactions.VAMPIRE))))
                        ;
        this.add(VAMPIRE.get(), vampire);
        this.add(VAMPIRE_IMOB.get(), vampire);

        LootTable.Builder advancedVampire = LootTable.lootTable()
                .withPool(LootPool.lootPool().when(LootItemKilledByPlayerCondition.killedByPlayer()).setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setWeight(1))
                        .add(LootItem.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(1).apply(SetBloodFunction.builder(2, IBottleBlood.MAX_VALUE)).apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, ConstantValue.exactly(1f)))))
                .withPool(LootPool.lootPool().when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.1f, 0.015f)).setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(1).apply(SetVampireBookFunction.special())))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.05f, 0.01f))
                        .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(ModFactions.VAMPIRE)))
                        .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(ModFactions.VAMPIRE)))
                        .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(ModFactions.VAMPIRE))))
                ;
        this.add(ADVANCED_VAMPIRE.get(), advancedVampire);
        this.add(ADVANCED_VAMPIRE_IMOB.get(), advancedVampire);

        this.add(VAMPIRE_BARON.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(0, LootContext.EntityTarget.THIS))
                        .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_0.get()).setWeight(1)))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(1, LootContext.EntityTarget.THIS))
                        .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_1.get()).setWeight(1)))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(2, LootContext.EntityTarget.THIS))
                        .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_2.get()).setWeight(1)))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(3, LootContext.EntityTarget.THIS))
                        .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_3.get()).setWeight(1)))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(AdjustableLevelCondition.builder(4, LootContext.EntityTarget.THIS))
                        .add(LootItem.lootTableItem(ModItems.PURE_BLOOD_4.get()).setWeight(1)))
        );

        LootTable.Builder hunter = LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.33f, 0.005f))
                        .add(LootItem.lootTableItem(ModItems.HUMAN_HEART.get()).setWeight(1)))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.05f, 0.02f))
                        .add(LootItem.lootTableItem(ModItems.AMULET.get()).setWeight(1).apply(RefinementSetFunction.builder(ModFactions.VAMPIRE)))
                        .add(LootItem.lootTableItem(ModItems.RING.get()).setWeight(1).apply(RefinementSetFunction.builder(ModFactions.VAMPIRE)))
                        .add(LootItem.lootTableItem(ModItems.OBI_BELT.get()).setWeight(1).apply(RefinementSetFunction.builder(ModFactions.VAMPIRE))))
                ;
        this.add(HUNTER.get(), hunter);
        this.add(HUNTER_IMOB.get(), hunter);

        LootTable.Builder advancedHunter = LootTable.lootTable()
                .withPool(LootPool.lootPool().when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .setRolls(UniformGenerator.between(0, 1))
                        .add(LootItem.lootTableItem(ModItems.VAMPIRE_BLOOD_BOTTLE.get()).setWeight(4))
                        .add(LootItem.lootTableItem(ModBlocks.GARLIC.get()).setWeight(4).apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))))
                        .add(LootItem.lootTableItem(ModItems.HOLY_WATER_SPLASH_BOTTLE_ENHANCED.get()).setWeight(3))
                        .add(LootItem.lootTableItem(ModItems.HOLY_WATER_SPLASH_BOTTLE_ULTIMATE.get()).setWeight(1))
                        .add(LootItem.lootTableItem(ModItems.PURE_SALT_WATER.get()).setWeight(4).apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))))
                .withPool(LootPool.lootPool().when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.1f, 0.015f)).setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.VAMPIRE_BOOK.get()).setWeight(1).apply(SetVampireBookFunction.special())))
                ;
        this.add(ADVANCED_HUNTER.get(), advancedHunter);
        this.add(ADVANCED_HUNTER_IMOB.get(), advancedHunter);

        // No loot table
        Stream.of(
                BLINDING_BAT,
                CONVERTED_CREATURE,
                CONVERTED_CREATURE_IMOB,
                DUMMY_CREATURE,
                HUNTER_TRAINER,
                VILLAGER_ANGRY,
                VILLAGER_CONVERTED,
                TASK_MASTER_VAMPIRE,
                TASK_MASTER_HUNTER,
                VAMPIRE_MINION,
                HUNTER_MINION,
                GHOST,
                THROWABLE_ITEM,
                SIT,
                CROSSBOW_ARROW,
                DARK_BLOOD_PROJECTILE,
                SOUL_ORB,
                HUNTER_TRAINER_DUMMY,
                PARTICLE_CLOUD,
                REMAINS_DEFENDER,
                VULNERABLE_REMAINS_DUMMY
        ).forEach(entity -> this.add(entity.get(), LootTable.lootTable()));
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return getAllEntities().stream();
    }
}
