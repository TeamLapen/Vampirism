package de.teamlapen.vampirism.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.renderer.entity.ConvertedCreatureRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Render the vampire overlay
 */
@OnlyIn(Dist.CLIENT)
public class VampireEntityLayer<T extends LivingEntity, U extends EntityModel<T>> extends RenderLayer<T, U> {

    private final ResourceLocation overlay;
    private final boolean checkIfRender;

    /**
     * @param checkIfRender If it should check if {@link ConvertedCreatureRenderer#renderOverlay} is true
     */
    public VampireEntityLayer(@NotNull RenderLayerParent<T, U> entityRendererIn, ResourceLocation overlay, boolean checkIfRender) {
        super(entityRendererIn);
        this.overlay = overlay;
        this.checkIfRender = checkIfRender;
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, @NotNull MultiBufferSource iRenderTypeBuffer, int i, @NotNull T entity, float v, float v1, float v2, float v3, float v4, float v5) {
        //noinspection ConstantValue
        if (!entity.isInvisible() && (!checkIfRender || ConvertedCreatureRenderer.renderOverlay) && Minecraft.getInstance().textureManager.getTexture(overlay) != null) {
            renderColoredCutoutModel(this.getParentModel(), overlay, matrixStack, iRenderTypeBuffer, i, entity, 1, 1, 1);
        }
    }
}
