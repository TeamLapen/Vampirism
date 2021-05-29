package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VampireClothLegsModel extends VampirismArmorModel {

    private static VampireClothLegsModel instance;

    public static VampireClothLegsModel getInstance() {
        if (instance == null) {
            instance = new VampireClothLegsModel();
        }
        return instance;
    }

    public ModelRenderer rightLeg;
    public ModelRenderer leftLeg;
    public ModelRenderer belt;

    public VampireClothLegsModel() {
        super(32,32);
        this.leftLeg = new ModelRenderer(this, 16, 0);
        this.leftLeg.setRotationPoint(-4F, 12.0F, 0.0F);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, 0.25F, 0.25F);
        this.belt = new ModelRenderer(this, 4, 16);
        this.belt.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.belt.addBox(-4.0F, 7.0F, -2.0F, 8.0F, 5.0F, 4.0F, 0.25F, 0.25F, 0.25F);
        this.rightLeg = new ModelRenderer(this, 0, 0);
        this.rightLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, 0.25F, 0.25F);
    }

    @Override
    protected Iterable<ModelRenderer> getBodyModels() {
        return ImmutableList.of(this.belt);
    }

    @Override
    protected Iterable<ModelRenderer> getLeftLegModels() {
        return ImmutableList.of(this.leftLeg);
    }

    @Override
    protected Iterable<ModelRenderer> getRightLegModels() {
        return ImmutableList.of(this.rightLeg);
    }
}
