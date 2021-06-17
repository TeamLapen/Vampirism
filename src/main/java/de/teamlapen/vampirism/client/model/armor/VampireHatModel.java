package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VampireHatModel extends VampirismArmorModel {

    private static VampireHatModel instance;

    public static VampireHatModel getInstance() {
        if (instance == null) {
            instance = new VampireHatModel();
        }
        return instance;
    }

    public ModelRenderer base;
    public ModelRenderer top;

    public VampireHatModel() {
        super(64, 32);
        this.base = new ModelRenderer(this, 16, 0);
        this.base.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.base.addBox(-4.5F, -8.4F, -3.5F, 7.0F, 0.4F, 7.0F, 0.25F, 0.25F, 0.25F);
        this.top = new ModelRenderer(this, 0, 0);
        this.top.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.top.addBox(-0.6F, -14.5F, -3.0F, 4.0F, 7.0F, 4.0F, 0.25F, 0.25F, 0.25F);
        this.setRotateAngle(top, -0.22217304763960307F, 0.0F, -0.27750734440919567F);
        this.base.addChild(this.top);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }


    @Override
    protected Iterable<ModelRenderer> getHeadModels() {
        return ImmutableList.of(this.base);
    }
}
