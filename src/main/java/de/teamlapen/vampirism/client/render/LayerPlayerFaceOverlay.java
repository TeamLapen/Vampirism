package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.util.IPlayerFace;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renders an overlay over the entities face
 *
 * @param <T> Has to be the same as Q
 * @param <Q> Has to be the same as T
 */
@OnlyIn(Dist.CLIENT)
public class LayerPlayerFaceOverlay<T extends CreatureEntity, Q extends IPlayerFace> implements LayerRenderer<T> {

    private final BipedRenderer<T> renderBiped;

    public LayerPlayerFaceOverlay(BipedRenderer<T> renderBiped) {
        this.renderBiped = renderBiped;
    }

    @Override
    public void render(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        String name = ((Q) entitylivingbaseIn).getPlayerFaceName();
        ResourceLocation loc = name == null ? null : AbstractClientPlayerEntity.getLocationSkin(name);
        AbstractClientPlayerEntity.getDownloadImageSkin(loc, name);
        if (loc != null) {
            renderBiped.bindTexture(loc);
            GlStateManager.pushMatrix();
            if (entitylivingbaseIn.isSneaking()) {
                GlStateManager.translatef(0.0F, 0.2F, 0.0F);
            }
            ((BipedModel) this.renderBiped.getMainModel()).bipedHead.render(scale);
            GlStateManager.popMatrix();
        }

    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
