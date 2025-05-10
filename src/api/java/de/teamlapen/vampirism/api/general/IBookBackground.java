package de.teamlapen.vampirism.api.general;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface IBookBackground {

    ResourceLocation texture();

    Optional<ResourceLocation> textureFirstPage();

    Optional<ResourceLocation> textureLastPage();

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
