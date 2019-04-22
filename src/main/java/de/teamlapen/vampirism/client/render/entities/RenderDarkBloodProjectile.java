package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.EntityDarkBloodProjectile;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderDarkBloodProjectile extends Render<EntityDarkBloodProjectile> {

    public RenderDarkBloodProjectile(RenderManager renderManagerIn) {
        super(renderManagerIn);

    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityDarkBloodProjectile entity) {
        return null;
    }
}
