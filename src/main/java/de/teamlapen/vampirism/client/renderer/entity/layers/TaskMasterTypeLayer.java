package de.teamlapen.vampirism.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.entity.IDefaultTaskMasterEntity;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.VillagerType;
import org.jetbrains.annotations.NotNull;

/**
 * Render biome specific middle layer and "profession" specific top layer
 */
public class TaskMasterTypeLayer<T extends Mob & IDefaultTaskMasterEntity> extends RenderLayer<T, VillagerModel<T>> {
    private final ResourceLocation additionalOverlay;

    public TaskMasterTypeLayer(@NotNull RenderLayerParent<T, VillagerModel<T>> entityRendererIn, ResourceLocation additionalOverlay) {
        super(entityRendererIn);
        this.additionalOverlay = additionalOverlay;
    }


    @Override
    public void render(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, @NotNull T entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entityIn.isInvisible()) {
            VillagerType type = entityIn.getBiomeType();
            VillagerModel<T> m = getParentModel();
            renderColoredCutoutModel(m, this.deriveTypeTextureOverlay(BuiltInRegistries.VILLAGER_TYPE.getKey(type)), matrixStackIn, bufferIn, packedLightIn, entityIn, 1.0F, 1.0F, 1.0F);
            renderColoredCutoutModel(m, additionalOverlay, matrixStackIn, bufferIn, packedLightIn, entityIn, 1.0F, 1.0F, 1.0F);
        }
    }

    private @NotNull ResourceLocation deriveTypeTextureOverlay(@NotNull ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), "textures/entity/villager/type/" + id.getPath() + ".png");
    }

}
