package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.renderer.entities.layers.TaskMasterTypeLayer;
import de.teamlapen.vampirism.client.renderer.entities.state.TaskMasterRenderState;
import de.teamlapen.vampirism.common.entity.hunter.HunterTaskMasterEntity;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Render the advanced vampire with overlays
 */
public class HunterTaskMasterRenderer extends MobRenderer<HunterTaskMasterEntity, HunterTaskMasterRenderer.HunterTaskMasterRenderState, VillagerModel> {
    private final static ResourceLocation texture = VResourceLocation.mc("textures/entity/villager/villager.png");
    private final static ResourceLocation overlay = VResourceLocation.mod("textures/entity/hunter_task_master_overlay.png");

    public HunterTaskMasterRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new VillagerModel(context.bakeLayer(ModEntitiesRender.TASK_MASTER)), 0.5F);
        this.addLayer(new TaskMasterTypeLayer<>(this, overlay));
        this.addLayer(new HelmetLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HunterTaskMasterRenderState state) {
        return texture;
    }

    @Override
    protected void submitNameTag(HunterTaskMasterRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.distanceToCameraSq <= 128) {
            super.submitNameTag(renderState, poseStack, nodeCollector, cameraRenderState);
        }
    }

    @Override
    public @NotNull HunterTaskMasterRenderState createRenderState() {
        return new HunterTaskMasterRenderState();
    }

    @Override
    protected boolean shouldShowName(@NotNull HunterTaskMasterEntity entity, double distance) {
        return Helper.isHunter(entity) && super.shouldShowName(entity, distance);
    }

    private static class HelmetLayer extends RenderLayer<HunterTaskMasterRenderState, VillagerModel> {

        public HelmetLayer(RenderLayerParent<HunterTaskMasterRenderState, VillagerModel> pRenderer) {
            super(pRenderer);
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, HunterTaskMasterRenderState state, float yRot, float xRot) {
            if (!state.headItem.isEmpty()) {
                poseStack.pushPose();
                this.getParentModel().getHead().translateAndRotate(poseStack);
                CustomHeadLayer.translateToHead(poseStack, CustomHeadLayer.Transforms.DEFAULT);
                poseStack.translate(0.0F, -0.2F, 0.0F);
                poseStack.scale(1.1F, 1.1F, 1.1F);
                state.headItem.submit(poseStack, nodeCollector, packedLight, OverlayTexture.NO_OVERLAY, -1);
                poseStack.popPose();
            }
        }

    }


    public static class HunterTaskMasterRenderState extends TaskMasterRenderState {
    }
}