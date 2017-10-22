package de.teamlapen.lib.lib.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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
        GlStateManager.enableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

        if (entityInvisible) {
            GlStateManager.depthMask(false);
        } else {
            GlStateManager.depthMask(true);
        }


        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightness, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
    }

    private static void endGlowing(int brightnessForRender){
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
        int i = brightnessForRender;
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        GlStateManager.disableBlend();
    }


}
