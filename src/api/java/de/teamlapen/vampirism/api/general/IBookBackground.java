package de.teamlapen.vampirism.api.general;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * Defines the layout and appearance of the vampire book GUI.
 */
public interface IBookBackground {

    /**
     * The main texture used for the background.
     */
    ResourceLocation texture();

    /**
     * Optional texture used for the first page of the book. Must have the same dimensions as the main one.
     * <p>
     * If not present, {@link #texture()} is used.
     */
    Optional<ResourceLocation> textureFirstPage();

    /**
     * Optional texture used for the last page of the book. Must have the same dimensions as the main one.
     * <p>
     * If not present, {@link #texture()} is used.
     */
    Optional<ResourceLocation> textureLastPage();

    /**
     * Determines whether the book displays one page at a time or two.
     * <p>
     * A two-page layout shows the left and right pages together, like an actual book or "diary" if you wish. Single page layouts are usually used for letters, notes and posters.
     */
    boolean twoPages();

    /**
     * The width of the texture (in pixels).
     */
    int textureWidth();

    /**
     * The height of the texture (in pixels).
     */
    int textureHeight();

    /**
     * The color used to render all text and page numbers. Should be an RGB integer (e.g., 0x404040).
     */
    int textColor();

    /**
     * The width in pixels of a block of text on a single page.
     */
    int textWidth();

    /**
     * The height in pixels of a block of text on a single page. Used to divide text between pages.
     * Consider setting this to a multiple of 10, as the height of a single line is 10.
     */
    int textHeight();

    /**
     * X-coordinate (relative to top left of the GUI) where the text of the first page begins.
     * Only used in the two-page layout.
     */
    int firstPageTextX();

    /**
     * X-coordinate where the text of the left page begins (in the two-page layout).
     * Also used for single-page layouts as the text offset for all pages.
     */
    int leftPageTextX();

    /**
     * X-coordinate where the text of the right page begins (in the two-page layout).
     * Only used in the two-page layout.
     */
    int rightPageTextX();

    /**
     * Y-coordinate (relative to top left of the GUI) where the text of all pages starts.
     */
    int textY();

    /**
     * X-offset from the edges of the GUI for page numbers.
     * Used to place page numbers near the bottom corners of the book. Just set it to half of the page width.
     */
    int pageNumberXOffset();

    /**
     * Y-offset from the bottom of the GUI for page numbers.
     */
    int pageNumberYOffset();

    /**
     * X-offset from the edges of the GUI for the page arrow buttons (back/forward).
     * Linked to the bottom left, if back, or bottom right, if forward, corner of the button texture.
     */
    int pageButtonXOffset();

    /**
     * Y-offset from the bottom edge of the GUI for the page arrow buttons.
     */
    int pageButtonYOffset();
}
