package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.model.ModelBasicHunter;
import de.teamlapen.vampirism.client.render.LayerPlayerFaceOverlay;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.entity.hunter.EntityAdvancedHunter;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renderer for the advanced hunter.
 * Similar to {@link RenderBasicHunter}
 */
@OnlyIn(Dist.CLIENT)
public class RenderAdvancedHunter extends RenderBiped<EntityAdvancedHunter> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_hunter_base1.png");
    private final ResourceLocation textureExtra = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_hunter_extra.png");


    public RenderAdvancedHunter(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBasicHunter(), 0.5F);
        if (!Configs.disable_advancedMobPlayerFaces) {
            this.addLayer(new LayerPlayerFaceOverlay<EntityAdvancedHunter, EntityAdvancedHunter>(this));

        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAdvancedHunter entity) {
        return texture;
    }

    @Override
    protected void renderLivingLabel(EntityAdvancedHunter entityIn, String str, double x, double y, double z, int maxDistance) {
        super.renderLivingLabel(entityIn, str, x, y, z, maxDistance / 4);
    }

    @Override
    protected void renderModel(EntityAdvancedHunter entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float partTicks) {

        super.renderModel(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, partTicks);
        bindTexture(textureExtra);
        ((ModelBasicHunter) mainModel).renderHat(partTicks, entitylivingbaseIn.getHunterType());
        ((ModelBasicHunter) mainModel).renderWeapons(partTicks, false);

    }
}
