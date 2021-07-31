package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.model.VillagerWithArmsModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.VillagerLevelPendantLayer;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HunterVillagerRenderer extends MobRenderer<VillagerEntity, VillagerWithArmsModel<VillagerEntity>> {

    private static final ResourceLocation texture = new ResourceLocation("textures/entity/villager/villager.png");

    public HunterVillagerRenderer(EntityRendererManager renderManagerIn, IReloadableResourceManager resourceManager) {
        super(renderManagerIn, new VillagerWithArmsModel<>(0), 0.5f);
        this.addLayer(new HeadLayer<>(this));
        this.addLayer(new VillagerLevelPendantLayer<>(this, resourceManager, "villager"));
        this.addLayer(new HeldItemLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(VillagerEntity villagerEntity) {
        return texture;
    }

    /**
     * Copied from VillagerRenderer
     */
    @Override
    protected void scale(VillagerEntity entity, MatrixStack matrixStack, float partialTickTime) {
        float s = 0.9375F;
        if (entity.isBaby()) {
            s = (float) ((double) s * 0.5D);
            this.shadowRadius = 0.25F;
        } else {
            this.shadowRadius = 0.5F;
        }

        matrixStack.scale(s, s, s);
    }
}
