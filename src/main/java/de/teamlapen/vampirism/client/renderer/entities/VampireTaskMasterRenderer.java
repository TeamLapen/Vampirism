package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.renderer.entities.layers.TaskMasterTypeLayer;
import de.teamlapen.vampirism.client.renderer.entities.layers.VampireEntityLayer;
import de.teamlapen.vampirism.client.renderer.entities.state.TaskMasterRenderState;
import de.teamlapen.vampirism.common.entity.vampire.VampireTaskMasterEntity;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Render the advanced vampire with overlays
 */
public class VampireTaskMasterRenderer extends MobRenderer<VampireTaskMasterEntity, VampireTaskMasterRenderer.VampireTaskMasterRenderState, VillagerModel> {
    private final static ResourceLocation texture = VResourceLocation.mc("textures/entity/villager/villager.png");
    private final static ResourceLocation vampireOverlay = VResourceLocation.mod("textures/entity/vanilla/villager_overlay.png");
    private final static ResourceLocation overlay = VResourceLocation.mod("textures/entity/vampire_task_master_overlay.png");

    public VampireTaskMasterRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new VillagerModel(context.bakeLayer(ModEntitiesRender.TASK_MASTER)), 0.5F);
        this.addLayer(new VampireEntityLayer<>(this, vampireOverlay));
        this.addLayer(new TaskMasterTypeLayer<>(this, overlay));
    }

    @Override
    public @NotNull VampireTaskMasterRenderState createRenderState() {
        return new VampireTaskMasterRenderState();
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull VampireTaskMasterRenderState entity) {
        return texture;
    }

    @Override
    protected void renderNameTag(VampireTaskMasterRenderState state, @NotNull Component name, @NotNull PoseStack stack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        if (state.distanceToCameraSq < 128) {
            super.renderNameTag(state, name, stack, bufferSource, packedLight);
        }
    }

    @Override
    protected boolean shouldShowName(@NotNull VampireTaskMasterEntity entity, double p_364446_) {
        return Helper.isVampire(entity) && super.shouldShowName(entity, p_364446_);
    }

    public static class VampireTaskMasterRenderState extends TaskMasterRenderState {
    }
}
