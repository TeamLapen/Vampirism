package de.teamlapen.vampirism.client.render;

import com.mojang.blaze3d.platform.GlStateManager;

import de.teamlapen.vampirism.util.IPlayerFace;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renders an overlay over the entities face
 *
 */
@OnlyIn(Dist.CLIENT)
public class LayerPlayerFaceOverlay<T extends CreatureEntity & IPlayerFace> extends LayerRenderer<T, BipedModel<T>> {

    public LayerPlayerFaceOverlay(IEntityRenderer<T, BipedModel<T>> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        //TODO 1.14 check overlay rendering
        String name = entitylivingbaseIn.getPlayerFaceName();
        ResourceLocation loc = name == null ? null : AbstractClientPlayerEntity.getLocationSkin(name);
        AbstractClientPlayerEntity.getDownloadImageSkin(loc, name);
        if (loc != null) {
            bindTexture(loc);
            GlStateManager.pushMatrix();
            if (entitylivingbaseIn.isSneaking()) {
                GlStateManager.translatef(0.0F, 0.2F, 0.0F);
            }
            getEntityModel().bipedHead.render(scale);
            GlStateManager.popMatrix();
        }

    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
