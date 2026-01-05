package de.teamlapen.factions.data.provider;

import de.teamlapen.factions.common.core.FactionBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {

    protected ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.DECORATIONS, FactionBlocks.TOTEM_BASE)
                .pattern("POP")
                .pattern("POP")
                .pattern("III")
                .define('P', ItemTags.PLANKS)
                .define('O', Tags.Items.OBSIDIANS)
                .define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_obsidian", has(Tags.Items.OBSIDIANS))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, FactionBlocks.TOTEM_TOP_CRAFTED)
                .pattern("O O")
                .pattern(" D ")
                .pattern("OIO")
                .define('O', Tags.Items.OBSIDIANS)
                .define('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
                .define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_diamond_block", has(Tags.Items.STORAGE_BLOCKS_DIAMOND))
                .unlockedBy("has_obsidian", has(Tags.Items.OBSIDIANS))
                .save(output);
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            return new ModRecipeProvider(provider, output);
        }

        @Override
        public String getName() {
            return "Factions Recipes";
        }
    }
}
