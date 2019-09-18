package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;

public class VampirismBlockTagProvider extends BlockTagsProvider {
    public VampirismBlockTagProvider(DataGenerator gen) {
        super(gen);
    }

    @Override
    public String getName() {
        return "Vampirism Block Tags";
    }

    @Override
    protected void registerTags() {
        getBuilder(ModTags.Blocks.OBSIDIAN).add(Blocks.OBSIDIAN);
        getBuilder(Tags.Blocks.DIRT).add(ModBlocks.cursed_earth);
        getBuilder(ModTags.Blocks.CURSEDEARTH).add(ModBlocks.cursed_earth);
    }
}
