package de.teamlapen.vampirism.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.util.REFERENCE;
import javafx.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RecipesGenerator extends RecipeProvider {
    public RecipesGenerator(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        IItemProvider hopper = Blocks.HOPPER;
        IItemProvider cauldron = Blocks.CAULDRON;
        IItemProvider black_dye = Items.BLACK_DYE;
        IItemProvider stone_bricks = Blocks.STONE_BRICKS;
        IItemProvider vampire_orchid = ModBlocks.vampire_orchid;
        IItemProvider stone = Blocks.STONE;
        IItemProvider castle_block_dark_brick = ModBlocks.castle_block_dark_brick;
        IItemProvider castle_block_dark_stone = ModBlocks.castle_block_dark_stone;
        IItemProvider castle_block_normal_brick = ModBlocks.castle_block_normal_brick;
        IItemProvider castle_block_purple_brick = ModBlocks.castle_block_purple_brick;
        IItemProvider vampire_book = ModItems.vampire_book;
        IItemProvider vampire_fang = ModItems.vampire_fang;
        IItemProvider book = Items.BOOK;
        IItemProvider bread = Items.BREAD;
        IItemProvider injection_empty = ModItems.injection_empty;
        IItemProvider glass_bottle = Items.GLASS_BOTTLE;
        IItemProvider garlic_beacon_core = ModItems.garlic_beacon_core;
        IItemProvider garlic_beacon_core_improved = ModItems.garlic_beacon_core_improved;
        IItemProvider garlic_beacon_normal = ModBlocks.garlic_beacon_normal;
        IItemProvider bucket = Items.BUCKET;
        Tag<Item> planks = ItemTags.PLANKS;
        Tag<Item> glass = Tags.Items.GLASS;
        Tag<Item> glass_pane = Tags.Items.GLASS_PANES;
        Tag<Item> logs = ItemTags.LOGS;
        Tag<Item> diamond = Tags.Items.GEMS_DIAMOND;
        Tag<Item> iron_ingot = Tags.Items.INGOTS_IRON;
        Tag<Item> quartz_block = Tags.Items.STORAGE_BLOCKS_QUARTZ;
        Tag<Item> coal_block = Tags.Items.STORAGE_BLOCKS_COAL;
        Tag<Item> garlic = ModTags.Items.GARLIC;
        Tag<Item> obsidian = Tags.Items.OBSIDIAN;
        Tag<Item> wool = ItemTags.WOOL;
        Tag<Item> stick = Tags.Items.RODS_WOODEN;
        Tag<Item> iron_block = Tags.Items.STORAGE_BLOCKS_IRON;

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.blood_grinder).key('Z', hopper).key('Y', planks).key('D', diamond).key('X',iron_ingot).patternLine(" Z ").patternLine("YDY").patternLine("YXY").addCriterion("has_hopper", this.hasItem(hopper)).build(consumer, modId("general/blood_grinder"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.blood_sieve).key('X',iron_ingot).key('Q',quartz_block).key('Y', planks).key('Z', cauldron).patternLine("XQX").patternLine("YZY").patternLine("YXY").addCriterion("has_cauldron", this.hasItem(cauldron)).build(consumer, modId("general/blood_sieve"));
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.castle_block_dark_brick, 8).addIngredient(castle_block_normal_brick, 8).addIngredient(black_dye).addCriterion("has_castle_brick", this.hasItem(castle_block_normal_brick)).addCriterion("has_black_dye", this.hasItem(black_dye)).build(consumer, modId( "general/castle_block_dark_brick_0"));
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.castle_block_dark_brick, 7).addIngredient(stone_bricks, 7).addIngredient(black_dye).addIngredient(vampire_orchid).addCriterion("has_orchid", this.hasItem(vampire_orchid)).build(consumer, modId("general/castle_block_dark_brick_1"));
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.castle_block_dark_stone, 7).addIngredient(stone, 7).addIngredient(black_dye).addIngredient(vampire_orchid).addCriterion("has_orchid", this.hasItem(vampire_orchid)).build(consumer, modId("general/castle_block_dark_stone"));
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.castle_block_normal_brick, 8).addIngredient(stone_bricks, 8).addIngredient(vampire_orchid).addCriterion("has_orchid", this.hasItem(vampire_orchid)).build(consumer, modId("general/castle_block_normal_brick"));
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.castle_block_purple_brick, 8).addIngredient(castle_block_normal_brick, 8).addIngredient(vampire_orchid).addCriterion("has_orchid", this.hasItem(vampire_orchid)).build(consumer, modId("general/castle_block_purple_brick"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_slab_dark_brick).patternLine("###").key('#',castle_block_dark_brick).addCriterion("has_castle_brick",this.hasItem(castle_block_dark_brick)).build(consumer, modId( "general/castle_slab_dark_brick"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_slab_dark_stone).patternLine("###").key('#',castle_block_dark_stone).addCriterion("has_castle_brick",this.hasItem(castle_block_dark_stone)).build(consumer, modId( "general/castle_slab_dark_stone"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_slab_purple_brick).patternLine("###").key('#',castle_block_purple_brick).addCriterion("has_castle_brick",this.hasItem(castle_block_purple_brick)).build(consumer, modId( "general/castle_slab_purple_brick"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_stairs_dark_brick).patternLine("#  ").patternLine("## ").patternLine("###").key('#',castle_block_dark_brick).addCriterion("has_castle_brick",this.hasItem(castle_block_dark_brick)).build(consumer, modId( "general/castle_stairs_dark_brick"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_stairs_dark_stone).patternLine("#  ").patternLine("## ").patternLine("###").key('#',castle_block_dark_stone).addCriterion("has_castle_brick",this.hasItem(castle_block_dark_stone)).build(consumer,modId("general/castle_stairs_dark_stone"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.castle_stairs_purple_brick).patternLine("#  ").patternLine("## ").patternLine("###").key('#',castle_block_purple_brick).addCriterion("has_castle_brick",this.hasItem(castle_block_purple_brick)).build(consumer,modId("general/castle_stairs_purple_brick"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.church_altar).patternLine(" X ").patternLine("YYY").patternLine(" Y ").key('X',vampire_book).key('Y', planks).addCriterion("has_vampire_book",this.hasItem(planks)).build(consumer,modId("general/church_altar"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.church_altar).patternLine("ZXZ").patternLine("YYY").patternLine(" Y ").key('X',vampire_fang).key('Y', planks).key('Z',book).addCriterion("has_book",this.hasItem(book)).build(consumer,modId("general/church_altar_new"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.fire_place).patternLine(" X ").patternLine("XYX").key('X',logs).key('Y',coal_block).addCriterion("has_logs",this.hasItem(logs)).build(consumer,modId("general/fire_place"));
        ShapelessRecipeBuilder.shapelessRecipe(ModItems.garlic_bread).addIngredient(garlic).addIngredient(bread).addCriterion("has_garlic",this.hasItem(garlic)).addCriterion("has_bread",this.hasItem(bread)).build(consumer,modId("general/garlic_bread"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.injection_empty).patternLine(" X ").patternLine(" X ").patternLine(" Y ").key('X',glass).key('Y',glass_pane).addCriterion("has_glass",this.hasItem(glass)).addCriterion("has_glass_pane",this.hasItem(glass_pane)).build(consumer,modId("general/injection_0"));
        ShapelessRecipeBuilder.shapelessRecipe(ModItems.injection_garlic).addIngredient(injection_empty).addIngredient(garlic).addCriterion("has_injection",this.hasItem(injection_empty)).build(consumer,modId("general/injection_1"));
        ShapelessRecipeBuilder.shapelessRecipe(ModItems.injection_sanguinare).addIngredient(injection_empty).addIngredient(vampire_fang,8).addCriterion("has_injection",this.hasItem(injection_empty)).build(consumer,modId("general/injection_2"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.totem_base).patternLine("XYX").patternLine("XYX").patternLine("ZZZ").key('X',planks).key('Y',obsidian).key('Z',iron_ingot).addCriterion("has_obsidian",this.hasItem(obsidian)).build(consumer,modId("general/totem_base"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.totem_top).patternLine("YXY").patternLine("XZX").patternLine("Y Y").key('X',glass).key('Y',obsidian).key('Z',iron_ingot).addCriterion("has_obsidian",this.hasItem(obsidian)).build(consumer,modId("general/totem_top"));
        shapedWithCondition(ModItems.umbrella).condition(ModRecipes.CONFIG_CONDITION,"umbrella").patternLine("###").patternLine("BAB").patternLine(" A ").key('#',wool).key('A',stick).key('B',vampire_orchid).addCriterion("has_wool",this.hasItem(wool)).build(consumer,modId("general/umbrella"));

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.alchemical_cauldron).patternLine("XZX").patternLine("XXX").patternLine("Y Y").key('X',iron_ingot).key('Y',stone_bricks).key('Z',garlic).addCriterion("has_iron",this.hasItem(iron_ingot)).build(consumer,modId("hunter/alchemical_cauldron"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.blood_potion_table).patternLine("XXX").patternLine("Y Y").patternLine("ZZZ").key('X',glass_bottle).key('Y',planks).key('Z',iron_ingot).addCriterion("has_glass_bottle",this.hasItem(glass_bottle)).build(consumer,modId("hunter/hunter_blood_potion_table"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.garlic_beacon_normal).patternLine("XYX").patternLine("YZY").patternLine("OOO").key('X',planks).key('Y',diamond).key('O',obsidian).key('Z',garlic_beacon_core).addCriterion("has_diamond",this.hasItem(diamond)).build(consumer,modId("hunter/garlic_beacon_normal"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.hunter_table).patternLine("XYW").patternLine("ZZZ").patternLine("Z Z").key('X',vampire_fang).key('Y',book).key('Z',planks).key('W',garlic).addCriterion("has_fang",this.hasItem(vampire_fang)).build(consumer,modId("hunter/hunter_table"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.item_med_chair).patternLine("XYX").patternLine("XXX").patternLine("XZX").key('X',iron_ingot).key('Y',wool).key('Z',glass_bottle).addCriterion("has_iron_ingot",this.hasItem(iron_ingot)).build(consumer,modId("hunter/item_med_chair"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.garlic_beacon_improved).patternLine("XYX").patternLine("YZY").patternLine("OOO").key('X',planks).key('Y',diamond).key('Z',garlic_beacon_core_improved).key('O',obsidian).addCriterion("has_garlic_beacon",this.hasItem(garlic_beacon_normal)).build(consumer,modId("hunter/garlic_beacon_improved"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.stake).patternLine("X").patternLine("Y").patternLine("X").key('X',stick).key('Y',planks).addCriterion("has_sticks",this.hasItem(stick)).build(consumer,modId("hunter/stake"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.weapon_table).patternLine("X  ").patternLine("YYY").patternLine(" Z ").key('X',bucket).key('Y',iron_ingot).key('Z',iron_block).addCriterion("has_iron_ingot",this.hasItem(iron_ingot)).build(consumer,modId("hunter_weapon_table"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.crossbow_arrow_normal).patternLine("X").patternLine("Y").key('X',iron_ingot).key('Y',stick).addCriterion("has_iron_ingot",this.hasItem(iron_ingot)).build(consumer,modId("hunter_crossbow_arrow_normal"));

        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_stone),ModBlocks.castle_block_dark_brick).addCriterion("has_castle_stone",this.hasItem(castle_block_dark_stone)).build(consumer,modId("stonecutting/castle_block_dark_brick_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_stone),ModBlocks.castle_stairs_dark_stone).addCriterion("has_stone",this.hasItem(castle_block_dark_stone)).build(consumer,modId("stonecutting/castle_stairs_dark_stone_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_brick),ModBlocks.castle_stairs_dark_stone).addCriterion("has_stone",this.hasItem(castle_block_dark_brick)).build(consumer,modId("stonecutting/castle_stairs_dark_stone_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_brick),ModBlocks.castle_stairs_dark_brick).addCriterion("has_stone",this.hasItem(castle_block_dark_brick)).build(consumer,modId("stonecutting/castle_stairs_dark_brick_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_stone),ModBlocks.castle_stairs_dark_brick).addCriterion("has_stone",this.hasItem(castle_block_dark_stone)).build(consumer,modId("stonecutting/castle_stairs_dark_brick_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_purple_brick),ModBlocks.castle_stairs_purple_brick).addCriterion("has_stone",this.hasItem(castle_block_purple_brick)).build(consumer,modId("stonecutting/castle_stairs_purple_brick_from_castle_block_purple_brick"));

        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_stone),ModBlocks.castle_slab_dark_stone,2).addCriterion("has_stone",this.hasItem(castle_block_dark_stone)).build(consumer,modId("stonecutting/castle_slaps_dark_stone_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_stone),ModBlocks.castle_slab_dark_brick,2).addCriterion("has_stone",this.hasItem(castle_block_dark_stone)).build(consumer,modId("stonecutting/castle_slaps_dark_brick_from_castle_block_dark_stone"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_brick),ModBlocks.castle_slab_dark_brick,2).addCriterion("has_stone",this.hasItem(castle_block_dark_brick)).build(consumer,modId("stonecutting/castle_slaps_dark_brick_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_dark_brick),ModBlocks.castle_slab_dark_stone,2).addCriterion("has_stone",this.hasItem(castle_block_dark_brick)).build(consumer,modId("stonecutting/castle_slaps_dark_stone_from_castle_block_dark_brick"));
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(castle_block_purple_brick),ModBlocks.castle_slab_purple_brick,2).addCriterion("has_stone",this.hasItem(castle_block_purple_brick)).build(consumer,modId("stonecutting/castle_slaps_purple_brick_from_castle_block_purple_brick"));

    }

    @Nonnull
    @Override
    public String getName() {
        return "Vampirism Recipes";
    }

    private ResourceLocation modId(String path) {
        return new ResourceLocation(REFERENCE.MODID,path);
    }

    private static ConditionalShapedRecipeBuilder shapedWithCondition(IItemProvider itemProvider) {
        return new ConditionalShapedRecipeBuilder(itemProvider);
    }

    private static class ConditionalShapedRecipeBuilder extends ShapedRecipeBuilder {
        private List<Pair<IConditionSerializer<?>,Object>> conditions = Lists.newArrayList();

        public ConditionalShapedRecipeBuilder(IItemProvider resultIn) {
            super(resultIn, 1);
        }

        public ConditionalShapedRecipeBuilder(IItemProvider resultIn, int countIn) {
            super(resultIn, countIn);
        }

        public ConditionalShapedRecipeBuilder condition(IConditionSerializer<?> condition, Object value) {
            conditions.add(new Pair<>(condition,value));
            return this;
        }

        @Override
        public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
            this.validate(id);
            this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
            consumerIn.accept(new ConditionalResult(id, this.result, this.count, this.group == null ? "" : this.group, this.pattern, this.key, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + (this.result.getGroup() != null?this.result.getGroup().getPath(): VampirismMod.creativeTab.getPath()) + "/" + id.getPath()),conditions));
        }

        private class ConditionalResult extends Result {
            private List<Pair<IConditionSerializer<?>,Object>> conditions;

            public ConditionalResult(ResourceLocation idIn, Item resultIn, int countIn, String groupIn, List<String> patternIn, Map<Character, Ingredient> keyIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn, List<Pair<IConditionSerializer<?>,Object>> conditions) {
                super(idIn, resultIn, countIn, groupIn, patternIn, keyIn, advancementBuilderIn, advancementIdIn);
                this.conditions = conditions;
            }

            @Override
            public void serialize(JsonObject json) {
                super.serialize(json);
                if(this.conditions != null) {
                    JsonArray array = new JsonArray();
                    for (Pair<IConditionSerializer<?>,Object> pair: this.conditions) {
                        JsonObject object = new JsonObject();
                        object.addProperty("type",pair.getKey().getID().toString());
                        object.addProperty("option",pair.getValue().toString());
                        array.add(object);
                    }
                    json.add("conditions", array);
                }
            }
        }
    }
}
