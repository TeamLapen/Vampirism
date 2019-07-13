package de.teamlapen.vampirism.client.render;

import de.teamlapen.lib.lib.client.render.RenderUtil;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;


@OnlyIn(Dist.CLIENT)
public class LayerGlowingEyes<T extends LivingEntity> extends LayerRenderer<T, EntityModel<T>> {
    private final ResourceLocation RESOURCE_LOCATION;
    private final IEntityRenderer<T, EntityModel<T>> entityRendererIn;
    private float brightness = 120f;

    public LayerGlowingEyes(IEntityRenderer<T, EntityModel<T>> entityRendererIn, ResourceLocation eyes) {
        super(entityRendererIn);
        this.entityRendererIn = entityRendererIn;
        this.RESOURCE_LOCATION = eyes;
    }

    @Override
    public void render(@Nonnull T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        RenderUtil.renderGlowing(entityRendererIn, RESOURCE_LOCATION, brightness, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
    }

    public LayerGlowingEyes<T> setBrightness(float brightness) {
        this.brightness = brightness;
        return this;
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}