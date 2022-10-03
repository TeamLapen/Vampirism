package de.teamlapen.vampirism.client.renderer.entity;

import de.teamlapen.vampirism.entity.DarkBloodProjectileEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DarkBloodProjectileRenderer extends EntityRenderer<DarkBloodProjectileEntity> {

    public DarkBloodProjectileRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context);

    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull DarkBloodProjectileEntity entity) {
        return null;
    }
}
