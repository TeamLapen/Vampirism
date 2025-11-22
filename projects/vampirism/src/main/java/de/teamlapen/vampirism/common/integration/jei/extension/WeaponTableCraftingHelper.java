package de.teamlapen.vampirism.common.integration.jei.extension;

import com.mojang.datafixers.util.Pair;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WeaponTableCraftingHelper implements ICraftingGridHelper {
    public static final WeaponTableCraftingHelper INSTANCE = new WeaponTableCraftingHelper();

    @Override
    public IRecipeSlotBuilder createAndSetOutputs(IRecipeLayoutBuilder builder, SlotDisplay outputs) {
        Minecraft minecraft = Minecraft.getInstance();
        ContextMap contextmap = SlotDisplayContext.fromLevel(Objects.requireNonNull(minecraft.level));
        List<ItemStack> outputStacks = outputs.resolveForStacks(contextmap);
        return createAndSetOutputs(builder, outputStacks);
    }

    @Override
    public void createAndSetIngredientsFromDisplays(IRecipeLayoutBuilder builder, List<SlotDisplay> displays, int width, int height) {
        Minecraft minecraft = Minecraft.getInstance();
        ContextMap contextmap = SlotDisplayContext.fromLevel(Objects.requireNonNull(minecraft.level));

        List<List<ItemStack>> ingredients = displays.stream()
                .map(d -> d.resolveForStacks(contextmap))
                .toList();
        createAndSetInputs(builder, ingredients, width, height);
    }

    @Override
    public <T> IRecipeSlotBuilder createAndSetOutputs(IRecipeLayoutBuilder builder, IIngredientType<T> ingredientType, @Nullable List<@Nullable T> outputs) {
        IRecipeSlotBuilder outputSlot = builder.addOutputSlot(110, 28)
                .setOutputSlotBackground();
        if (outputs != null) {
            outputSlot.addIngredients(ingredientType, outputs);
        }
        return outputSlot;
    }

    @Override
    public <T> List<IRecipeSlotBuilder> createAndSetInputs(IRecipeLayoutBuilder builder, IIngredientType<T> ingredientType, List<@Nullable List<@Nullable T>> inputs, int width, int height) {
        List<IRecipeSlotBuilder> inputSlots = createInputSlots(builder, width, height);
        setInputs(inputSlots, ingredientType, inputs, width, height);
        return inputSlots;
    }

    @Override
    public <T> void setInputs(List<IRecipeSlotBuilder> slotBuilders, IIngredientType<T> ingredientType, List<@Nullable List<@Nullable T>> inputs, int width, int height) {
        if (width <= 0 || height <= 0) {
            width = height = getShapelessSize(inputs.size());
        }
        if (slotBuilders.size() < width * height) {
            throw new IllegalArgumentException(String.format("There are not enough slots (%s) to hold a recipe of this size. (%sx%s)", slotBuilders.size(), width, height));
        }

        for (int i = 0; i < inputs.size(); i++) {
            int index = getCraftingIndex(i, width, height);
            IRecipeSlotBuilder slot = slotBuilders.get(index);

            @Nullable List<@Nullable T> ingredients = inputs.get(i);
            if (ingredients != null) {
                slot.addIngredients(ingredientType, ingredients);
            }
        }
    }

    private static List<IRecipeSlotBuilder> createInputSlots(IRecipeLayoutBuilder builder, int width, int height) {
        if (width <= 0 || height <= 0) {
            builder.setShapeless();
        }

        List<IRecipeSlotBuilder> inputSlots = new ArrayList<>();
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                IRecipeSlotBuilder slot = builder.addInputSlot(x * 18 + 1, y * 18 + 1)
                        .setStandardSlotBackground();
                inputSlots.add(slot);
            }
        }
        return inputSlots;
    }

    protected int getShapelessSize(int total) {
        if (total > 9) {
            return 4;
        } if (total > 4) {
            return 3;
        } else if (total > 1) {
            return 2;
        } else {
            return 1;
        }
    }

    protected int getCraftingIndex(int i, int width, int height) {
        int gridSize = 4;

        // recipe-local coordinates
        int rx = i % width;
        int ry = i / width;

        // center the recipe in the 4×4 grid
        int offsetX = (gridSize - width) / 2;
        int offsetY = (gridSize - height) / 2;

        int gx = rx + offsetX;
        int gy = ry + offsetY;

        return gy * gridSize + gx;
    }

    @Override
    public List<IRecipeSlotBuilder> createAndSetNamedIngredients(IRecipeLayoutBuilder builder, List<Pair<String, Ingredient>> namedIngredients, int width, int height) {
        return List.of();
    }

    @Override
    public void createAndSetIngredients(IRecipeLayoutBuilder builder, List<Ingredient> ingredients, int width, int height) {

    }

    @Override
    public <T> List<IRecipeSlotBuilder> createAndSetNamedInputs(IRecipeLayoutBuilder builder, IIngredientType<T> ingredientType, List<@Nullable Pair<String, List<@Nullable T>>> namedInputs, int width, int height) {
        return List.of();
    }
}
