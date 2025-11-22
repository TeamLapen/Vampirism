package de.teamlapen.factions.common.data.provider;

import de.teamlapen.factions.common.data.provider.tags.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class ModTagsProvider {

    static void register(DataGenerator generator, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProviderFuture) {
        generator.addProvider(true, new ModFactionTagProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModSkillTreeTagProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModPoiTypeTagProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModTaskTagProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModBlockTagProvider(output, lookupProviderFuture));
    }
}
