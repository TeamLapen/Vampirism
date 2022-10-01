package de.teamlapen.vampirism.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.WingModel;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Predicate;


public class WingsLayer<T extends LivingEntity, Q extends EntityModel<T>> extends RenderLayer<T, Q> {

    private final @NotNull WingModel<T> model;
    private final Predicate<T> predicateRender;
    private final BiFunction<T, Q, ModelPart> bodyPartFunction;
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/wings.png");

    /**
     * @param predicateRender  Decides if the layer is rendered
     * @param bodyPartFunction Should return the main body part. The returned ModelRenderer is used to adjust the wing rotation
     */
    public WingsLayer(@NotNull RenderLayerParent<T, Q> entityRendererIn, @NotNull EntityModelSet modelSet, Predicate<T> predicateRender, BiFunction<T, Q, ModelPart> bodyPartFunction) {
        super(entityRendererIn);
        this.model = new WingModel<>(modelSet.bakeLayer(ModEntitiesRender.WING));
        this.predicateRender = predicateRender;
        this.bodyPartFunction = bodyPartFunction;
    }


    @Override
    public void render(@NotNull PoseStack stack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible() && predicateRender.test(entity)) {
            this.model.copyRotationFromBody(bodyPartFunction.apply(entity, this.getParentModel()));
            float s = 1f;
            if (entity instanceof VampireBaronEntity) {
                s = ((VampireBaronEntity) entity).getEnragedProgress();
            }
            stack.pushPose();
            stack.translate(0f, 0, 0.02f);
            stack.scale(s, s, s);
            coloredCutoutModelCopyLayerRender(this.getParentModel(), model, texture, stack, buffer, packedLight, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1, 1, 1);
            stack.popPose();
        }
    }


}