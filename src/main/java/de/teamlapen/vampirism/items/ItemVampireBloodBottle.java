package de.teamlapen.vampirism.items;


public class ItemVampireBloodBottle extends VampirismItem {
    private final static String regName = "vampireBloodBottle";

    public ItemVampireBloodBottle() {
        super(regName);
        this.setMaxStackSize(3);
    }
}
