package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.data.ModBlockFamilies;
import de.teamlapen.vampirism.data.provider.tags.TagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = REFERENCE.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataProvider {

    @SuppressWarnings("UnreachableCode")
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        CompletableFuture<HolderLookup.Provider> lookupProviderFuture = event.getLookupProvider();

        ModBlockFamilies.init();
        DatapackBuiltinEntriesProvider provider = new DatapackBuiltinEntriesProvider(packOutput, lookupProviderFuture, ModRegistries.DATA_BUILDER, Set.of(REFERENCE.MODID));
        lookupProviderFuture = provider.getRegistryProvider();
        generator.addProvider(event.includeServer(), provider);
        TagProvider.register(generator, event, packOutput, lookupProviderFuture, existingFileHelper);
        generator.addProvider(event.includeServer(), LootTablesProvider.getProvider(packOutput, lookupProviderFuture));
        generator.addProvider(event.includeServer(), new AdvancementProvider(packOutput, lookupProviderFuture, existingFileHelper));
        generator.addProvider(event.includeServer(), new RecipesProvider(packOutput, lookupProviderFuture));
        generator.addProvider(event.includeClient(), new BlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new ItemModelGenerator(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), new SingleJigsawPiecesProvider(packOutput, REFERENCE.MODID));
        generator.addProvider(event.includeServer(), new SundamageProvider(packOutput, REFERENCE.MODID));
        generator.addProvider(event.includeServer(), new SkillTreeProvider(packOutput, lookupProviderFuture));
        generator.addProvider(event.includeServer(), new DataMapsProvider(packOutput, lookupProviderFuture));
        generator.addProvider(event.includeServer(), new LootModifierGenerator(packOutput, lookupProviderFuture));
        generator.addProvider(event.includeClient(), new SoundDefinitionProvider(packOutput, existingFileHelper));
    }
}
