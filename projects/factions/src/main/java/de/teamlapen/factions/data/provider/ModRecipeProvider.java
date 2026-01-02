package de.teamlapen.factions.data.provider;

import de.teamlapen.factions.common.core.FactionBlocks;
import de.teamlapen.factions.common.core.FactionItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {

    protected ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, FactionItems.SYRINGE_EMPTY)
                .pattern("I")
                .pattern("G")
                .pattern("N")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', Tags.Items.GLASS_BLOCKS)
                .define('N', Tags.Items.NUGGETS_IRON)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .unlockedBy("has_iron_nugget", has(Tags.Items.NUGGETS_IRON))
                .save(output);
        shaped(RecipeCategory.DECORATIONS, FactionBlocks.MED_CHAIR)
                .pattern("IWI")
                .pattern("III")
                .pattern("IGI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('W', ItemTags.WOOL)
                .define('G', Items.GLASS_BOTTLE)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
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
