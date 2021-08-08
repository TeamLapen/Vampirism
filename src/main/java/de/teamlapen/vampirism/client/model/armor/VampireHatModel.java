package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
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

    public ModelPart base;
    public ModelPart top;

    public VampireHatModel() {
        super(64, 32);
        this.base = new ModelPart(this, 16, 0);
        this.base.setPos(0.0F, 0.0F, 0.0F);
        this.base.addBox(-4.5F, -8.4F, -3.5F, 7.0F, 0.4F, 7.0F, 0.25F, 0.25F, 0.25F);
        this.top = new ModelPart(this, 0, 0);
        this.top.setPos(0.0F, 0.0F, 0.0F);
        this.top.addBox(-0.6F, -14.5F, -3.0F, 4.0F, 7.0F, 4.0F, 0.25F, 0.25F, 0.25F);
        this.setRotateAngle(top, -0.22217304763960307F, 0.0F, -0.27750734440919567F);
        this.base.addChild(this.top);
    }

    public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }


    @Override
    protected Iterable<ModelPart> getHeadModels() {
        return ImmutableList.of(this.base);
    }
}
