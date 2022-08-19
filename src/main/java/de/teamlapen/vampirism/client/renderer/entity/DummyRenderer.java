package de.teamlapen.vampirism.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;


public class DummyRenderer<T extends Entity> extends EntityRenderer<T> {
    private final ResourceLocation TEX = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire.png");

    public DummyRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull T entity) {
        return TEX; //Dummy anyway
    }

    @Override
    public void render(@NotNull T entityIn, float entityYaw, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {

    }

    @Override
    public boolean shouldRender(@NotNull T livingEntityIn, @NotNull Frustum camera, double camX, double camY, double camZ) {
        return false;
    }
}
