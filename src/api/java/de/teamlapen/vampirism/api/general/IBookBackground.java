package de.teamlapen.vampirism.api.general;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface IBookBackground {

    ResourceLocation texture();

    @Nullable ResourceLocation textureFirstPage();

    @Nullable ResourceLocation textureLastPage();

    boolean twoPages();

    int textureWidth();

    int textureHeight();

    int textColor();

    int textWidth();

    int textHeight();

    int firstPageTextX();

    int leftPageTextX();

    int rightPageTextX();

    int textY();

    int pageNumberXOffset();

    int pageNumberYOffset();

    int pageButtonXOffset();

    int pageButtonYOffset();
}
