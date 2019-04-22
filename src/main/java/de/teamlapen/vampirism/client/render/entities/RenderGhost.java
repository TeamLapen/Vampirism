package de.teamlapen.vampirism.client.render.entities;


import de.teamlapen.vampirism.client.model.ModelGhost;
import de.teamlapen.vampirism.entity.EntityGhost;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author WILLIAM
 */
@OnlyIn(Dist.CLIENT)
public class RenderGhost extends RenderLiving<EntityGhost> {
    private static final ResourceLocation ghostTexture = new ResourceLocation(REFERENCE.MODID + ":textures/entity/ghost.png");

    public RenderGhost(RenderManager renderManager) {
        super(renderManager, new ModelGhost(), 0.3F);
    }


    @Override
    protected ResourceLocation getEntityTexture(EntityGhost entity) {
        return ghostTexture;
    }
}