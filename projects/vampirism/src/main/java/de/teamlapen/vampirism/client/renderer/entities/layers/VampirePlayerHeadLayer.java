package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModEntityRenderStates;
import de.teamlapen.vampirism.client.renderer.TextureLoader;
import de.teamlapen.vampirism.common.config.ModConfig;
import it.unimi.dsi.fastutil.objects.AbstractObject2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import java.util.SequencedMap;

public class VampirePlayerHeadLayer<T extends AvatarRenderState, Q extends PlayerModel> extends RenderLayer<T, Q> {

    private static final Identifier FALLBACK_EYE = VIdentifier.mod("textures/entity/eyes/eyes0.png");
    private static final Identifier FALLBACK_FANG = VIdentifier.mod("textures/entity/fangs/fangs0.png");

    private final SequencedMap<Identifier, Identifier> eyeOverlays;
    private final SequencedMap<Identifier, Identifier> fangOverlays;

    public VampirePlayerHeadLayer(RenderLayerParent<T, Q> entityRendererIn) {
        super(entityRendererIn);
        this.eyeOverlays = TextureLoader.mapTexturesInById("textures/entity/eyes");
        this.fangOverlays = TextureLoader.mapTexturesInById("textures/entity/fangs");
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, T renderState, float yRot, float xRot) {
        if (!ModConfig.client().renderVampireEyes.get() || renderState.deathTime > 0) return;
        if (renderState.getRenderDataOrDefault(ModEntityRenderStates.VAMPIRE_LEVEL, 0) > 0 && !renderState.getRenderDataOrDefault(ModEntityRenderStates.VAMPIRE_DISGUISE, false) && !renderState.isInvisible) {

            Identifier eye = this.eyeOverlays.getOrDefault(renderState.getRenderData(ModEntityRenderStates.VAMPIRE_EYE_TYPE), FALLBACK_EYE);
            Identifier fang = this.fangOverlays.getOrDefault(renderState.getRenderData(ModEntityRenderStates.VAMPIRE_FANG_TYPE), FALLBACK_FANG);

            ModelPart head = this.getParentModel().head;

            nodeCollector.submitModelPart(head, poseStack,renderState.getRenderDataOrDefault(ModEntityRenderStates.VAMPIRE_GLOWING_EYES, false) ? RenderTypes.eyes(eye) : RenderTypes.entityTranslucentEmissive(eye), packedLight, OverlayTexture.NO_OVERLAY, null);
            nodeCollector.submitModelPart(head, poseStack,RenderTypes.entityTranslucentEmissive(fang),packedLight, OverlayTexture.NO_OVERLAY, null);
        }
    }
}