package de.teamlapen.vampirism.client.model;

/**
 * TODO
 * Placeholder
 */
public class DummyClothingModel extends VampirismArmorModel {

    private static DummyClothingModel itemModel;

    public static DummyClothingModel getArmorModel(){
        if(itemModel ==null){
            itemModel =new DummyClothingModel();
        }
        return itemModel;
    }

    protected DummyClothingModel() {
        super(64,64);
    }
}
