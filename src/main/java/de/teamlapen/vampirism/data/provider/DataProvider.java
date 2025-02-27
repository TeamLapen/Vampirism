package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.data.ModBlockFamilies;
import de.teamlapen.vampirism.data.provider.model.ModelProvider;
import de.teamlapen.vampirism.data.provider.tags.TagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = REFERENCE.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataProvider {

    @SuppressWarnings("UnreachableCode")
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        CompletableFuture<HolderLookup.Provider> lookupProviderFuture = event.getLookupProvider();

        ModBlockFamilies.init();
        DatapackBuiltinEntriesProvider provider = new DatapackBuiltinEntriesProvider(packOutput, lookupProviderFuture, ModRegistries.DATA_BUILDER, Set.of(REFERENCE.MODID));
        lookupProviderFuture = provider.getRegistryProvider();
        generator.addProvider(true, provider);
        TagProvider.register(generator, event, packOutput, lookupProviderFuture);
        generator.addProvider(true, LootTablesProvider.getProvider(packOutput, lookupProviderFuture));
        generator.addProvider(true, new AdvancementProvider(packOutput, lookupProviderFuture));
        generator.addProvider(true, new RecipesProvider.Runner(packOutput, lookupProviderFuture));
        generator.addProvider(true, new ModelProvider(packOutput));
        generator.addProvider(true, new SingleJigsawPiecesProvider(packOutput, REFERENCE.MODID));
        generator.addProvider(true, new SundamageProvider(packOutput, REFERENCE.MODID));
        generator.addProvider(true, new SkillTreeProvider(packOutput, lookupProviderFuture));
        generator.addProvider(true, new DataMapsProvider(packOutput, lookupProviderFuture));
        generator.addProvider(true, new LootModifierGenerator(packOutput, lookupProviderFuture));
        generator.addProvider(true, new SoundDefinitionProvider(packOutput));
        generator.addProvider(true, new EquipmentAssetProvider(packOutput));
    }
}
