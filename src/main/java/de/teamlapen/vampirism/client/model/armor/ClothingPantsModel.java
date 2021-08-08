package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClothingPantsModel extends VampirismArmorModel {

    private static ClothingPantsModel instance;

    public static ClothingPantsModel getInstance() {
        if (instance == null) {
            instance = new ClothingPantsModel();
        }
        return instance;
    }

    public ModelPart rightLeg;
    public ModelPart leftLeg;
    public ModelPart belt;

    public ClothingPantsModel() {
        super(32, 32);
        this.leftLeg = new ModelPart(this, 16, 0);
        this.leftLeg.setPos(-4F, 12.0F, 0.0F);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, 0.25F, 0.25F);
        this.belt = new ModelPart(this, 4, 16);
        this.belt.setPos(0.0F, 0.0F, 0.0F);
        this.belt.addBox(-4.0F, 7.0F, -2.0F, 8.0F, 5.0F, 4.0F, 0.25F, 0.25F, 0.25F);
        this.rightLeg = new ModelPart(this, 0, 0);
        this.rightLeg.setPos(1.9F, 12.0F, 0.0F);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, 0.25F, 0.25F);
    }

    @Override
    protected Iterable<ModelPart> getBodyModels() {
        return ImmutableList.of(this.belt);
    }

    @Override
    protected Iterable<ModelPart> getLeftLegModels() {
        return ImmutableList.of(this.leftLeg);
    }

    @Override
    protected Iterable<ModelPart> getRightLegModels() {
        return ImmutableList.of(this.rightLeg);
    }
}
