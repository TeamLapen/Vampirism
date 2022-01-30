package de.teamlapen.vampirism.client.model.armor;

import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

/**
 * TODO
 * Placeholder
 */
public class DummyClothingModel extends VampirismArmorModel {

    private static DummyClothingModel itemModel;

    public static DummyClothingModel getAdjustedInstance(HumanoidModel<?> wearerModel) {
        if (itemModel == null) {
            itemModel = new DummyClothingModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEntitiesRender.GENERIC_BIPED));
        }
        itemModel.copyFromHumanoid(wearerModel);
        return itemModel;
    }

    protected DummyClothingModel(ModelPart part) {
        super();
    }
}
