package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModEntityRenderStates;
import de.teamlapen.vampirism.common.config.ModConfig;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class VampirePlayerHeadLayer<T extends AvatarRenderState, Q extends PlayerModel> extends RenderLayer<T, Q> {

    private final Identifier @NotNull [] eyeOverlays;
    private final Identifier @NotNull [] fangOverlays;

    public VampirePlayerHeadLayer(@NotNull RenderLayerParent<T, Q> entityRendererIn) {
        super(entityRendererIn);
        eyeOverlays = new Identifier[REFERENCE.EYE_TYPE_COUNT];
        for (int i = 0; i < eyeOverlays.length; i++) {
            eyeOverlays[i] = VIdentifier.mod("textures/entity/vanilla/eyes" + (i) + ".png");
        }
        fangOverlays = new Identifier[REFERENCE.FANG_TYPE_COUNT];
        for (int i = 0; i < fangOverlays.length; i++) {
            fangOverlays[i] = VIdentifier.mod("textures/entity/vanilla/fangs" + i + ".png");
        }
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, T renderState, float yRot, float xRot) {
        if (!ModConfig.client().renderVampireEyes.get() || renderState.deathTime > 0) return;
        if (renderState.getRenderDataOrDefault(ModEntityRenderStates.VAMPIRE_LEVEL, 0) > 0 && !renderState.getRenderDataOrDefault(ModEntityRenderStates.VAMPIRE_DISGUISE, false) && !renderState.isInvisible) {
            int eyeType = Math.max(0, Math.min(renderState.getRenderDataOrDefault(ModEntityRenderStates.VAMPIRE_EYE_TYPE, 0), eyeOverlays.length - 1));
            int fangType = Math.max(0, Math.min(renderState.getRenderDataOrDefault(ModEntityRenderStates.VAMPIRE_FANG_TYPE, 0), fangOverlays.length - 1));
            ModelPart head = this.getParentModel().head;

            nodeCollector.submitModelPart(head, poseStack,renderState.getRenderDataOrDefault(ModEntityRenderStates.VAMPIRE_GLOWING_EYES, false) ? RenderTypes.eyes(eyeOverlays[eyeType]) : RenderTypes.entityCutoutNoCull(eyeOverlays[eyeType]),packedLight, OverlayTexture.NO_OVERLAY, null);
            nodeCollector.submitModelPart(head, poseStack,RenderTypes.entityCutout(fangOverlays[fangType]),packedLight, OverlayTexture.NO_OVERLAY, null);
        }
    }
}