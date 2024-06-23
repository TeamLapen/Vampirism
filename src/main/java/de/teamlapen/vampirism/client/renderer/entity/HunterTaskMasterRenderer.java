package de.teamlapen.vampirism.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.renderer.entity.layers.TaskMasterTypeLayer;
import de.teamlapen.vampirism.entity.hunter.HunterTaskMasterEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Render the advanced vampire with overlays
 */
public class HunterTaskMasterRenderer extends MobRenderer<HunterTaskMasterEntity, VillagerModel<HunterTaskMasterEntity>> {
    private final static ResourceLocation texture = VResourceLocation.mc("textures/entity/villager/villager.png");
    private final static ResourceLocation overlay = VResourceLocation.mod("textures/entity/hunter_task_master_overlay.png");

    public HunterTaskMasterRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModEntitiesRender.TASK_MASTER)), 0.5F);
//        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new TaskMasterTypeLayer<>(this, overlay));
        this.addLayer(new HelmetLayer(this, context.getItemInHandRenderer()));
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull HunterTaskMasterEntity entity) {
        return texture;
    }

    @Override
    protected void renderNameTag(@NotNull HunterTaskMasterEntity entityIn, @NotNull Component displayNameIn, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, float partialTicks) {
        double dist = this.entityRenderDispatcher.distanceToSqr(entityIn);
        if (dist <= 128) {
            super.renderNameTag(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn, partialTicks);
        }
    }

    @Override
    protected boolean shouldShowName(HunterTaskMasterEntity pEntity) {
        return Helper.isHunter(pEntity) && super.shouldShowName(pEntity);
    }
    private static class HelmetLayer extends RenderLayer<HunterTaskMasterEntity, VillagerModel<HunterTaskMasterEntity>> {
        private final ItemInHandRenderer pItemInHandRenderer;

        public HelmetLayer(RenderLayerParent<HunterTaskMasterEntity, VillagerModel<HunterTaskMasterEntity>> pRenderer, ItemInHandRenderer pItemInHandRenderer) {
            super(pRenderer);
            this.pItemInHandRenderer = pItemInHandRenderer;
        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, HunterTaskMasterEntity pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            ItemStack itemstack = pLivingEntity.getItemBySlot(EquipmentSlot.HEAD);
            if (!itemstack.isEmpty()) {
                pPoseStack.pushPose();
                this.getParentModel().getHead().translateAndRotate(pPoseStack);
                CustomHeadLayer.translateToHead(pPoseStack, true);
                pPoseStack.translate(0.0F, -0.2F, 0.0F);
                pPoseStack.scale(1.1F, 1.1F, 1.1F);
                this.pItemInHandRenderer.renderItem(pLivingEntity, itemstack, ItemDisplayContext.HEAD, false, pPoseStack, pBuffer, pPackedLight);
                pPoseStack.popPose();
            }
        }
    }


}