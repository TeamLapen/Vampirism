package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.client.renderer.entities.AdvancedVampireRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

/**
 * Render the eyes over the advanced vampire custom face
 */
public class AdvancedVampireFangLayer extends RenderLayer<AdvancedVampireRenderer.AdvancedVampireRenderState, ClothedModel<AdvancedVampireRenderer.AdvancedVampireRenderState>> {

    private final Identifier[] overlays;

    public AdvancedVampireFangLayer(RenderLayerParent<AdvancedVampireRenderer.AdvancedVampireRenderState, ClothedModel<AdvancedVampireRenderer.AdvancedVampireRenderState>> renderer) {
        super(renderer);
        overlays = new Identifier[REFERENCE.FANG_TYPE_COUNT];
        for (int i = 0; i < overlays.length; i++) {
            overlays[i] = VIdentifier.mod("textures/entity/vanilla/fangs" + (i) + ".png");
        }
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, AdvancedVampireRenderer.AdvancedVampireRenderState renderState, float yRot, float xRot) {
        int type = renderState.fangType;
        if (type < 0 || type >= overlays.length) {
            type = 0;
        }

        boolean showModel = this.getParentModel().head.visible;
        this.getParentModel().head.visible = true;
        nodeCollector.submitModel(getParentModel(), renderState, poseStack, RenderTypes.entityTranslucent(overlays[type]), packedLight, OverlayTexture.NO_OVERLAY, 0, null);
        this.getParentModel().head.visible = showModel;

    }
}
