package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.data.provider.tags.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class ModTagsProvider {

    public static void register(DataGenerator generator, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProviderFuture) {
        ModBlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(output, lookupProviderFuture);
        generator.addProvider(true, blockTagsProvider);
        generator.addProvider(true, new ModItemTagsProvider(output, lookupProviderFuture, blockTagsProvider.contentsGetter()));
        generator.addProvider(true, new ModEntityTypeTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModFluidTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModBiomeTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModPoiTypeTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModVillageProfessionTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModDamageTypeTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModTaskTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModStructureTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModSkillTreeTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModEffectTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModPotionTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModEnchantmentTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModFactionTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModActionTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModVampireBookTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModGameEventTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModDataComponentTagsProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModVillageTradesTagProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModTimelineTagProvider(output, lookupProviderFuture));
        generator.addProvider(true, new ModOilTagsProvider(output, lookupProviderFuture));
    }
}
