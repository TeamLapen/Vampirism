package de.teamlapen.lib.lib.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderUtil {

    /**
     * Render the given model part using the given texture with a glowing lightmap (like vanilla spider)
     *
     * @param brightness Between 0 and 255f
     */
    public static <T extends EntityLivingBase> void renderGlowing(RenderLivingBase<T> render, ModelRenderer modelPart, ResourceLocation texture, float brightness, T entity, float scale) {
        render.bindTexture(texture);

        startGlowing(entity.isInvisible(), brightness);
        modelPart.render(scale);
        endGlowing(entity.getBrightnessForRender());

    }

    /**
     * Render the complete model using the given texture with a glowing lightmap (like vanilla spider)
     *
     * @param brightness Between 0 and 255f
     */
    public static <T extends EntityLivingBase> void renderGlowing(RenderLivingBase<T> render, ResourceLocation texture, float brightness, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        render.bindTexture(texture);
        render.bindTexture(texture);
        startGlowing(entity.isInvisible(), brightness);
        render.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        endGlowing(entity.getBrightnessForRender());
    }

    private static void startGlowing(boolean entityInvisible, float brightness){
        GlStateManager.enableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

        if (entityInvisible) {
            GlStateManager.depthMask(false);
        } else {
            GlStateManager.depthMask(true);
        }
        OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, brightness, 0.0F);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().entityRenderer.setupFogColor(true);
    }

    private static void endGlowing(int brightnessForRender){
        Minecraft.getInstance().entityRenderer.setupFogColor(false);
        int j = brightnessForRender % 65536;
        int k = brightnessForRender / 65536;
        OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, j, k);
        GlStateManager.disableBlend();
    }


}
