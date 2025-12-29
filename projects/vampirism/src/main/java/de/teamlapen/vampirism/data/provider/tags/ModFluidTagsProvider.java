package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.core.ModFluids;
import de.teamlapen.vampirism.common.tags.ModFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModFluidTagsProvider extends FluidTagsProvider {
    public ModFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, REFERENCE.MODID);
    }

    @NotNull
    @Override
    public String getName() {
        return REFERENCE.MODID + " " + super.getName();
    }

    @Override
    protected void addTags(HolderLookup.Provider holderLookup) {
        tag(ModFluidTags.BLOOD).add(ModFluids.BLOOD.get());
    }
}
