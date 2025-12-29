package de.teamlapen.vampirism.misc.mixin.client;

import de.teamlapen.vampirism.misc.extension.client.IListBasedSpriteSet;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ParticleResources.MutableSpriteSet.class)
public abstract class MutableSpriteSetMixin implements IListBasedSpriteSet {
    @Shadow
    private List<TextureAtlasSprite> sprites;

    @Override
    public @NotNull List<TextureAtlasSprite> vampirism$getSprites() {
        return this.sprites != null ? this.sprites : List.of();
    }
}
