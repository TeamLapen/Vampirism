package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class RecipesGenerator extends RecipeProvider {
    public RecipesGenerator(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.blood_grinder).key('Z', Blocks.HOPPER).key('Y', ItemTags.PLANKS).key('D', Tags.Items.GEMS_DIAMOND).key('X',Tags.Items.INGOTS_IRON).patternLine(" Z ").patternLine("YDY").patternLine("YXY").addCriterion("has_hopper", this.hasItem(Items.HOPPER)).build(consumer, new ResourceLocation(REFERENCE.MODID, "general/blood_grinder"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.blood_sieve).key('X',Tags.Items.INGOTS_IRON).key('Q',Tags.Items.STORAGE_BLOCKS_QUARTZ).key('Y', ItemTags.PLANKS).key('Z', Items.CAULDRON).patternLine("XQX").patternLine("YZY").patternLine("YXY").addCriterion("has_cauldron", this.hasItem(Items.CAULDRON)).build(consumer, new ResourceLocation(REFERENCE.MODID, "general/blood_sieve"));
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.castle_block_dark_brick, 8).addIngredient(ModBlocks.castle_block_normal_brick, 8).addIngredient(Items.BLACK_DYE).addCriterion("has_castle_brick", this.hasItem(ModBlocks.castle_block_normal_brick)).addCriterion("has_black_dye", this.hasItem(Items.BLACK_DYE)).build(consumer, new ResourceLocation(REFERENCE.MODID, "general/castle_block_dark_brick_0"));
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.castle_block_dark_brick, 7).addIngredient(Items.STONE_BRICKS, 7).addIngredient(Items.BLACK_DYE).addIngredient(ModBlocks.vampire_orchid).addCriterion("has_orchid", this.hasItem(ModBlocks.vampire_orchid)).build(consumer, new ResourceLocation(REFERENCE.MODID,"general/castle_block_dark_brick_1"));
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.castle_block_dark_stone, 7).addIngredient(Items.STONE, 7).addIngredient(Items.BLACK_DYE).addIngredient(ModBlocks.vampire_orchid).addCriterion("has_orchid", this.hasItem(ModBlocks.vampire_orchid)).build(consumer, new ResourceLocation(REFERENCE.MODID,"general/castle_block_dark_stone"));
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.castle_block_normal_brick, 8).addIngredient(Items.STONE_BRICKS, 8).addIngredient(ModBlocks.vampire_orchid).addCriterion("has_orchid", this.hasItem(ModBlocks.vampire_orchid)).build(consumer, new ResourceLocation(REFERENCE.MODID,"general/castle_block_normal_brick"));
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.castle_block_purple_brick, 8).addIngredient(ModBlocks.castle_block_normal_brick, 8).addIngredient(ModBlocks.vampire_orchid).addCriterion("has_orchid", this.hasItem(ModBlocks.vampire_orchid)).build(consumer, new ResourceLocation(REFERENCE.MODID,"general/castle_block_purple_brick"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_slab_dark_brick).patternLine("###").key('#',ModBlocks.castle_block_dark_brick).addCriterion("has_castle_brick",this.hasItem(ModBlocks.castle_block_dark_brick)).build(consumer, new ResourceLocation(REFERENCE.MODID, "general/castle_slab_dark_brick"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_slab_dark_stone).patternLine("###").key('#',ModBlocks.castle_block_dark_stone).addCriterion("has_castle_brick",this.hasItem(ModBlocks.castle_block_dark_stone)).build(consumer, new ResourceLocation(REFERENCE.MODID, "general/castle_slab_dark_stone"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_slab_purple_brick).patternLine("###").key('#',ModBlocks.castle_block_purple_brick).addCriterion("has_castle_brick",this.hasItem(ModBlocks.castle_block_purple_brick)).build(consumer, new ResourceLocation(REFERENCE.MODID, "general/castle_slab_purple_brick"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_stairs_dark_brick).patternLine("#  ").patternLine("## ").patternLine("###").key('#',ModBlocks.castle_block_dark_brick).addCriterion("has_castle_brick",this.hasItem(ModBlocks.castle_block_dark_brick)).build(consumer, new ResourceLocation(REFERENCE.MODID, "general/castle_stairs_dark_brick"));
    }

    @Nonnull
    @Override
    public String getName() {
        return "Vampirism Recipes";
    }
}
