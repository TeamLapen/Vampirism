package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.IListBasedSpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IListBasedSpriteSetMock extends IListBasedSpriteSet {
    @Override
    default @NotNull List<TextureAtlasSprite> vampirism$getSprites() {
        return List.of();
    }
}
