package de.teamlapen.vampirism.data.provider.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class TagProvider {

    public static void register(DataGenerator gen, @NotNull GatherDataEvent.Client event, PackOutput output, CompletableFuture<HolderLookup.Provider> future, @SuppressWarnings("removal") net.neoforged.neoforge.common.data.ExistingFileHelper existingFileHelper) {
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(output, future, existingFileHelper);
        gen.addProvider(true, blockTagsProvider);
        gen.addProvider(true, new ModItemTagsProvider(output, future, blockTagsProvider.contentsGetter(), existingFileHelper));
        gen.addProvider(true, new ModEntityTypeTagsProvider(output, future, existingFileHelper));
        gen.addProvider(true, new ModFluidTagsProvider(output, future, existingFileHelper));
        gen.addProvider(true, new ModBiomeTagsProvider(output, future, existingFileHelper));
        gen.addProvider(true, new ModPoiTypeProvider(output, future, existingFileHelper));
        gen.addProvider(true, new ModVillageProfessionProvider(output, future, existingFileHelper));
        gen.addProvider(true, new ModDamageTypeProvider(output, future, existingFileHelper));
        gen.addProvider(true, new ModTasksProvider(output, future, existingFileHelper));
        gen.addProvider(true, new ModStructuresProvider(output, future, existingFileHelper));
        gen.addProvider(true, new ModSkillTreeProvider(output, future, existingFileHelper));
        gen.addProvider(true, new ModEffectTypeProvider(output, future, existingFileHelper));
        gen.addProvider(true, new ModEnchantmentProvider(output, future, existingFileHelper));
        gen.addProvider(true, new ModFactionProvider(output, future, existingFileHelper));
        gen.addProvider(true, new ModActionTagsProvider(output, future, existingFileHelper));
        gen.addProvider(true, new ModVampireBookTagsProvider(output, future, existingFileHelper));
        gen.addProvider(true, new ModGameEventProvider(output, future, existingFileHelper));
        gen.addProvider(true, new DataComponentTagsProvider(output, future, existingFileHelper));
    }

}
