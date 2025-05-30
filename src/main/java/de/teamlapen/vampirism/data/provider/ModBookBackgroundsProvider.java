package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.core.ModVampireBooks;
import de.teamlapen.vampirism.data.provider.parent.BookBackgroundsProvider;
import de.teamlapen.vampirism.misc.BookBackground;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class ModBookBackgroundsProvider extends BookBackgroundsProvider {

    public ModBookBackgroundsProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void registerBackgrounds(BiConsumer<ResourceLocation, BookBackground> output) {
        output.accept(ModVampireBooks.DIARY_BACKGROUND, BookBackground
                .builder(VResourceLocation.mod("textures/gui/vampire_books/diary.png"), 304, 200)
                .textureFirstPage(VResourceLocation.mod("textures/gui/vampire_books/diary_first.png"))
                .textureLastPage(VResourceLocation.mod("textures/gui/vampire_books/diary_last.png"))
                .build());
        output.accept(ModVampireBooks.LETTER_BACKGROUND, BookBackground
                .builder(VResourceLocation.mod("textures/gui/vampire_books/letter.png"), 177, 200)
                .twoPages(false)
                .textWidth(154).textHeight(160).leftPageTextX(15).textY(12)
                .pageNumberXOffset(88).pageNumberYOffset(15).pageButtonXOffset(10).pageButtonYOffset(5)
                .build());
        output.accept(ModVampireBooks.POSTER_BACKGROUND, BookBackground
                .builder(VResourceLocation.mod("textures/gui/vampire_books/poster.png"), 177, 200)
                .twoPages(false)
                .textWidth(154).textHeight(160).leftPageTextX(15).textY(12)
                .pageNumberXOffset(88).pageNumberYOffset(15).pageButtonXOffset(10).pageButtonYOffset(5)
                .build());
    }
}
