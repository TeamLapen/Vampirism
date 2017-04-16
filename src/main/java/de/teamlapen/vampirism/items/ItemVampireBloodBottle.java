package de.teamlapen.vampirism.items;


public class ItemVampireBloodBottle extends VampirismItem {
    private final static String regName = "vampire_blood_bottle";

    public ItemVampireBloodBottle() {
        super(regName);
        this.setMaxStackSize(15);
    }
}
