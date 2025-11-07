package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.renderer.entities.state.IVampirismRenderState;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.entity.player.VampirismPlayerAttributes;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class VampirePlayerHeadLayer<T extends AvatarRenderState, Q extends PlayerModel> extends RenderLayer<T, Q> {

    private final ResourceLocation @NotNull [] eyeOverlays;
    private final ResourceLocation @NotNull [] fangOverlays;

    public VampirePlayerHeadLayer(@NotNull RenderLayerParent<T, Q> entityRendererIn) {
        super(entityRendererIn);
        eyeOverlays = new ResourceLocation[REFERENCE.EYE_TYPE_COUNT];
        for (int i = 0; i < eyeOverlays.length; i++) {
            eyeOverlays[i] = VResourceLocation.mod("textures/entity/vanilla/eyes" + (i) + ".png");
        }
        fangOverlays = new ResourceLocation[REFERENCE.FANG_TYPE_COUNT];
        for (int i = 0; i < fangOverlays.length; i++) {
            fangOverlays[i] = VResourceLocation.mod("textures/entity/vanilla/fangs" + i + ".png");
        }
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, T renderState, float yRot, float xRot) {
        if (!ModConfig.CLIENT.renderVampireEyes.get() || renderState.deathTime > 0) return;
        VampirismPlayerAttributes atts = ((IVampirismRenderState) renderState).vampirism$attributes();
        if (atts.vampireLevel > 0 && !atts.getVampSpecial().disguised && !renderState.isInvisible) {
            int eyeType = Math.max(0, Math.min(atts.getVampSpecial().eyeType, eyeOverlays.length - 1));
            int fangType = Math.max(0, Math.min(atts.getVampSpecial().fangType, fangOverlays.length - 1));
            ModelPart head = this.getParentModel().head;
            int packerOverlay = LivingEntityRenderer.getOverlayCoords(renderState, 0);

            nodeCollector.submitCustomGeometry(poseStack, atts.getVampSpecial().glowingEyes ? RenderType.eyes(eyeOverlays[eyeType]) : RenderType.entityCutoutNoCull(eyeOverlays[eyeType]), (pose, cosumer) -> {
                head.render(poseStack, cosumer, packedLight, packerOverlay);
            });
            nodeCollector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(fangOverlays[fangType]), (pose, cosumer) -> {
                head.render(poseStack, cosumer, packedLight, packerOverlay);

            });

        }
    }

}