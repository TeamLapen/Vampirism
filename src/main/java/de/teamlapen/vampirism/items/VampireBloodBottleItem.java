package de.teamlapen.vampirism.items;


import de.teamlapen.vampirism.VampirismMod;

public class VampireBloodBottleItem extends VampirismItem {
    private final static String regName = "vampire_blood_bottle";

    public VampireBloodBottleItem() {
        super(regName, new Properties().group(VampirismMod.creativeTab));
    }
}
