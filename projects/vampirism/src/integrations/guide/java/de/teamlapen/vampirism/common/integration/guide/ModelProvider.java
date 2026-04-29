package de.teamlapen.vampirism.common.integration.guide;

import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.util.ModelHelper;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.core.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Generate the item and model file for the GuideBook
 */
@EventBusSubscriber(modid = REFERENCE.MODID, value= Dist.CLIENT)
public class ModelProvider {

    @SuppressWarnings("UnreachableCode")
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.getGenerator().addProvider(true, new GuideAPIModelProvider(event.getGenerator().getPackOutput()));
        event.getGenerator().addProvider(true, new GuideAPIRecipeProvider.Runner( event.getGenerator().getPackOutput(), event.getLookupProvider()));
    }

    private static class GuideAPIModelProvider extends net.minecraft.client.data.models.ModelProvider {

        public GuideAPIModelProvider(PackOutput output) {
            super(output, GuideMod.ID);
        }

        @Override
        protected @NotNull Stream<? extends Holder<Item>> getKnownItems() {
            return ModelHelper.getBookItems(GuideBook.guideBook);
        }

        @Override
        protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
//            ModelHelper.generateDefaultGuidebookModel(itemModels, GuideBook.guideBook); //We don't use the default model
            Identifier baseTexture = Identifier.fromNamespaceAndPath(REFERENCE.MODID, "item/guidebook");
            Item item = GuideAPI.getItemForBook(GuideBook.guideBook).value();
            Identifier model = ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.decorateItemModelLocation(item.toString()), TextureMapping.layer0(baseTexture), itemModels.modelOutput);
            itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
        }

    }

    private static class GuideAPIRecipeProvider extends RecipeProvider {

        protected GuideAPIRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            super(registries, output);
        }

        @Override
        protected void buildRecipes() {
            shapeless(RecipeCategory.MISC, GuideAPI.getItemForBook(GuideBook.guideBook).value())
                    .requires(ModItems.VAMPIRE_FANG)
                    .requires(Items.BOOK)
                    .unlockedBy("has_fang", has(ModItems.VAMPIRE_FANG))
                    .save(output.withConditions(new ModLoadedCondition(REFERENCE.GUIDEAPI_MODID)));
        }

        public static class Runner extends RecipeProvider.Runner {

            public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
                super(output, lookupProvider);
            }

            @Override
            protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
                return new GuideAPIRecipeProvider(provider, output);
            }

            @Override
            public String getName() {
                return "Test Guidebook Recipes";
            }
        }
    }
}
