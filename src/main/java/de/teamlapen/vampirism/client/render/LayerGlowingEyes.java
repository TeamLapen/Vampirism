package de.teamlapen.vampirism.client.render;

import de.teamlapen.lib.lib.client.render.RenderUtil;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;


@OnlyIn(Dist.CLIENT)
public class LayerGlowingEyes<T extends LivingEntity> implements LayerRenderer<T> {
    private final ResourceLocation RESOURCE_LOCATION;
    private final LivingRenderer<T> render;
    private float brightness = 120f;

    public LayerGlowingEyes(LivingRenderer<T> render, ResourceLocation eyes) {
        this.render = render;
        this.RESOURCE_LOCATION = eyes;
    }

    @Override
    public void render(@Nonnull T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        RenderUtil.renderGlowing(render, RESOURCE_LOCATION, brightness, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
    }

    public LayerGlowingEyes<T> setBrightness(float brightness) {
        this.brightness = brightness;
        return this;
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}