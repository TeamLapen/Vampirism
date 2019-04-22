package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.render.LayerAdvancedVampireEye;
import de.teamlapen.vampirism.client.render.LayerPlayerFaceOverlay;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.entity.vampire.EntityAdvancedVampire;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Render the advanced vampire with overlays
 */
@OnlyIn(Dist.CLIENT)
public class RenderAdvancedVampire extends RenderBiped<EntityAdvancedVampire> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire.png");

    public RenderAdvancedVampire(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBiped(0F, 0F, 64, 64), 0.5F);
        if (!Configs.disable_advancedMobPlayerFaces) {
            this.addLayer(new LayerPlayerFaceOverlay<EntityAdvancedVampire, EntityAdvancedVampire>(this));
            this.addLayer(new LayerAdvancedVampireEye(this));

        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAdvancedVampire entity) {
        return texture;
    }

    @Override
    protected void renderLivingLabel(EntityAdvancedVampire entityIn, String str, double x, double y, double z, int maxDistance) {
        super.renderLivingLabel(entityIn, str, x, y, z, maxDistance / 4);
    }


}
