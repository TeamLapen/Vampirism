package de.teamlapen.vampirism.client.render;

import de.teamlapen.lib.lib.client.render.RenderUtil;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;


@SideOnly(Side.CLIENT)
public class LayerGlowingEyes<T extends EntityLivingBase> implements LayerRenderer<T> {
    private final ResourceLocation RESOURCE_LOCATION;
    private final RenderLivingBase<T> render;
    private float brightness = 120f;

    public LayerGlowingEyes(RenderLivingBase<T> spiderRendererIn, ResourceLocation eyes) {
        this.render = spiderRendererIn;
        this.RESOURCE_LOCATION = eyes;
    }

    @Override
    public void doRenderLayer(@Nonnull T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        RenderUtil.renderGlowing(render, RESOURCE_LOCATION, brightness, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
    }


    public boolean shouldCombineTextures() {
        return false;
    }

    public LayerGlowingEyes<T> setBrightness(float brightness) {
        this.brightness = brightness;
        return this;
    }
}