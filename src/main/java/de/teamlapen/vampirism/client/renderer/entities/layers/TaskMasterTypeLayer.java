package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.renderer.entities.state.TaskMasterRenderState;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerType;
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
    public void render(PoseStack stack, MultiBufferSource bufferSource, int packedLight, T state, float p_117353_, float p_117354_) {
        if (!state.isInvisible) {
            VillagerType type = state.getVillagerData().getType();
            VillagerModel parentModel = getParentModel();
            renderColoredCutoutModel(parentModel, this.deriveTypeTextureOverlay(BuiltInRegistries.VILLAGER_TYPE.getKey(type)), stack, bufferSource, packedLight, state, -1);
            renderColoredCutoutModel(parentModel, additionalOverlay, stack, bufferSource, packedLight, state, -1);
        }
    }
}
