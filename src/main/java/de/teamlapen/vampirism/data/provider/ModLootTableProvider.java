package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.data.provider.loot.ModBlockLootTableProvider;
import de.teamlapen.vampirism.data.provider.loot.ModChestLootTableProvider;
import de.teamlapen.vampirism.data.provider.loot.ModEntityLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider {

    public static LootTableProvider getProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProviderFuture) {
        return new LootTableProvider(output, ModLootTables.getLootTables(),
                List.of(
                        new LootTableProvider.SubProviderEntry(ModEntityLootTableProvider::new, LootContextParamSets.ENTITY),
                        new LootTableProvider.SubProviderEntry(ModChestLootTableProvider::new, LootContextParamSets.CHEST),
                        new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)),
                lookupProviderFuture);
    }
}
