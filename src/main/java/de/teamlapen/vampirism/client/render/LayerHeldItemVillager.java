package de.teamlapen.vampirism.client.render;

import com.mojang.blaze3d.platform.GlStateManager;

import de.teamlapen.vampirism.client.model.VillagerWithArmsModel;
import de.teamlapen.vampirism.entity.hunter.AggressiveVillagerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Same as {@link HeldItemLayer} but for {@link VillagerWithArmsModel} model
 */
@OnlyIn(Dist.CLIENT)
public class LayerHeldItemVillager extends LayerRenderer<AggressiveVillagerEntity, VillagerModel<AggressiveVillagerEntity>> {

    public LayerHeldItemVillager(IEntityRenderer<AggressiveVillagerEntity, VillagerModel<AggressiveVillagerEntity>> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(AggressiveVillagerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
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

    private void renderHeldItem(AggressiveVillagerEntity p_188358_1_, ItemStack stack, ItemCameraTransforms.TransformType p_188358_3_, HandSide p_188358_4_) {
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            ((VillagerWithArmsModel) getEntityModel()).postRenderArm(0.0625F, p_188358_4_);

            if (p_188358_1_.isSneaking()) {
                GlStateManager.translatef(0.0F, 0.2F, 0.0F);
            }

            GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            boolean flag = p_188358_4_ == HandSide.LEFT;
            GlStateManager.translatef(flag ? -0.0925F : 0.0925F, 0.125F, -0.525F);
            Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(p_188358_1_, stack, p_188358_3_, flag);
            GlStateManager.popMatrix();
        }
    }

}
