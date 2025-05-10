package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.misc.BookBackground;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class BookBackgroundsProvider extends de.teamlapen.vampirism.data.provider.parent.BookBackgroundsProvider {

    public static final ResourceLocation DIARY_BACKGROUND_ID = VResourceLocation.mod("diary");
    public static final BookBackground DIARY_BACKGROUND = BookBackground
            .builder(VResourceLocation.mod("textures/gui/vampire_books/diary.png"), 304, 200)
            .textureFirstPage(VResourceLocation.mod("textures/gui/vampire_books/diary_first.png"))
            .textureLastPage(VResourceLocation.mod("textures/gui/vampire_books/diary_last.png"))
            .build();

    public static final ResourceLocation LETTER_BACKGROUND_ID = VResourceLocation.mod("letter");
    public static final BookBackground LETTER_BACKGROUND = BookBackground
            .builder(VResourceLocation.mod("textures/gui/vampire_books/letter.png"), 177, 200)
            .twoPages(false)
            .textWidth(154).textHeight(160).leftPageTextX(15).textY(12)
            .pageNumberXOffset(88).pageNumberYOffset(15).pageButtonXOffset(10).pageButtonYOffset(5)
            .build();

    public static final ResourceLocation POSTER_BACKGROUND_ID = VResourceLocation.mod("poster");
    public static final BookBackground POSTER_BACKGROUND = BookBackground
            .builder(VResourceLocation.mod("textures/gui/vampire_books/poster.png"), 177, 200)
            .twoPages(false)
            .textWidth(154).textHeight(160).leftPageTextX(15).textY(12)
            .pageNumberXOffset(88).pageNumberYOffset(15).pageButtonXOffset(10).pageButtonYOffset(5)
            .build();

    public BookBackgroundsProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void registerBackgrounds(BiConsumer<ResourceLocation, BookBackground> output) {
        output.accept(DIARY_BACKGROUND_ID, DIARY_BACKGROUND);
        output.accept(LETTER_BACKGROUND_ID, LETTER_BACKGROUND);
        output.accept(POSTER_BACKGROUND_ID, POSTER_BACKGROUND);
    }
}
