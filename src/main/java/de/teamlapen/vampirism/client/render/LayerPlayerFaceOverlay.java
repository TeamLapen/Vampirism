package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.util.IPlayerFace;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Renders an overlay over the entities face
 *
 * @param <T> Has to be the same as Q
 * @param <Q> Has to be the same as T
 */
@SideOnly(Side.CLIENT)
public class LayerPlayerFaceOverlay<T extends EntityCreature, Q extends IPlayerFace> implements LayerRenderer<T> {

    private final RenderBiped<T> renderBiped;

    public LayerPlayerFaceOverlay(RenderBiped<T> renderBiped) {
        this.renderBiped = renderBiped;
    }

    @Override
    public void doRenderLayer(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        String name = ((Q) entitylivingbaseIn).getPlayerFaceName();
        ResourceLocation loc = name == null ? null : AbstractClientPlayer.getLocationSkin(name);
        AbstractClientPlayer.getDownloadImageSkin(loc, name);
        if (loc != null) {
            renderBiped.bindTexture(loc);
            GlStateManager.pushMatrix();
            if (entitylivingbaseIn.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }
            ((ModelBiped) this.renderBiped.getMainModel()).bipedHead.render(scale);
            GlStateManager.popMatrix();
        }

    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
