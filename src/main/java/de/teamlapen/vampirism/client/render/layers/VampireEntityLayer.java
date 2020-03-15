package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.render.entities.ConvertedCreatureRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Render the vampire overlay
 */
@OnlyIn(Dist.CLIENT)
public class VampireEntityLayer<T extends CreatureEntity, U extends EntityModel<T>> extends LayerRenderer<T, U> {

    private final ResourceLocation overlay;
    private final boolean checkIfRender;

    /**
     * @param overlay
     * @param checkIfRender If it should check if {@link ConvertedCreatureRenderer#renderOverlay} is true
     */
    public VampireEntityLayer(IEntityRenderer<T, U> entityRendererIn, ResourceLocation overlay, boolean checkIfRender) {
        super(entityRendererIn);
        this.overlay = overlay;
        this.checkIfRender = checkIfRender;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, T entity, float v, float v1, float v2, float v3, float v4, float v5) {
        if (!entity.isInvisible() && (!checkIfRender || ConvertedCreatureRenderer.renderOverlay)) {
            renderCutoutModel(this.getEntityModel(), overlay, matrixStack, iRenderTypeBuffer, i, entity, 1, 1, 1);
        }
    }
}
