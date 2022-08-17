package de.teamlapen.lib.lib.client.render;

import de.teamlapen.lib.lib.entity.BasicAreaParticleCloud;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;


@OnlyIn(Dist.CLIENT)
public class RenderAreaParticleCloud extends EntityRenderer<BasicAreaParticleCloud> {
    public RenderAreaParticleCloud(EntityRendererProvider.Context context) {
        super(context);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull BasicAreaParticleCloud entity) {
        return null;
    }
}
