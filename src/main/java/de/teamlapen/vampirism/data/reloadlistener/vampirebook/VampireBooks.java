package de.teamlapen.vampirism.data.reloadlistener.vampirebook;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.api.components.IVampireBook;
import de.teamlapen.vampirism.api.general.IBookContents;
import de.teamlapen.vampirism.core.ModVampireBooks;
import de.teamlapen.vampirism.misc.BookBackground;
import de.teamlapen.vampirism.misc.BookContents;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import org.jetbrains.annotations.NotNull;

public class VampireBooks {

    private final VampireBookBackgroundReloadListener bookBackgrounds = new VampireBookBackgroundReloadListener();
    private final VampireBookContentsReloadListener bookContents = new VampireBookContentsReloadListener();

    public void register(@NotNull AddClientReloadListenersEvent event) {
        event.addListener(VampireBookContentsReloadListener.ID, this.bookContents);
        event.addListener(VampireBookBackgroundReloadListener.ID, this.bookBackgrounds);
    }

    @NotNull
    public IBookContents getContentsFor(IVampireBook book) {
        return getContentsFor(book, VampLib.proxy.getActiveLanguage());
    }

    @NotNull
    public IBookContents getContentsFor(IVampireBook book, String languageCode) {
        VampireBookContentsReloadListener.TranslatedBookContent translatedBookContent = this.bookContents.getTranslatedBookContents().get(book.id());
        var content = translatedBookContent != null ? translatedBookContent.getContentsFor(languageCode) : null;
        if (content == null) {
            content = BookContents.EMPTY;
        }
        return content;
    }

    @NotNull
    public BookBackground getBackground(ResourceLocation id) {
        var backgrounds = this.bookBackgrounds.getBackgrounds();
        BookBackground bookBackground = backgrounds.get(id);
        if (bookBackground == null) {
            bookBackground = backgrounds.get(ModVampireBooks.DIARY_BACKGROUND);
        }
        return bookBackground;
    }
}
