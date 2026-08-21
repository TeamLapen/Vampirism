package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.faction.client.gui.components.ColoredImageWidget;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModRenderPipelines;
import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.client.renderer.TextureLoader;
import de.teamlapen.vampirism.client.renderer.entities.AdvancedVampireRenderer;
import it.unimi.dsi.fastutil.objects.AbstractObject2ObjectFunction;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import java.util.SequencedMap;

/**
 * Render the eyes over the advanced vampire custom face
 */
public class AdvancedVampireFangLayer extends RenderLayer<AdvancedVampireRenderer.AdvancedVampireRenderState, ClothedModel<AdvancedVampireRenderer.AdvancedVampireRenderState>> {

    private final SequencedMap<Identifier, Identifier> overlays;

    public AdvancedVampireFangLayer(RenderLayerParent<AdvancedVampireRenderer.AdvancedVampireRenderState, ClothedModel<AdvancedVampireRenderer.AdvancedVampireRenderState>> renderer) {
        super(renderer);
        this.overlays = TextureLoader.mapTexturesInById("textures/entity/fangs");
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, AdvancedVampireRenderer.AdvancedVampireRenderState renderState, float yRot, float xRot) {
        Identifier texture = this.overlays.get(renderState.fangTexture);

        if (texture == null) return;

        nodeCollector.order(1).submitModel(getParentModel(), renderState, poseStack, RenderTypes.eyes(texture), packedLight, OverlayTexture.NO_OVERLAY, 0, null);

    }
}
