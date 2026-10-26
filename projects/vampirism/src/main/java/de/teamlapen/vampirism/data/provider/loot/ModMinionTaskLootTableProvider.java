package de.teamlapen.vampirism.data.provider.loot;

import de.teamlapen.vampirism.api.world.items.components.IBottleBlood;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModLootTables;
import de.teamlapen.vampirism.data.loot.functions.SetBloodFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

/**
 * Loot tables for {@link de.teamlapen.faction.common.factions.minions.management.CollectResourcesTask}s registered by Vampirism
 */
public class ModMinionTaskLootTableProvider implements LootTableSubProvider {

    @SuppressWarnings("unused")
    public ModMinionTaskLootTableProvider(HolderLookup.Provider registries) {
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(ModLootTables.MINION_COLLECT_BLOOD, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.BLOOD_BOTTLE.get()).setWeight(20).apply(SetBloodFunction.builder(2, IBottleBlood.MAX_VALUE)))
                        .add(LootItem.lootTableItem(ModItems.HUMAN_HEART.get()).setWeight(2))));
    }
}
