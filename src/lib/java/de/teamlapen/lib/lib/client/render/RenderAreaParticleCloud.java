package de.teamlapen.lib.lib.client.render;

import de.teamlapen.lib.lib.entity.BasicEntityAreaParticleCloud;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;


@OnlyIn(Dist.CLIENT)
public class RenderAreaParticleCloud extends Render<BasicEntityAreaParticleCloud> {
    public RenderAreaParticleCloud(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull BasicEntityAreaParticleCloud entity) {
        return null;
    }
}
