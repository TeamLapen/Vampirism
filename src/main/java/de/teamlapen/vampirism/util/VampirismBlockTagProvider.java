package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.Tags;

import static de.teamlapen.vampirism.core.ModTags.Blocks.OBSIDIAN;

public class VampirismBlockTagProvider extends BlockTagsProvider {
    public VampirismBlockTagProvider(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void registerTags() {
        getBuilder(OBSIDIAN).add(Blocks.OBSIDIAN);
        getBuilder(Tags.Blocks.DIRT).add(ModBlocks.cursed_earth);
    }

    @Override
    public String getName() {
        return "Vampirism Block Tags";
    }
}
