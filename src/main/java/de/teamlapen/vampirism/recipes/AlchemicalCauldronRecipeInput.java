package de.teamlapen.vampirism.recipes;

import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record AlchemicalCauldronRecipeInput(ItemStack ingredient, ItemStack fluid, Optional<ISkillHandler<?>> skills, ITestableRecipeInput.TestType testType) implements RecipeInput, ITestableRecipeInput {

    public AlchemicalCauldronRecipeInput(ItemStack fluid, ItemStack ingredient, Optional<ISkillHandler<?>> skills) {
        this(fluid, ingredient, skills, ITestableRecipeInput.TestType.BOTH);
    }

    public AlchemicalCauldronRecipeInput(ItemStack fluid, ItemStack ingredient, ITestableRecipeInput.TestType testType) {
        this(fluid, ingredient, Optional.empty(), testType);
    }

    public AlchemicalCauldronRecipeInput(ItemStack fluid, ItemStack ingredient) {
        this(fluid, ingredient, Optional.empty(), TestType.BOTH);
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> this.ingredient;
            case 1 -> this.fluid;
            default -> throw new IllegalArgumentException("Recipe does not contain slot " + index);
        };
    }

    @Override
    public int size() {
        return 2;
    }
}
