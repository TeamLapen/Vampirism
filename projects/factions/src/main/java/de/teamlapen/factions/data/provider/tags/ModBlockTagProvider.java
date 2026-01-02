package de.teamlapen.factions.data.provider.tags;

import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.common.core.FactionBlocks;
import de.teamlapen.factions.common.tags.FactionBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {

    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, REFERENCE.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(FactionBlocks.TOTEM_BASE.get())
                .addTag(FactionBlockTags.TOTEM_TOP)
                .add(FactionBlocks.MED_CHAIR.get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(FactionBlocks.MED_CHAIR.get());
        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(FactionBlocks.TOTEM_BASE.get())
                .addTag(FactionBlockTags.TOTEM_TOP_CRAFTED);

        tag(FactionBlockTags.TOTEM_TOP)
                .addTag(FactionBlockTags.TOTEM_TOP_FRAGILE)
                .addTag(FactionBlockTags.TOTEM_TOP_CRAFTED);

        tag(FactionBlockTags.TOTEM_TOP_FRAGILE)
                .add(FactionBlocks.TOTEM_TOP.get());

        tag(FactionBlockTags.TOTEM_TOP_CRAFTED)
                .add(FactionBlocks.TOTEM_TOP_CRAFTED.get());
    }
}
