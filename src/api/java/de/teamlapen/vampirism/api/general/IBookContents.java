package de.teamlapen.vampirism.api.general;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface IBookContents {

    List<String> contents();

    ResourceLocation background();

    List<IImageEntry> images();

    interface IImageEntry {

        int id();

        ResourceLocation texture();

        int page();

        int x();

        int y();

        int width();

        int height();
    }
}
