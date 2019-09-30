package de.teamlapen.vampirism.client.render;

import com.mojang.blaze3d.platform.GlStateManager;

import de.teamlapen.vampirism.client.model.VillagerWithArmsModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Same as {@link HeldItemLayer} but for {@link VillagerWithArmsModel} model
 */
@OnlyIn(Dist.CLIENT)
public class LayerHeldItemVillager extends LayerRenderer<VillagerEntity, VillagerModel<VillagerEntity>> {

    public LayerHeldItemVillager(VillagerRenderer entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(VillagerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        boolean flag = entitylivingbaseIn.getPrimaryHand() == HandSide.RIGHT;
        ItemStack itemstack = flag ? entitylivingbaseIn.getHeldItemOffhand() : entitylivingbaseIn.getHeldItemMainhand();
        ItemStack itemstack1 = flag ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();

        if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
            GlStateManager.pushMatrix();

            if (getEntityModel().isChild) {
                float f = 0.5F;
                GlStateManager.translatef(0.0F, 0.625F, 0.0F);
                GlStateManager.rotatef(-20.0F, -1.0F, 0.0F, 0.0F);
                GlStateManager.scalef(f, f, f);
            }

            this.renderHeldItem(entitylivingbaseIn, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HandSide.RIGHT);
            this.renderHeldItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HandSide.LEFT);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }

    private void renderHeldItem(VillagerEntity entity, ItemStack stack, ItemCameraTransforms.TransformType transformType, HandSide handSide) {
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            ((VillagerWithArmsModel) getEntityModel()).postRenderArm(0.0625F, handSide);

            if (entity.isSneaking()) {
                GlStateManager.translatef(0.0F, 0.2F, 0.0F);
            }

            GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            boolean flag = handSide == HandSide.LEFT;
            GlStateManager.translatef(flag ? -0.0925F : 0.0925F, 0.125F, -0.525F);
            Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entity, stack, transformType, flag);
            GlStateManager.popMatrix();
        }
    }

}
