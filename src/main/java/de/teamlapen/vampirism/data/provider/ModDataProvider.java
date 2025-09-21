package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.core.ModRegistries;
import de.teamlapen.vampirism.data.ModBlockFamilies;
import de.teamlapen.vampirism.data.provider.models.ModModelProvider;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = REFERENCE.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModDataProvider {

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
        ModTagsProvider.register(generator, packOutput, lookupProviderFuture);
        generator.addProvider(true, ModLootTableProvider.getProvider(packOutput, lookupProviderFuture));
        generator.addProvider(true, new ModAdvancementProvider(packOutput, lookupProviderFuture));
        generator.addProvider(true, new ModRecipeProvider.Runner(packOutput, lookupProviderFuture));
        generator.addProvider(true, new ModModelProvider(packOutput));
        generator.addProvider(true, new ModSingleJigsawPiecesProvider(packOutput, REFERENCE.MODID));
        generator.addProvider(true, new ModSundamageProvider(packOutput, REFERENCE.MODID));
        generator.addProvider(true, new ModSkillTreeProvider(packOutput, lookupProviderFuture));
        generator.addProvider(true, new ModDataMapProvider(packOutput, lookupProviderFuture));
        generator.addProvider(true, new ModLootModifierProvider(packOutput, lookupProviderFuture));
        generator.addProvider(true, new ModSoundDefinitionsProvider(packOutput));
        generator.addProvider(true, new ModEquipmentAssetProvider(packOutput));
        generator.addProvider(true, new ModBookBackgroundsProvider(packOutput));
        generator.addProvider(true, new PackMetadataGenerator(packOutput).add(PackMetadataSection.TYPE, new PackMetadataSection(Component.literal("Vampirism resources"), DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES))));
    }
}
