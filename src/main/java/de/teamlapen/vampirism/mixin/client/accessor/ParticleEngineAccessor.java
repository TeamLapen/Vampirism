package de.teamlapen.vampirism.mixin.client.accessor;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ParticleEngine.class)
public interface ParticleEngineAccessor {

    @Accessor("textureAtlas")
    TextureAtlas getTextureAtlas();
}
