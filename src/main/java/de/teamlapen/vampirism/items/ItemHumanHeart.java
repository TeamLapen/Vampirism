package de.teamlapen.vampirism.items;


//TODO 1.13 remove and use instantiate a generic version of VampirismItemBloodFood in ModItems
public class ItemHumanHeart extends VampirismItemBloodFood {

    private final static String name = "human_heart";

    public ItemHumanHeart() {
        super(name, 20, 1.2F);
    }
}
