package de.teamlapen.vampirism.client.model;

import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;


/**
 * Wraps the male and female model into a single model class/instance as the {@link EntityRenderer} needs a single model
 */
public class BaronWrapperModel extends EntityModel<VampireBaronEntity> {
    private final BaronModel baron = new BaronModel();
    private final BaronessModel baroness = new BaronessModel();

    public RendererModel getBodyPart(VampireBaronEntity entityIn) {
        return entityIn.isLady() ? baroness.body : baron.body;
    }

    @Override
    public void render(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        EntityModel<VampireBaronEntity> model = entityIn.isLady() ? baroness : baron;
        model.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Override
    public void setLivingAnimations(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        EntityModel<VampireBaronEntity> model = entityIn.isLady() ? baroness : baron;
        this.setModelAttributes(model);
        model.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
    }

    @Override
    public void setRotationAngles(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        EntityModel<VampireBaronEntity> model = entityIn.isLady() ? baroness : baron;
        model.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
    }
}
