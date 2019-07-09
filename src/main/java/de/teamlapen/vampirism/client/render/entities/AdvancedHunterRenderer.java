package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.model.BasicHunterModel;
import de.teamlapen.vampirism.client.render.LayerPlayerFaceOverlay;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renderer for the advanced hunter.
 * Similar to {@link BasicHunterRenderer}
 */
@OnlyIn(Dist.CLIENT)
public class AdvancedHunterRenderer extends BipedRenderer<AdvancedHunterEntity> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_hunter_base1.png");
    private final ResourceLocation textureExtra = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_hunter_extra.png");


    public AdvancedHunterRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BasicHunterModel(), 0.5F);
        if (!Configs.disable_advancedMobPlayerFaces) {
            this.addLayer(new LayerPlayerFaceOverlay<AdvancedHunterEntity, AdvancedHunterEntity>(this));

        }
    }

    @Override
    protected ResourceLocation getEntityTexture(AdvancedHunterEntity entity) {
        return texture;
    }

    @Override
    protected void renderLivingLabel(AdvancedHunterEntity entityIn, String str, double x, double y, double z, int maxDistance) {
        super.renderLivingLabel(entityIn, str, x, y, z, maxDistance / 4);
    }

    @Override
    protected void renderModel(AdvancedHunterEntity entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float partTicks) {

        super.renderModel(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, partTicks);
        bindTexture(textureExtra);
        ((BasicHunterModel) mainModel).renderHat(partTicks, entitylivingbaseIn.getHunterType());
        ((BasicHunterModel) mainModel).renderWeapons(partTicks, false);

    }
}
