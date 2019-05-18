package de.teamlapen.vampirism.items;


//TODO 1.13 remove and use instantiate a generic version of VampirismItemBloodFood in ModItems
public class ItemHumanHeartWeak extends VampirismItemBloodFood {
    private final static String name = "weak_human_heart";

    public ItemHumanHeartWeak() {
        super(name, 10, 0.9F);
    }
}
