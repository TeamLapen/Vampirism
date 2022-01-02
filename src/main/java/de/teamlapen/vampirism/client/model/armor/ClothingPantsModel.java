package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
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

    public ModelRenderer rightLegOverlay;
    public ModelRenderer leftLegOverlay;
    public ModelRenderer belt;

    public ClothingPantsModel() {
        super(32, 32);
        this.leftLegOverlay = new ModelRenderer(this, 16, 0);
        this.leftLegOverlay.setPos(-4F, 12.0F, 0.0F);
        this.leftLegOverlay.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, 0.25F, 0.25F);
        this.belt = new ModelRenderer(this, 4, 16);
        this.belt.setPos(0.0F, 0.0F, 0.0F);
        this.belt.addBox(-4.0F, 7.0F, -2.0F, 8.0F, 5.0F, 4.0F, 0.25F, 0.25F, 0.25F);
        this.rightLegOverlay = new ModelRenderer(this, 0, 0);
        this.rightLegOverlay.setPos(1.9F, 12.0F, 0.0F);
        this.rightLegOverlay.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, 0.25F, 0.25F);
        getBodyModels().forEach(this.body::addChild);
        getRightLegModels().forEach(super.rightLeg::addChild);
        getRightLegModels().forEach(super.leftLeg::addChild);
    }

    @Override
    protected Iterable<ModelRenderer> getBodyModels() {
        return ImmutableList.of(this.belt);
    }

    @Override
    protected Iterable<ModelRenderer> getLeftLegModels() {
        return ImmutableList.of(this.leftLegOverlay);
    }

    @Override
    protected Iterable<ModelRenderer> getRightLegModels() {
        return ImmutableList.of(this.rightLegOverlay);
    }
}
