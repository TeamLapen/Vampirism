package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.DarkBloodProjectileEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DarkBloodProjectileRenderer extends EntityRenderer<DarkBloodProjectileEntity> {

    public DarkBloodProjectileRenderer(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn);

    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    public ResourceLocation getTextureLocation(DarkBloodProjectileEntity entity) {
        return null;
    }
}
