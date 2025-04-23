package de.teamlapen.vampirism.api.components;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.Optional;

public interface IVampireBook {

    ResourceLocation id();

    Component author();

    ResourceLocation backgroundId();

    boolean is(TagKey<IVampireBook> tag, RegistryAccess registryAccess);

    boolean isEmpty();

    MutableComponent title();

    List<MutableComponent> contents();

    IBookBackground background();

    interface IBookBackground {

        ResourceLocation texture();

        Optional<ResourceLocation> textureFirstPage();

        Optional<ResourceLocation> textureLastPage();

        boolean twoPages();

        int textureWidth();

        int textureHeight();

        IBookTextProperties textProperties();

        IBookPageNumbering pageNumbering();
    }

    interface IBookTextProperties {

        int textColor();

        int textWidth();

        int textHeight();

        int firstPageTextX();

        int leftPageTextX();

        int rightPageTextX();

        int textY();
    }

    interface IBookPageNumbering {

        int pageNumberXOffset();

        int pageNumberYOffset();

        int pageButtonXOffset();

        int pageButtonYOffset();
    }
}
