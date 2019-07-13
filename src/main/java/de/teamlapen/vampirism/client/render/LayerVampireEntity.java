package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.client.render.entities.ConvertedCreatureRenderer;
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
public class LayerVampireEntity extends LayerRenderer<CreatureEntity, EntityModel<CreatureEntity>> {

    private final ResourceLocation overlay;
    private final boolean checkIfRender;

    /**
     * @param overlay
     * @param checkIfRender If it should check if {@link ConvertedCreatureRenderer#renderOverlay} is true
     */
    public LayerVampireEntity(IEntityRenderer<CreatureEntity, EntityModel<CreatureEntity>> entityRendererIn, ResourceLocation overlay, boolean checkIfRender) {
        super(entityRendererIn);
        this.overlay = overlay;
        this.checkIfRender = checkIfRender;
    }

    @Override
    public void render(CreatureEntity entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        if (!entitylivingbaseIn.isInvisible() && (!checkIfRender || ConvertedCreatureRenderer.renderOverlay)) {
            bindTexture(overlay);
            getEntityModel().render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
