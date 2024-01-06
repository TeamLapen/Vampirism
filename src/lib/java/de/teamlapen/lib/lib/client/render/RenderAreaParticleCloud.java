package de.teamlapen.lib.lib.client.render;

import de.teamlapen.lib.lib.entity.BasicAreaParticleCloud;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;


public class RenderAreaParticleCloud extends EntityRenderer<BasicAreaParticleCloud> {
    public RenderAreaParticleCloud(EntityRendererProvider.@NotNull Context context) {
        super(context);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull BasicAreaParticleCloud entity) {
        return null;
    }
}
