package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.renderer.entities.state.TaskMasterRenderState;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Render biome specific middle layer and "profession" specific top layer
 */
public class TaskMasterTypeLayer<T extends TaskMasterRenderState> extends RenderLayer<T, VillagerModel> {

    private final Identifier additionalOverlay;

    public TaskMasterTypeLayer(@NotNull RenderLayerParent<T, VillagerModel> entityRendererIn, Identifier additionalOverlay) {
        super(entityRendererIn);
        this.additionalOverlay = additionalOverlay;
    }

    private @NotNull Identifier deriveTypeTextureOverlay(@NotNull Identifier id) {
        return id.withPath("textures/entity/villager/type/" + id.getPath() + ".png");
    }

    @Override
    public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector nodeCollector, int packedLight, T renderState, float yRot, float xRot) {
        if (!renderState.isInvisible) {
            Identifier type = renderState.getVillagerData().type().getKey().identifier();
            VillagerModel parentModel = getParentModel();
            renderColoredCutoutModel(parentModel, this.deriveTypeTextureOverlay(type), poseStack, nodeCollector, packedLight, renderState, -1, 1);
            renderColoredCutoutModel(parentModel, additionalOverlay, poseStack, nodeCollector, packedLight, renderState, -1, 1);
        }
    }
}
