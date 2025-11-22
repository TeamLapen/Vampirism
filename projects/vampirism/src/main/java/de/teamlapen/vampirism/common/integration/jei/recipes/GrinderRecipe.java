package de.teamlapen.vampirism.common.integration.jei.recipes;

import de.teamlapen.vampirism.api.datamaps.IItemBlood;
import net.minecraft.world.item.ItemStack;

public record GrinderRecipe(ItemStack input, IItemBlood itemBlood) {

    public int blood() {
        return this.itemBlood.blood();
    }
}
