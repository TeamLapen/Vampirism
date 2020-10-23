package de.teamlapen.vampirism.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class HunterHatModel<T extends LivingEntity> extends BipedModel<T> {
    public static final HunterHatModel hat0 = new HunterHatModel(0);
    public static final HunterHatModel hat1 = new HunterHatModel(1);
    private ModelRenderer hatTop;
    private ModelRenderer hatRim;


    public HunterHatModel(int type) {
        super(0.0F, 0.0F, 64, 64);
        if (type == 1) {
            hatTop = new ModelRenderer(this, 0, 31);
            hatTop.addBox(-4F, -14F, -4F, 8, 5, 8);
            hatTop.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
            hatTop.setTextureSize(128, 64);
            hatTop.mirror = true;

            hatRim = new ModelRenderer(this, 0, 35);
            hatRim.addBox(-6F, -9F, -6F, 12, 1, 12);
            hatRim.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
            hatRim.setTextureSize(128, 64);
            hatRim.mirror = true;
        } else if (type == 0) {
            hatTop = new ModelRenderer(this, 0, 31);
            hatTop.addBox(-4F, -12F, -4F, 8, 3, 8);
            hatTop.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
            hatTop.setTextureSize(128, 64);
            hatTop.mirror = true;

            hatRim = new ModelRenderer(this, 0, 31);
            hatRim.addBox(-8F, -9F, -8F, 16, 1, 16);
            hatRim.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
            hatRim.setTextureSize(128, 64);
            hatRim.mirror = true;
        }

    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) { //setRotationAngles
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        hatRim.copyModelAngles(super.bipedHead);
        hatTop.copyModelAngles(super.bipedHead);
    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts() {
        return Iterables.concat(super.getHeadParts(), ImmutableList.of(hatTop, hatRim));
    }


    @Override
    public void setVisible(boolean invisible) {
        super.setVisible(false);
        hatRim.showModel = true;
        hatTop.showModel = true;
    }
}
