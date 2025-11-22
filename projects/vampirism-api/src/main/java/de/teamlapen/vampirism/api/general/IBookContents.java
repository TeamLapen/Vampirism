package de.teamlapen.vampirism.api.general;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Defines the content structure of the vampire book, including its text, background id and images.
 */
public interface IBookContents {

    /**
     * The text of the book, split into pages based on the space available. Each string in the list represents a new page if a new book spread is needed.
     */
    List<String> contents();

    /**
     * The id of the book background, aka layout, to be used.
     */
    ResourceLocation background();

    /**
     * A list of image entries that should be rendered onto specific pages of the book. Can be used for fancy titles, photos, drawings, etc.
     * <p>
     * The text is not cropped automatically, so consider aligning them by hand so that the text doesn't overlap the image.
     */
    List<IImageEntry> images();

    /**
     * An image entry that is rendered on a specific page of the book.
     */
    interface IImageEntry {

        /**
         * Id of the image. Required for cases when images are overridden in localizations, e.g. the same titles.
         */
        int id();

        /**
         * The location of the image texture.
         */
        ResourceLocation texture();

        /**
         * The page number (starting from 0) the image will appear on. If the layout is two-page, the images from both of the visible pages will be rendered.
         */
        int page();

        /**
         * X-coordinate of the top left corner of the image going from the top left corner of the background.
         */
        int xOffset();

        /**
         * Y-coordinate of the top left corner of the image going from the top left corner of the background.
         */
        int yOffset();

        /**
         * The width of the image in pixels.
         */
        int width();

        /**
         * The height of the image in pixels.
         */
        int height();
    }
}
