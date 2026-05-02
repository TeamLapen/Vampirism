package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.core.ModStructures;
import de.teamlapen.vampirism.common.tags.ModStructureTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModStructureTagsProvider extends KeyTagProvider<Structure> {

    public ModStructureTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.STRUCTURE, provider, REFERENCE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(ModStructureTags.ON_ANCIENT_REMAINS_MAPS).add(ModStructures.MOTHER);
        this.tag(ModStructureTags.ON_CRYPT_MAPS).add(ModStructures.CRYPT);
        this.tag(ModStructureTags.VELMORRA_PORTAL).add(ModStructures.VELMORRA_PORTAL);
        this.tag(ModStructureTags.HUNTER_OUTPOST).add(ModStructures.HUNTER_OUTPOST_BADLANDS, ModStructures.HUNTER_OUTPOST_DESERT, ModStructures.HUNTER_OUTPOST_PLAINS, ModStructures.HUNTER_OUTPOST_VAMPIRE_FOREST);
    }
}
