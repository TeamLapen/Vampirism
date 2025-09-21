package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.armor.WingModel;
import de.teamlapen.vampirism.client.renderer.entities.VampireBaronRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Predicate;


public class WingsLayer<T extends LivingEntity, S extends HumanoidRenderState, Q extends EntityModel<S>> extends RenderLayer<S, Q> {

    private final @NotNull WingModel<S> model;
    private final Predicate<S> predicateRender;
    private final BiFunction<S, Q, ModelPart> bodyPartFunction;
    private final ResourceLocation texture = VResourceLocation.mod("textures/entity/wings.png");

    /**
     * @param predicateRender  Decides if the layer is rendered
     * @param bodyPartFunction Should return the main body part. The returned ModelRenderer is used to adjust the wing rotation
     */
    public WingsLayer(@NotNull RenderLayerParent<S, Q> entityRendererIn, @NotNull EntityModelSet modelSet, Predicate<S> predicateRender, BiFunction<S, Q, ModelPart> bodyPartFunction) {
        super(entityRendererIn);
        this.model = new WingModel<>(modelSet.bakeLayer(ModEntitiesRender.WING));
        this.predicateRender = predicateRender;
        this.bodyPartFunction = bodyPartFunction;
    }

    @Override
    public void render(@NotNull PoseStack stack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull S entity, float f1, float f2) {
        if (!entity.isInvisible && predicateRender.test(entity)) {
            this.model.copyRotationFromBody(bodyPartFunction.apply(entity, this.getParentModel()));
            float s = 1f;
            if (entity instanceof VampireBaronRenderer.VampireBaronRenderState baron) {
                s = baron.enragedProgress;
            }
            stack.pushPose();
            stack.translate(0f, 0, 0.02f);
            stack.scale(s, s, s);
            coloredCutoutModelCopyLayerRender(model, texture, stack, buffer, packedLight, entity, -1);
            stack.popPose();
        }
    }


}