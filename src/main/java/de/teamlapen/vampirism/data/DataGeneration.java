package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = REFERENCE.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGeneration {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        CompletableFuture<HolderLookup.Provider> lookupProviderFuture = event.getLookupProvider().thenApply(ModRegistries::createLookup);
        HolderLookup.Provider lookupProvider = lookupProviderFuture.join();

        ModBlockFamilies.init();
        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(packOutput, ModRegistries::createLookup));
        TagGenerator.register(generator, event, packOutput, lookupProviderFuture, existingFileHelper);
        generator.addProvider(event.includeServer(), new LootTablesGenerator(packOutput));
        generator.addProvider(event.includeServer(), new AdvancementGenerator(packOutput, lookupProviderFuture, existingFileHelper));
        generator.addProvider(event.includeServer(), new RecipesGenerator(packOutput));
        generator.addProvider(event.includeServer(), new ModSkillNodeProvider(packOutput));
//        BiomeModifierGenerator.register(event, generator, lookupProvider); //TODO 1.19 re-add when possible
        generator.addProvider(event.includeClient(), new BlockStateGenerator(generator, existingFileHelper));
        generator.addProvider(event.includeClient(), new ItemModelGenerator(generator, existingFileHelper));
    }
}
