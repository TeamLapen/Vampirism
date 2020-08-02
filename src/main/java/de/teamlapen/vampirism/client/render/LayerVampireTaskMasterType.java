package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.entity.vampire.VampireTaskMasterEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

/**
 * Render biome specific middle layer and "profession" specific top layer
 */
public class LayerVampireTaskMasterType extends LayerRenderer<VampireTaskMasterEntity, VillagerModel<VampireTaskMasterEntity>> {
    private final static ResourceLocation OVERLAY = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_task_master_overlay.png");

    public LayerVampireTaskMasterType(IEntityRenderer<VampireTaskMasterEntity, VillagerModel<VampireTaskMasterEntity>> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(VampireTaskMasterEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
        if (!entityIn.isInvisible()) {
            IVillagerType type = entityIn.getBiomeType();
            this.bindTexture(this.func_215351_a("type", Registry.VILLAGER_TYPE.getKey(type)));
            VillagerModel<VampireTaskMasterEntity> m = getEntityModel();
            m.render(entityIn, p_212842_2_, p_212842_3_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);
            this.bindTexture(OVERLAY);
            m.render(entityIn, p_212842_2_, p_212842_3_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);

        }
    }


    public boolean shouldCombineTextures() {
        return true;
    }

    private ResourceLocation func_215351_a(String p_215351_1_, ResourceLocation p_215351_2_) {
        return new ResourceLocation(p_215351_2_.getNamespace(), "textures/entity/villager/" + p_215351_1_ + "/" + p_215351_2_.getPath() + ".png");
    }

}
