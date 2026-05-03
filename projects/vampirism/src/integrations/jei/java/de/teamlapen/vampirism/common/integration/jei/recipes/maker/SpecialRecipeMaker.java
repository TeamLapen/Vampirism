package de.teamlapen.vampirism.common.integration.jei.recipes.maker;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.items.oil.IApplicableOil;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModRegistries;
import de.teamlapen.vampirism.common.util.RegUtil;
import de.teamlapen.vampirism.common.world.items.BloodBottleItem;
import de.teamlapen.vampirism.common.world.items.SerumInjectionItem;
import de.teamlapen.vampirism.common.world.items.component.AppliedOilContent;
import de.teamlapen.vampirism.common.world.items.component.OilContent;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SpecialRecipeMaker {

    public static List<RecipeHolder<CraftingRecipe>> getAllCraftingRecipes() {
        return Stream.of(
                makeOilRecipes().stream(),
                makeBloodBottleFromSyringesRecipes().stream(),
                makeSerumFromPotionRecipes().stream()
        ).flatMap(s -> s).toList();
    }

    public static List<RecipeHolder<CraftingRecipe>> makeOilRecipes() {
        return ModRegistries.OILS.listElements()
                .filter(s -> s.value() instanceof IApplicableOil)
                .map(s -> (Holder<IApplicableOil>) (Object) s)
                .flatMap(oil -> BuiltInRegistries.ITEM.listElements()
                        .filter(x -> !x.is(Items.AIR.builtInRegistryHolder()))
                        .map(ItemStackTemplate::new)
                        .filter(x -> IFaction.contains(x.getOrDefault(FactionDataComponents.FACTION_RESTRICTION, FactionRestriction.ALL).factions(), ModFactions.HUNTER))
                        .filter(item -> oil.value().canBeApplied(item))
                        .mapMulti((ItemStackTemplate stack, Consumer<RecipeHolder<CraftingRecipe>> consumer) -> {
                            var oilTemplate = new ItemStackTemplate(stack.item(), stack.count(), DataComponentPatch.builder()
                                    .set(ModDataComponents.OIL.get(), OilContent.of(oil))
                                    .build());

                            var applyRecipe = new ShapelessRecipe(new Recipe.CommonInfo(false), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.EQUIPMENT, ""), oilTemplate, NonNullList.of(DataComponentIngredient.of(false, stack), DataComponentIngredient.of(false, ModDataComponents.OIL.get(), OilContent.of(oil), ModItems.OIL_BOTTLE)));
                            consumer.accept(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, VIdentifier.mod((oil.unwrapKey().orElseThrow().identifier().toString() + RegUtil.id(stack.item())).replace(':', '_'))), applyRecipe));

                            var revertRecipe = new ShapelessRecipe(new Recipe.CommonInfo(false), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.EQUIPMENT, ""), stack, NonNullList.of(Ingredient.of(Items.PAPER), DataComponentIngredient.of(false, oilTemplate)));
                            consumer.accept(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, VIdentifier.mod(("clean_" + oil.unwrapKey().orElseThrow().identifier() + "_from_" + stack.item().unwrapKey().orElseThrow().identifier()).replace(':', '_'))), revertRecipe));
                        })).toList();
    }

    private static List<RecipeHolder<CraftingRecipe>> makeBloodBottleFromSyringesRecipes() {
        return IntStream.rangeClosed(1, BloodBottleItem.AMOUNT)
                .mapToObj(syringes -> {
                    NonNullList<Ingredient> ingredients = NonNullList.create();
                    ingredients.add(Ingredient.of(Items.GLASS_BOTTLE));
                    IntStream.range(0, syringes).forEach(value -> ingredients.add(Ingredient.of(ModItems.SYRINGE_BLOOD.get())));

                    var result = BloodBottleItem.template(syringes);
                    ShapelessRecipe recipe = new ShapelessRecipe(new Recipe.CommonInfo(false), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""), result, ingredients);
                    return new RecipeHolder<CraftingRecipe>(ResourceKey.create(Registries.RECIPE, VIdentifier.mod("fill_bottle_from_syringe_" + syringes)), recipe);
                })
                .toList();
    }

    public static List<RecipeHolder<CraftingRecipe>> makeSerumFromPotionRecipes() {
        return BuiltInRegistries.POTION.listElements()
                .filter(potion -> !SerumInjectionItem.isBlockedPotion(potion))
                .map(potion -> {
                    ItemStack potionStack = new ItemStack(Items.POTION);
                    potionStack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));

                    var result = new ItemStackTemplate(ModItems.SERUM_INJECTION.get(), 4, DataComponentPatch.builder()
                            .set(DataComponents.POTION_CONTENTS, new PotionContents(potion))
                            .build());

                    List<Ingredient> ingredients = new ArrayList<>(Collections.nCopies(4, Ingredient.of(ModItems.SYRINGE_EMPTY.get())));
                    ingredients.add(DataComponentIngredient.of(false, potionStack));

                    ShapelessRecipe recipe = new ShapelessRecipe(new Recipe.CommonInfo(false), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""), result, ingredients);
                    return new RecipeHolder<CraftingRecipe>(ResourceKey.create(Registries.RECIPE, VIdentifier.mod("serum_from_potion_" + potion.unwrapKey().orElseThrow().identifier().getPath())), recipe);
                })
                .toList();
    }
}
