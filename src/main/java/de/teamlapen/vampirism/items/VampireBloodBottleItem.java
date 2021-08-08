package de.teamlapen.vampirism.items;


import de.teamlapen.vampirism.VampirismMod;

import net.minecraft.world.item.Item.Properties;

public class VampireBloodBottleItem extends VampirismItem {
    private final static String regName = "vampire_blood_bottle";

    public VampireBloodBottleItem() {
        super(regName, new Properties().tab(VampirismMod.creativeTab));
    }
}
