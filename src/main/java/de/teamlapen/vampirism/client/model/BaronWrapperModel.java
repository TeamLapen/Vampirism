package de.teamlapen.vampirism.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;


/**
 * Wraps the male and female model into a single model class/instance as the {@link EntityRenderer} needs a single model
 */
public class BaronWrapperModel extends EntityModel<VampireBaronEntity> {
    private final BaronModel baron = new BaronModel();
    private final BaronessModel baroness = new BaronessModel();
    private boolean lady=false;

    public ModelRenderer getBodyPart(VampireBaronEntity entityIn) {
        return entityIn.isLady() ? baroness.body : baron.body;
    }


    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        EntityModel<VampireBaronEntity> model = lady ? baroness : baron;
        model.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,red,green,blue,alpha);
    }

    @Override
    public void setRotationAngles(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        EntityModel<VampireBaronEntity> model = entityIn.isLady() ? baroness : baron;
        model.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public void setLivingAnimations(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        this.lady = entityIn.isLady();
        EntityModel<VampireBaronEntity> model = lady ? baroness : baron;
        this.copyModelAttributesTo(model);
        model.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
    }


}