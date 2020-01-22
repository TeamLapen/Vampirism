package de.teamlapen.lib.lib.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderUtil {

    /**
     * Render the given model part using the given texture with a glowing lightmap (like vanilla spider)
     * <p>
     * The Texture must set before calling this method
     *
     * @param brightness Between 0 and 255f
     */
    public static <T extends LivingEntity> void renderGlowing(ModelRenderer modelPart, float brightness, T entity, float scale) {
        startGlowing(entity.isInvisible(), brightness);
        modelPart.render(scale);
        endGlowing(entity.getBrightnessForRender());

    }

    /**
     * Render the complete model using the given texture with a glowing lightmap (like vanilla spider)
     *
     * @param brightness Between 0 and 255f
     */
    public static <T extends LivingEntity> void renderGlowing(IEntityRenderer<T, EntityModel<T>> render, ResourceLocation texture, float brightness, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        render.bindTexture(texture);
        render.bindTexture(texture);
        startGlowing(entity.isInvisible(), brightness);
        render.getEntityModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        endGlowing(entity.getBrightnessForRender());
    }

    private static void startGlowing(boolean entityInvisible, float brightness) {
        GlStateManager.enableBlend();
        RenderSystem.enableAlphaTest();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

        if (entityInvisible) {
            GlStateManager.depthMask(false);
        } else {
            GlStateManager.depthMask(true);
        }
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, brightness, 0.0F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().gameRenderer.setupFogColor(true);
    }

    private static void endGlowing(int brightnessForRender) {
        Minecraft.getInstance().gameRenderer.setupFogColor(false);
        int j = brightnessForRender % 65536;
        int k = brightnessForRender / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, j, k);
        GlStateManager.disableBlend();
    }


}
