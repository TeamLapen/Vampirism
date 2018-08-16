package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.client.model.ModelVillagerWithArms;
import de.teamlapen.vampirism.client.render.entities.RenderHunterVillager;
import de.teamlapen.vampirism.entity.hunter.EntityAggressiveVillager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Same as {@link LayerHeldItem} but for {@link ModelVillagerWithArms} model
 */
@SideOnly(Side.CLIENT)
public class LayerHeldItemVillager implements LayerRenderer<EntityAggressiveVillager> {

    private final RenderHunterVillager renderer;

    public LayerHeldItemVillager(RenderHunterVillager renderer) {
        this.renderer = renderer;
    }

    public void doRenderLayer(EntityAggressiveVillager entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        boolean flag = entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT;
        ItemStack itemstack = flag ? entitylivingbaseIn.getHeldItemOffhand() : entitylivingbaseIn.getHeldItemMainhand();
        ItemStack itemstack1 = flag ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();

        if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
            GlStateManager.pushMatrix();

            if (this.renderer.getMainModel().isChild) {
                float f = 0.5F;
                GlStateManager.translate(0.0F, 0.625F, 0.0F);
                GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
                GlStateManager.scale(f, f, f);
            }

            this.renderHeldItem(entitylivingbaseIn, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            this.renderHeldItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }

    private void renderHeldItem(EntityAggressiveVillager p_188358_1_, ItemStack stack, ItemCameraTransforms.TransformType p_188358_3_, EnumHandSide p_188358_4_) {
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            ((ModelVillagerWithArms) this.renderer.getMainModel()).postRenderArm(0.0625F, p_188358_4_);

            if (p_188358_1_.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            boolean flag = p_188358_4_ == EnumHandSide.LEFT;
            GlStateManager.translate(flag ? -0.0925F : 0.0925F, 0.125F, -0.525F);
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(p_188358_1_, stack, p_188358_3_, flag);
            GlStateManager.popMatrix();
        }
    }

}
