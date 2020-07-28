package de.teamlapen.vampirism.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.client.model.WingModel;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiFunction;
import java.util.function.Predicate;


public class LayerWings<T extends LivingEntity, Q extends EntityModel<T>> extends LayerRenderer<T, Q> {

    private final WingModel<T> model = new WingModel<>();
    private final Predicate<T> predicateRender;
    private final BiFunction<T, Q, RendererModel> bodyPartFunction;
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/wings.png");

    /**
     * @param predicateRender  Decides if the layer is rendered
     * @param bodyPartFunction Should return the main body part. The returned renderermodel is used to adjust the wing rotation
     */
    public LayerWings(IEntityRenderer<T, Q> entityRendererIn, Predicate<T> predicateRender, BiFunction<T, Q, RendererModel> bodyPartFunction) {
        super(entityRendererIn);
        this.predicateRender = predicateRender;
        this.bodyPartFunction = bodyPartFunction;
    }

    @Override
    public void render(T entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!entityIn.isInvisible() && predicateRender.test(entityIn)) {
            bindTexture(texture);
            this.getEntityModel().setModelAttributes(model);
            this.model.copyRotationFromBody(bodyPartFunction.apply(entityIn, this.getEntityModel()));
            this.model.setLivingAnimations(entityIn, ageInTicks);
            float s = 1f;
            if (entityIn instanceof VampireBaronEntity) {
                s = ((VampireBaronEntity) entityIn).getEnragedProgress();
            }
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0f, 0, 0.02f);
            GlStateManager.scalef(s, s, s);
            this.model.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
