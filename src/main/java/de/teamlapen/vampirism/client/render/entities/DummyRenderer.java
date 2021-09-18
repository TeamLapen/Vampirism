package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;


public class DummyRenderer<T extends Entity> extends EntityRenderer<T> {
    private final ResourceLocation TEX = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire.png");

    public DummyRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull T entity) {
        return TEX; //Dummy anyway
    }

    @Override
    public void render(@Nonnull T entityIn, float entityYaw, float partialTicks, @Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn, int packedLightIn) {

    }

    @Override
    public boolean shouldRender(@Nonnull T livingEntityIn, @Nonnull Frustum camera, double camX, double camY, double camZ) {
        return false;
    }
}
