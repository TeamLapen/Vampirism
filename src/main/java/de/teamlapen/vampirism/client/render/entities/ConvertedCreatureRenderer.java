package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.entity.converted.ConvertedCreatureEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renders a converted creature, by rendering it's old creature
 */
@OnlyIn(Dist.CLIENT)
public class ConvertedCreatureRenderer extends EntityRenderer<ConvertedCreatureEntity> {
    public static boolean renderOverlay = false;

    public ConvertedCreatureRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }


    @Override
    public ResourceLocation getEntityTexture(ConvertedCreatureEntity entity) {
        return null;
    }

    @Override
    public void render(ConvertedCreatureEntity entity, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225623_6_) {
        CreatureEntity creature = entity.getOldCreature();
        if (creature != null) {
            creature.removed = false;
            renderOverlay = true;
            this.renderManager.renderEntityStatic(creature, 0, 0, 0, 0, 0, matrixStack, renderTypeBuffer, p_225623_6_);
            renderOverlay = false;
            creature.removed = true;
        }
    }
}
