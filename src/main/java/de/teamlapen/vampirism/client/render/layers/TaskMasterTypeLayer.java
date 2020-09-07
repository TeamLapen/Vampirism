package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.entity.IDefaultTaskMasterEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

/**
 * Render biome specific middle layer and "profession" specific top layer
 */
public class TaskMasterTypeLayer<T extends MobEntity & IDefaultTaskMasterEntity> extends LayerRenderer<T, VillagerModel<T>> {
    private final ResourceLocation additionalOverlay;

    public TaskMasterTypeLayer(IEntityRenderer<T, VillagerModel<T>> entityRendererIn, ResourceLocation additionalOverlay) {
        super(entityRendererIn);
        this.additionalOverlay = additionalOverlay;
    }


    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entityIn.isInvisible()) {
            IVillagerType type = entityIn.getBiomeType();
            VillagerModel<T> m = getEntityModel();
            renderCutoutModel(m, this.deriveTypeTextureOverlay(Registry.VILLAGER_TYPE.getKey(type)), matrixStackIn, bufferIn, packedLightIn, entityIn, 1.0F, 1.0F, 1.0F);
            renderCutoutModel(m, additionalOverlay, matrixStackIn, bufferIn, packedLightIn, entityIn, 1.0F, 1.0F, 1.0F);

        }
    }


    public boolean shouldCombineTextures() {
        return true;
    }

    private ResourceLocation deriveTypeTextureOverlay(ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), "textures/entity/villager/type/" + id.getPath() + ".png");
    }

}
