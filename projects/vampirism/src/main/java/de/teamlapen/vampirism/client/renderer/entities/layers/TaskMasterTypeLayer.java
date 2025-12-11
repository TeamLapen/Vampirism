package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.renderer.entities.state.TaskMasterRenderState;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Render biome specific middle layer and "profession" specific top layer
 */
public class TaskMasterTypeLayer<T extends TaskMasterRenderState> extends RenderLayer<T, VillagerModel> {

    private final ResourceLocation additionalOverlay;

    public TaskMasterTypeLayer(@NotNull RenderLayerParent<T, VillagerModel> entityRendererIn, ResourceLocation additionalOverlay) {
        super(entityRendererIn);
        this.additionalOverlay = additionalOverlay;
    }

    private @NotNull ResourceLocation deriveTypeTextureOverlay(@NotNull ResourceLocation id) {
        return id.withPath("textures/entity/villager/type/" + id.getPath() + ".png");
    }

    @Override
    public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector nodeCollector, int packedLight, T renderState, float yRot, float xRot) {
        if (!renderState.isInvisible) {
            ResourceLocation type = renderState.getVillagerData().type().getKey().location();
            VillagerModel parentModel = getParentModel();
            renderColoredCutoutModel(parentModel, this.deriveTypeTextureOverlay(type), poseStack, nodeCollector, packedLight, renderState, -1, -1);
            renderColoredCutoutModel(parentModel, additionalOverlay, poseStack, nodeCollector, packedLight, renderState, -1, -1);
        }
    }
}
