package de.teamlapen.vampirism.misc.extension.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IListBasedSpriteSet {

    @NotNull
    List<TextureAtlasSprite> vampirism$getSprites();
}
