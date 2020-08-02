package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.entity.IDefaultTaskMasterEntity;
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
public class LayerTaskMasterType<T extends MobEntity & IDefaultTaskMasterEntity> extends LayerRenderer<T, VillagerModel<T>> {
    private final ResourceLocation additionalOverlay;

    public LayerTaskMasterType(IEntityRenderer<T, VillagerModel<T>> entityRendererIn, ResourceLocation additionalOverlay) {
        super(entityRendererIn);
        this.additionalOverlay = additionalOverlay;
    }

    @Override
    public void render(T entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
        if (!entityIn.isInvisible()) {
            IVillagerType type = entityIn.getBiomeType();
            this.bindTexture(this.deriveTypeTextureOverlay(Registry.VILLAGER_TYPE.getKey(type)));
            VillagerModel<T> m = getEntityModel();
            m.render(entityIn, p_212842_2_, p_212842_3_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);
            this.bindTexture(additionalOverlay);
            m.render(entityIn, p_212842_2_, p_212842_3_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);

        }
    }


    public boolean shouldCombineTextures() {
        return true;
    }

    private ResourceLocation deriveTypeTextureOverlay(ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), "textures/entity/villager/type/" + id.getPath() + ".png");
    }

}
