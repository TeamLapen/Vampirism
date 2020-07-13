package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.model.WingModel;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiFunction;
import java.util.function.Predicate;


public class WingsLayer<T extends LivingEntity, Q extends EntityModel<T>> extends LayerRenderer<T, Q> {

    private final WingModel<T> model = new WingModel<>();
    private final Predicate<T> predicateRender;
    private final BiFunction<T, Q, ModelRenderer> bodyPartFunction;
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/wings.png");

    /**
     * @param predicateRender  Decides if the layer is rendered
     * @param bodyPartFunction Should return the main body part. The returned ModelRenderer is used to adjust the wing rotation
     */
    public WingsLayer(IEntityRenderer<T, Q> entityRendererIn, Predicate<T> predicateRender, BiFunction<T, Q, ModelRenderer> bodyPartFunction) {
        super(entityRendererIn);
        this.predicateRender = predicateRender;
        this.bodyPartFunction = bodyPartFunction;
    }


    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entityIn.isInvisible() && predicateRender.test(entityIn)) {
            this.model.copyRotationFromBody(bodyPartFunction.apply(entityIn, this.getEntityModel()));
            float s = 1f;
            if (entityIn instanceof VampireBaronEntity) {
                s = ((VampireBaronEntity) entityIn).getEnragedProgress();
            }
            matrixStackIn.push();
            matrixStackIn.translate(0f, 0, 0.02f);
            matrixStackIn.scale(s, s, s);
            renderCopyCutoutModel(this.getEntityModel(), model, texture, matrixStackIn, bufferIn, packedLightIn, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1, 1, 1);
            matrixStackIn.pop();
        }
    }


}