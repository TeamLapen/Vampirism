package de.teamlapen.vampirism.modcompat.jei.recipes;

import de.teamlapen.vampirism.api.datamaps.IDiffuserFuel;
import de.teamlapen.vampirism.api.datamaps.IFogDiffuserFuel;
import de.teamlapen.vampirism.api.datamaps.IGarlicDiffuserFuel;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FogDiffuserRecipe {

    private final List<ItemStack> input;
    private final IFogDiffuserFuel fuel;

    public FogDiffuserRecipe(ItemStack input, IFogDiffuserFuel fuel) {
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
