package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.client.renderer.entities.AdvancedVampireRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Render the eyes over the advanced vampire custom face
 */
public class AdvancedVampireEyeLayer extends RenderLayer<AdvancedVampireRenderer.AdvancedVampireRenderState, ClothedModel<AdvancedVampireRenderer.AdvancedVampireRenderState>> {

    private final Identifier @NotNull []overlays;

    public AdvancedVampireEyeLayer(@NotNull RenderLayerParent<AdvancedVampireRenderer.AdvancedVampireRenderState, ClothedModel<AdvancedVampireRenderer.AdvancedVampireRenderState>> renderer) {
        super(renderer);
        overlays = new Identifier[REFERENCE.EYE_TYPE_COUNT];
        for (int i = 0; i < overlays.length; i++) {
            overlays[i] = VResourceLocation.mod("textures/entity/vanilla/eyes" + (i) + ".png");
        }
    }

    @Override
    public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector nodeCollector, int packedLight, AdvancedVampireRenderer.AdvancedVampireRenderState renderState, float yRot, float xRot) {
        int type = renderState.eyeType;
        if (type < 0 || type >= overlays.length) {
            type = 0;
        }

        boolean showModel = this.getParentModel().head.visible;

        getParentModel().head.visible = true;
        nodeCollector.submitModel(getParentModel(), renderState, poseStack, RenderTypes.entityTranslucent(overlays[type]), packedLight, OverlayTexture.NO_OVERLAY, -1, null);
        getParentModel().head.visible = showModel;
    }
}
