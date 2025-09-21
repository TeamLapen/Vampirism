package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.client.renderer.entities.state.IOverlayRenderState;
import de.teamlapen.vampirism.common.util.IPlayerOverlay;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

/**
 * Renders an overlay over the entities face
 */
public class PlayerFaceOverlayLayer<T extends Mob & IPlayerOverlay, S extends HumanoidRenderState & IOverlayRenderState, M extends HumanoidModel<S>> extends RenderLayer<S, M> {


    public PlayerFaceOverlayLayer(@NotNull HumanoidMobRenderer<T, S, M> renderBiped) {
        super(renderBiped);
    }

    @Override
    public void render(@NotNull PoseStack stack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull S entity, float f1, float f2) {
        ResourceLocation loc = entity.overlay();
        VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(loc));
        this.getParentModel().head.visible = true;
        this.getParentModel().hat.visible = true;
        this.getParentModel().head.render(stack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, -1);
        this.getParentModel().hat.render(stack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, -1);
        this.getParentModel().head.visible = false;
        this.getParentModel().hat.visible = false;

    }


}
