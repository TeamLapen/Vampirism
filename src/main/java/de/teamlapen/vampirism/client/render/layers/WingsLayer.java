package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.model.WingModel;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible() && predicateRender.test(entity)) {
            this.model.copyRotationFromBody(bodyPartFunction.apply(entity, this.getEntityModel()));
            float s = 1f;
            if (entity instanceof VampireBaronEntity) {
                s = ((VampireBaronEntity) entity).getEnragedProgress();
            } else if (entity instanceof PlayerEntity) { //In case we are using the player model for rendering the baron
                int ticks = VampirePlayer.getOpt((PlayerEntity) entity).map(VampirePlayer::getWingCounter).orElse(0);
                s = ticks > 20 ? (ticks > 1180 ? 1f - (ticks - 1180) / 20f : 1f) : ticks / 20f;
            }
            stack.push();
            stack.translate(0f, 0, 0.02f);
            stack.scale(s, s, s);
            renderCopyCutoutModel(this.getEntityModel(), model, texture, stack, buffer, packedLight, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1, 1, 1);
            stack.pop();
        }
    }


}