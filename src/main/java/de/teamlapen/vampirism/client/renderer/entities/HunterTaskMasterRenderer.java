package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.renderer.entities.layers.TaskMasterTypeLayer;
import de.teamlapen.vampirism.client.renderer.entities.state.TaskMasterRenderState;
import de.teamlapen.vampirism.common.entity.hunter.HunterTaskMasterEntity;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
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
    protected void renderNameTag(HunterTaskMasterRenderState state, @NotNull Component name, @NotNull PoseStack stack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        if (state.distanceToCameraSq < 128) {
            super.renderNameTag(state, name, stack, bufferSource, packedLight);
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
        public void render(@NotNull PoseStack stack, @NotNull MultiBufferSource bufferSource, int packedLight, HunterTaskMasterRenderState state, float p_117353_, float p_117354_) {
            if (!state.headItem.isEmpty()) {
                stack.pushPose();
                this.getParentModel().getHead().translateAndRotate(stack);
                CustomHeadLayer.translateToHead(stack, CustomHeadLayer.Transforms.DEFAULT);
                stack.translate(0.0F, -0.2F, 0.0F);
                stack.scale(1.1F, 1.1F, 1.1F);
                state.headItem.render(stack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
                stack.popPose();
            }
        }
    }


    public static class HunterTaskMasterRenderState extends TaskMasterRenderState {
    }
}