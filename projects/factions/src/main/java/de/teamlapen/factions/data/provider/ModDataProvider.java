package de.teamlapen.factions.data.provider;

import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.common.core.ModRegistries;
import de.teamlapen.factions.data.provider.model.ModModelProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
@EventBusSubscriber
public class ModDataProvider {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        CompletableFuture<HolderLookup.Provider> lookupProviderFuture = event.getLookupProvider();

        var provider = new DatapackBuiltinEntriesProvider(packOutput, lookupProviderFuture, ModRegistries.DATA_BUILDER, Set.of(REFERENCE.MOD_ID));

        lookupProviderFuture = provider.getRegistryProvider();

        generator.addProvider(true, provider);

        ModTagsProvider.register(generator, packOutput, lookupProviderFuture);
        generator.addProvider(true, new ModRecipeProvider.Runner(packOutput, lookupProviderFuture));
        generator.addProvider(true, ModLootTableProvider.getProvider(packOutput, lookupProviderFuture));
        generator.addProvider(true, new SoundDefinitionsProvider(packOutput));
        generator.addProvider(true, new ModModelProvider(packOutput));
    }
}
