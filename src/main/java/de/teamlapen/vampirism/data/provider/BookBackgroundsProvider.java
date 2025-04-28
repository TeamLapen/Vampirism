package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.core.ModVampireBooks;
import de.teamlapen.vampirism.misc.BookBackground;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class BookBackgroundsProvider extends de.teamlapen.vampirism.data.provider.parent.BookBackgroundsProvider {

    public BookBackgroundsProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void registerBackgrounds(BiConsumer<ResourceLocation, BookBackground> output) {
        output.accept(ModVampireBooks.DIARY_BACKGROUND_ID, ModVampireBooks.DIARY_BACKGROUND);
        output.accept(ModVampireBooks.LETTER_BACKGROUND_ID, ModVampireBooks.LETTER_BACKGROUND);
    }
}
