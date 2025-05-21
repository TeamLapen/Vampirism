package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.core.ModLootTables;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.world.loot.modifiers.SmeltItemLootModifier;
import de.teamlapen.vampirism.world.loot.conditions.FactionCondition;
import de.teamlapen.vampirism.world.loot.conditions.OilItemCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class LootModifierGenerator extends GlobalLootModifierProvider {

    public LootModifierGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, REFERENCE.MODID);
    }

    @Override
    protected void start() {
        add("smelting", new SmeltItemLootModifier(new OilItemCondition(ModOils.SMELT.get()), new FactionCondition(ModFactions.HUNTER)));

        addChestLoot("abandoned_mineshaft", ModLootTables.INJECT_ABANDONED_MINESHAFT, BuiltInLootTables.ABANDONED_MINESHAFT.location());
        addChestLoot("jungle_temple", ModLootTables.INJECT_JUNGLE_TEMPLE, BuiltInLootTables.JUNGLE_TEMPLE.location());
        addChestLoot("stronghold_corridor", ModLootTables.INJECT_STRONGHOLD_CORRIDOR, BuiltInLootTables.STRONGHOLD_CORRIDOR.location());
        addChestLoot("desert_pyramid", ModLootTables.INJECT_DESERT_PYRAMID, BuiltInLootTables.DESERT_PYRAMID.location());
        addChestLoot("stronghold_library", ModLootTables.INJECT_STRONGHOLD_LIBRARY, BuiltInLootTables.STRONGHOLD_LIBRARY.location());
    }

    private void addChestLoot(String name, ResourceKey<LootTable> insertedPool, ResourceLocation targetPool) {
        add("add_loot_" + name, new AddTableLootModifier(new LootItemCondition[] { LootTableIdCondition.builder(targetPool).build() }, insertedPool));
    }
}
