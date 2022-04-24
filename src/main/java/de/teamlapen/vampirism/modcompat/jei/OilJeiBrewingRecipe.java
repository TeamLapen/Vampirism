package de.teamlapen.vampirism.modcompat.jei;


import com.google.common.base.Objects;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.util.OilUtils;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * copied and adapted from {@link mezz.jei.plugins.vanilla.brewing.JeiBrewingRecipe}
 */
public class OilJeiBrewingRecipe implements IJeiBrewingRecipe {

    private final List<ItemStack> ingredients;
    private final List<ItemStack> oilInputs;
    private final ItemStack oilOutput;
    private final List<List<ItemStack>> inputs;
    private final int hashCode;

    public OilJeiBrewingRecipe(List<ItemStack> ingredients, List<ItemStack> oilInputs, ItemStack oilOutput) {
        this.ingredients = ingredients;
        this.oilInputs = oilInputs;
        this.oilOutput = oilOutput;

        this.inputs = new ArrayList<>();
        this.inputs.add(oilInputs);
        this.inputs.add(oilInputs);
        this.inputs.add(oilInputs);
        this.inputs.add(ingredients);

        ItemStack firstIngredient = ingredients.get(0);
        ItemStack firstInput = oilInputs.get(0);

        this.hashCode = Objects.hashCode(firstInput.getItem(), OilUtils.getOil(firstInput),
                oilOutput.getItem(), OilUtils.getOil(oilOutput),
                firstIngredient.getItem());
    }

    public List<List<ItemStack>> getInputs() {
        return inputs;
    }

    public ItemStack getOilOutput() {
        return oilOutput;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OilJeiBrewingRecipe)) {
            return false;
        }
        OilJeiBrewingRecipe other = (OilJeiBrewingRecipe) obj;

        for (int i = 0; i < oilInputs.size(); i++) {
            ItemStack potionInput = oilInputs.get(i);
            ItemStack otherPotionInput = other.oilInputs.get(i);
            if (!areOilsEqual(potionInput, otherPotionInput)) {
                return false;
            }
        }

        if (!areOilsEqual(other.oilOutput, oilOutput)) {
            return false;
        }

        if (ingredients.size() != other.ingredients.size()) {
            return false;
        }

        for (int i = 0; i < ingredients.size(); i++) {
            if (!ItemStack.tagMatches(ingredients.get(i), other.ingredients.get(i))) {
                return false;
            }
        }

        return true;
    }

    private static boolean areOilsEqual(ItemStack oilItem1, ItemStack oilItem2) {
        if (oilItem1.getItem() != oilItem2.getItem()){
            return false;
        }
        IOil oil1 = OilUtils.getOil(oilItem1);
        IOil oil2 = OilUtils.getOil(oilItem2);
        ResourceLocation key1 = oil1.getRegistryName();
        ResourceLocation key2 = oil2.getRegistryName();
        return java.util.Objects.equals(key1, key2);
    }

    @Override
    public int getBrewingSteps() {
        return 0;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        IOil inputType = OilUtils.getOil(oilInputs.get(0));
        IOil outputType = OilUtils.getOil(oilOutput);
        return ingredients + " + [" + oilInputs.get(0).getItem() + " " + inputType.getName("") + "] = [" + oilOutput + " " + outputType.getName("") + "]";
    }
}
