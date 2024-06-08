package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModStructures;
import de.teamlapen.vampirism.core.tags.ModStructureTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModStructuresProvider extends TagsProvider<Structure> {

    protected ModStructuresProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.STRUCTURE, provider, REFERENCE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(ModStructureTags.HUNTER_OUTPOST).add(ModStructures.HUNTER_OUTPOST_BADLANDS, ModStructures.HUNTER_OUTPOST_DESERT, ModStructures.HUNTER_OUTPOST_PLAINS, ModStructures.HUNTER_OUTPOST_VAMPIRE_FOREST);
    }
}
