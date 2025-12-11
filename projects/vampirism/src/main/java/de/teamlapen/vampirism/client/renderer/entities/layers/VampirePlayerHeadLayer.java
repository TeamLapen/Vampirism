package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.misc.extension.client.IVampirePlayerState;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
        if (renderState instanceof IVampirePlayerState vampireState && vampireState.vampirism$vampire$level() > 0 && !vampireState.vampirism$vampire$isDisguised() && !renderState.isInvisible) {
            int eyeType = Math.max(0, Math.min(vampireState.vampirism$vampire$getEyeType(), eyeOverlays.length - 1));
            int fangType = Math.max(0, Math.min(vampireState.vampirism$vampire$getFangType(), fangOverlays.length - 1));
            ModelPart head = this.getParentModel().head;

            nodeCollector.submitModelPart(head, poseStack,vampireState.vampirism$vampire$getGlowingEyes() ? RenderType.eyes(eyeOverlays[eyeType]) : RenderType.entityCutoutNoCull(eyeOverlays[eyeType]),packedLight, OverlayTexture.NO_OVERLAY, null);
            nodeCollector.submitModelPart(head, poseStack,RenderType.entityCutoutNoCull(fangOverlays[fangType]),packedLight, OverlayTexture.NO_OVERLAY, null);
        }
    }
}