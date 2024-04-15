package de.teamlapen.vampirism.modcompat.jei.recipes;

import de.teamlapen.vampirism.api.datamaps.IItemBlood;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record GrinderRecipe(ItemStack input, IItemBlood itemBlood) {

    public int blood() {
        return this.itemBlood.blood();
    }
}
