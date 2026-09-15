package de.teamlapen.vampirism.common.integration.jei.recipes.maker;

import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.items.oil.IApplicableOil;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModRegistries;
import de.teamlapen.vampirism.common.integration.jei.recipes.OilDisplayRecipe;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.*;
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
        List<Holder<IApplicableOil>> oils = ModRegistries.OILS.listElements()
                .filter(oil -> oil.value() instanceof IApplicableOil)
                .map(oil -> (Holder<IApplicableOil>) (Object) oil)
                .toList();

        List<ItemStackTemplate> supported = BuiltInRegistries.ITEM.listElements()
                .filter(item -> !item.is(BuiltInRegistries.ITEM.wrapAsHolder(Items.AIR)))
                .map(ItemStackTemplate::new)
                .filter(stack -> stack.getOrDefault(FactionDataComponents.FACTION_RESTRICTION, FactionRestriction.ALL).supports(ModFactions.HUNTER))
                .filter(stack -> oils.stream().anyMatch(oil -> oil.value().canBeApplied(stack)))
                .toList();

        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>(supported.size() * 2);

        for (ItemStackTemplate stack : supported) {
            List<Holder<IApplicableOil>> supportedOils = oils.stream().filter(oil -> oil.value().canBeApplied(stack)).toList();
            ItemStack toolStack = stack.create();

            List<ItemStackTemplate> bottles = supportedOils.stream()
                    .map(oil -> new ItemStackTemplate(ModItems.OIL_BOTTLE.get(), 1, DataComponentPatch.builder()
                            .set(ModDataComponents.OIL.get(), OilContent.of(oil))
                            .build()))
                    .toList();
            List<ItemStackTemplate> oiled = supportedOils.stream()
                    .map(oil -> new ItemStackTemplate(stack.item(), stack.count(), DataComponentPatch.builder()
                            .set(ModDataComponents.APPLIED_OIL.get(), new AppliedOilContent(oil, oil.value().getMaxDuration(toolStack)))
                            .build()))
                    .toList();

            SlotDisplay toolDisplay = new SlotDisplay.ItemStackSlotDisplay(stack);
            String toolId = RegUtil.id(stack.item()).toString().replace(':', '_');

            recipes.add(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, VIdentifier.mod("apply_oil_to_" + toolId)),
                    new OilDisplayRecipe(oiled.getFirst(),
                            List.of(DataComponentIngredient.of(false, stack), Ingredient.of(ModItems.OIL_BOTTLE)),
                            List.of(toolDisplay, OilDisplayRecipe.cycle(bottles)),
                            OilDisplayRecipe.cycle(oiled))));

            recipes.add(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, VIdentifier.mod("clean_oil_from_" + toolId)),
                    new OilDisplayRecipe(stack,
                            List.of(Ingredient.of(Items.PAPER), DataComponentIngredient.of(false, stack)),
                            List.of(new SlotDisplay.ItemSlotDisplay(Items.PAPER.builtInRegistryHolder()), OilDisplayRecipe.cycle(oiled)),
                            toolDisplay)));
        }
        return recipes;
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
