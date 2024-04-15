package de.teamlapen.vampirism.modcompat.jei.recipes;

import de.teamlapen.vampirism.api.datamaps.IGarlicDiffuserFuel;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class GarlicDiffuserRecipe {

    private final List<ItemStack> input;
    private final IGarlicDiffuserFuel fuel;

    public GarlicDiffuserRecipe(ItemStack input, IGarlicDiffuserFuel fuel) {
        this.input = List.of(input);
        this.fuel = fuel;
    }

    public List<ItemStack> getInputs() {
        return this.input;
    }

    public int getBurnTime() {
        return this.fuel.burnDuration();
    }
}
