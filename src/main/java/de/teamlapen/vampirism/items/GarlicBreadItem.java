package de.teamlapen.vampirism.items;

import net.minecraft.item.Food;
import net.minecraft.item.ItemGroup;

public class GarlicBreadItem extends VampirismItem {
    private static final String regName = "garlic_bread";

    public GarlicBreadItem() {
        super(regName, new Properties().food((new Food.Builder()).hunger(6).saturation(0.7F).build()).group(ItemGroup.FOOD));
    }
}
