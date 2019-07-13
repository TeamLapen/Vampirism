package de.teamlapen.vampirism.client.render.entities;


import de.teamlapen.vampirism.client.model.GhostModel;
import de.teamlapen.vampirism.entity.GhostEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author WILLIAM
 */
@OnlyIn(Dist.CLIENT)
public class GhostRenderer extends MobRenderer<GhostEntity, GhostModel<GhostEntity>> {
    private static final ResourceLocation ghostTexture = new ResourceLocation(REFERENCE.MODID + ":textures/entity/ghost.png");

    public GhostRenderer(EntityRendererManager renderManager) {
        super(renderManager, new GhostModel(), 0.3F);
    }


    @Override
    protected ResourceLocation getEntityTexture(GhostEntity entity) {
        return ghostTexture;
    }
}