package de.teamlapen.lib.lib.client.render;

import de.teamlapen.lib.lib.entity.BasicAreaParticleCloud;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;


@OnlyIn(Dist.CLIENT)
public class RenderAreaParticleCloud extends EntityRenderer<BasicAreaParticleCloud> {
    public RenderAreaParticleCloud(EntityRendererProvider.Context context) {
        super(context);
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull BasicAreaParticleCloud entity) {
        return null;
    }
}
