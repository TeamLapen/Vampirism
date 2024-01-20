package de.teamlapen.vampirism.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.mixin.client.accessor.RenderLayerAccessor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Render the vampire overlay
 */
public class VampireEntityLayer<T extends LivingEntity, U extends EntityModel<T>> extends RenderLayer<T, U> {

    private final ResourceLocation texture;

    public VampireEntityLayer(@NotNull RenderLayerParent<T, U> entityRendererIn, ResourceLocation texture) {
        super(entityRendererIn);
        this.texture = texture;
    }

    @Deprecated
    public VampireEntityLayer(@NotNull RenderLayerParent<T, U> entityRendererIn, ResourceLocation texture, @SuppressWarnings("unused") boolean checkIfRender) {
        this(entityRendererIn, texture);
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, @NotNull MultiBufferSource iRenderTypeBuffer, int i, @NotNull T entity, float v, float v1, float v2, float v3, float v4, float v5) {
        if (!entity.isInvisible()) {
            RenderLayerAccessor.renderColoredCutoutModel(this.getParentModel(), this.texture, matrixStack, iRenderTypeBuffer, i, entity, 1, 1, 1);
        }
    }
}
