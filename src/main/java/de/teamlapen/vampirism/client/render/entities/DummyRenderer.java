package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;


public class DummyRenderer<T extends Entity> extends EntityRenderer<T> {
    private final ResourceLocation TEX = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire.png");

    public DummyRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return TEX; //Dummy anyway
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

    }

    @Override
    public boolean shouldRender(T livingEntityIn, ClippingHelperImpl camera, double camX, double camY, double camZ) {
        return false;
    }
}
