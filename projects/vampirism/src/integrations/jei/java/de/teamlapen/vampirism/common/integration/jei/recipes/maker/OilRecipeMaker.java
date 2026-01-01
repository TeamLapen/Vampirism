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
import de.teamlapen.vampirism.common.world.items.component.AppliedOilContent;
import de.teamlapen.vampirism.common.world.items.component.OilContent;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.List;
import java.util.function.Consumer;

public class OilRecipeMaker {

    public static List<RecipeHolder<CraftingRecipe>> getRecipes(IIngredientManager ingredientManager) {
        return ModRegistries.OILS.listElements()
                .filter(s -> s.value() instanceof IApplicableOil)
                .map(s -> (Holder<IApplicableOil>) (Object) s)
                .flatMap(oil -> BuiltInRegistries.ITEM.stream()
                        .map(Item::getDefaultInstance)
                        .filter(x -> IFaction.contains(x.getOrDefault(FactionDataComponents.FACTION_RESTRICTION, FactionRestriction.ALL).factions(), ModFactions.HUNTER))
                        .filter(item -> oil.value().canBeApplied(item))
                        .mapMulti((ItemStack stack, Consumer<RecipeHolder<CraftingRecipe>> consumer) -> {
                            var originalStack = stack.copy();
                            var oilStack = AppliedOilContent.apply(stack.copy(), oil);

                            var applyRecipe = new ShapelessRecipe("", CraftingBookCategory.EQUIPMENT, oilStack, NonNullList.of(DataComponentIngredient.of(false, stack), DataComponentIngredient.of(false, ModDataComponents.OIL.get(), OilContent.of(oil), ModItems.OIL_BOTTLE)));
                            consumer.accept(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, VIdentifier.mod((oil.unwrapKey().orElseThrow().identifier().toString() + RegUtil.id(stack.getItem())).replace(':', '_'))), applyRecipe));

                            var revertRecipe = new ShapelessRecipe("", CraftingBookCategory.EQUIPMENT, originalStack, NonNullList.of(Ingredient.of(Items.PAPER), DataComponentIngredient.of(false, oilStack)));
                            consumer.accept(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, VIdentifier.mod(("clean_" + oil.unwrapKey().orElseThrow().identifier() + "_from_" + stack.getItemHolder().unwrapKey().orElseThrow().identifier()).replace(':', '_'))), revertRecipe));
                        })).toList();
    }
}
