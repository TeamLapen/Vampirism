package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.tags.ModFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModFluidTagsProvider extends FluidTagsProvider {
    public ModFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, REFERENCE.MODID, existingFileHelper);
    }

    @NotNull
    @Override
    public String getName() {
        return REFERENCE.MODID + " " + super.getName();
    }

    @Override
    protected void addTags(HolderLookup.Provider holderLookup) {
        tag(ModFluidTags.BLOOD).add(ModFluids.BLOOD.get());
        tag(ModFluidTags.IMPURE_BLOOD).add(ModFluids.IMPURE_BLOOD.get());
    }
}
