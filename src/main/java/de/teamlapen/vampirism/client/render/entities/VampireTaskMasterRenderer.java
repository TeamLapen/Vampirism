package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.render.layers.TaskMasterTypeLayer;
import de.teamlapen.vampirism.client.render.layers.VampireEntityLayer;
import de.teamlapen.vampirism.entity.vampire.VampireTaskMasterEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Render the advanced vampire with overlays
 */
@OnlyIn(Dist.CLIENT)
public class VampireTaskMasterRenderer extends MobRenderer<VampireTaskMasterEntity, VillagerModel<VampireTaskMasterEntity>> {
    private final static ResourceLocation texture = new ResourceLocation("textures/entity/villager/villager.png");
    private final static ResourceLocation vampireOverlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/villager_overlay.png");
    private final static ResourceLocation overlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_task_master_overlay.png");

    public VampireTaskMasterRenderer(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new VillagerModel<>(0F), 0.5F);
//        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new VampireEntityLayer<>(this, vampireOverlay, false));
        this.addLayer(new TaskMasterTypeLayer<>(this, overlay));
    }

    @Override
    public ResourceLocation getTextureLocation(VampireTaskMasterEntity entity) {
        return texture;
    }

    @Override
    protected void renderNameTag(VampireTaskMasterEntity entityIn, Component displayNameIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        double dist = this.entityRenderDispatcher.distanceToSqr(entityIn);
        if (dist <= 128) {
            super.renderNameTag(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        }
    }


}
