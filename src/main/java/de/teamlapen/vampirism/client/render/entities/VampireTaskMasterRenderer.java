package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.render.layers.TaskMasterTypeLayer;
import de.teamlapen.vampirism.client.render.layers.VampireEntityLayer;
import de.teamlapen.vampirism.entity.vampire.VampireTaskMasterEntity;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;

/**
 * Render the advanced vampire with overlays
 */
@OnlyIn(Dist.CLIENT)
public class VampireTaskMasterRenderer extends MobRenderer<VampireTaskMasterEntity, VillagerModel<VampireTaskMasterEntity>> {
    private final static ResourceLocation texture = new ResourceLocation("textures/entity/villager/villager.png");
    private final static ResourceLocation vampireOverlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/villager_overlay.png");
    private final static ResourceLocation overlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_task_master_overlay.png");

    public VampireTaskMasterRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModEntitiesRender.TASK_MASTER)), 0.5F);
//        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new VampireEntityLayer<>(this, vampireOverlay, false));
        this.addLayer(new TaskMasterTypeLayer<>(this, overlay));
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull VampireTaskMasterEntity entity) {
        return texture;
    }

    @Override
    protected void renderNameTag(@NotNull VampireTaskMasterEntity entityIn, @NotNull Component displayNameIn, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        double dist = this.entityRenderDispatcher.distanceToSqr(entityIn);
        if (dist <= 128) {
            super.renderNameTag(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        }
    }


}
