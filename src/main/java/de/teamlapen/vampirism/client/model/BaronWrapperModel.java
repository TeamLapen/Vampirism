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
    private boolean lady = false;

    public ModelRenderer getBodyPart(VampireBaronEntity entityIn) {
        return entityIn.isLady() ? baroness.body : baron.body;
    }


    @Override
    public void prepareMobModel(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        this.lady = entityIn.isLady();
        EntityModel<VampireBaronEntity> model = lady ? baroness : baron;
        this.copyPropertiesTo(model);
        model.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        EntityModel<VampireBaronEntity> model = lady ? baroness : baron;
        model.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        EntityModel<VampireBaronEntity> model = entityIn.isLady() ? baroness : baron;
        model.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }


}