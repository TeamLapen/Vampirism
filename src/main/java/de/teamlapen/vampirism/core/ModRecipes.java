package de.teamlapen.vampirism.core;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.datamaps.EntityExistsCondition;
import de.teamlapen.vampirism.recipes.*;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

/**
 * Handles all recipe registrations and reference.
 */
@SuppressWarnings("unused")
public class ModRecipes {
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, REFERENCE.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, REFERENCE.MODID);
    private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, REFERENCE.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<IWeaponTableRecipe>> WEAPONTABLE_CRAFTING_TYPE = RECIPE_TYPES.register("weapontable_crafting", () -> new RecipeType<>() {
        public @NotNull String toString() {
            return "weapontable_crafting";
        }
    });
    public static final DeferredHolder<RecipeType<?>, RecipeType<AlchemicalCauldronRecipe>> ALCHEMICAL_CAULDRON_TYPE = RECIPE_TYPES.register("alchemical_cauldron", () -> new RecipeType<>() {
        public @NotNull String toString() {
            return "alchemical_cauldron";
        }
    });
    public static final DeferredHolder<RecipeType<?>, RecipeType<AlchemyTableRecipe>> ALCHEMICAL_TABLE_TYPE = RECIPE_TYPES.register("alchemical_table", () -> new RecipeType<>() {
        public @NotNull String toString() {
            return "alchemical_table";
        }
    });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedWeaponTableRecipe>> SHAPED_CRAFTING_WEAPONTABLE = RECIPE_SERIALIZERS.register("shaped_crafting_weapontable", ShapedWeaponTableRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapelessWeaponTableRecipe>> SHAPELESS_CRAFTING_WEAPONTABLE = RECIPE_SERIALIZERS.register("shapeless_crafting_weapontable", ShapelessWeaponTableRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedRecipe>> REPAIR_IITEMWITHTIER = RECIPE_SERIALIZERS.register("repair_iitemwithtier", ShapedItemWithTierRepair.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlchemicalCauldronRecipe>> ALCHEMICAL_CAULDRON = RECIPE_SERIALIZERS.register("alchemical_cauldron", AlchemicalCauldronRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlchemyTableRecipe>> ALCHEMICAL_TABLE = RECIPE_SERIALIZERS.register("alchemical_table", AlchemyTableRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ApplicableOilRecipe>> APPLICABLE_OIL = RECIPE_SERIALIZERS.register("applicable_oil", () -> new SimpleCraftingRecipeSerializer<>(ApplicableOilRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CleanOilRecipe>> CLEAN_OIL = RECIPE_SERIALIZERS.register("clean_oil", () -> new SimpleCraftingRecipeSerializer<>(CleanOilRecipe::new));

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<ConfigCondition>> CONFIG_CONDITION = CONDITION_CODECS.register("config", () -> ConfigCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<EntityExistsCondition>> ENTITY_EXISTS_CONDITION = CONDITION_CODECS.register("entity_exists", () -> EntityExistsCondition.CODEC);

    static void register(@NotNull IEventBus bus) {
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        CONDITION_CODECS.register(bus);
    }

    static void registerCategories(RegisterRecipeBookCategoriesEvent event) {
        event.registerRecipeCategoryFinder(ALCHEMICAL_CAULDRON_TYPE.get(), holder -> RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(ALCHEMICAL_TABLE_TYPE.get(), holder -> RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(WEAPONTABLE_CRAFTING_TYPE.get(), holder -> RecipeBookCategories.UNKNOWN);
    }

}
