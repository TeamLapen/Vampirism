package de.teamlapen.vampirism.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.components.IVampireBook;
import de.teamlapen.vampirism.api.general.IBookContents;
import de.teamlapen.vampirism.core.ModVampireBooks;
import de.teamlapen.vampirism.misc.BookBackground;
import de.teamlapen.vampirism.misc.BookContents;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VampireBookLoader {

    private static final Logger LOGGER = LogManager.getLogger();

    public static BookContents loadBookContents(IVampireBook vampireBook) {
        String language = VampLib.proxy.getActiveLanguage();

        String defaultPath = getContentsFilePath(vampireBook, "en_us");
        String localizedPath = getContentsFilePath(vampireBook, language);

        BookContents defaultContents = loadContentsFile(defaultPath);
        BookContents localizedContents = language.equals("en_us") ? BookContents.EMPTY : loadContentsFile(localizedPath);

        List<String> resultContents = !localizedContents.contents().isEmpty() ? localizedContents.contents() : defaultContents.contents();

        Map<Integer, IBookContents.IImageEntry> resultImages = new LinkedHashMap<>();
        for (IBookContents.IImageEntry entry : defaultContents.images()) {
            resultImages.put(entry.id(), entry);
        }
        for (IBookContents.IImageEntry entry : localizedContents.images()) {
            resultImages.put(entry.id(), entry);
        }

        return new BookContents(resultContents.isEmpty() ? List.of("§4Failed to load the contents file: " + localizedPath + "§r") : resultContents, new ArrayList<>(resultImages.values()));
    }

    private static String getContentsFilePath(IVampireBook vampireBook, String languageCode) {
        return "/assets/" + vampireBook.id().getNamespace() + "/vampire_books/" + vampireBook.id().getPath() + "/" + languageCode +".json";
    }

    private static BookContents loadContentsFile(String path) {
        try (InputStream inputStream = VampirismMod.class.getResourceAsStream(path)) {
            if (inputStream != null) {
                try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                    return BookContents.decode(json);
                } catch (Exception exception) {
                    LOGGER.warn("Failed to read vampire book file: {}", path, exception);
                }
            } else {
                LOGGER.warn("Vampire book file not found at path: {}", path);
            }
        } catch (IOException exception) {
            LOGGER.warn("Failed to start an input stream for vampire book file: {}", path, exception);
        }

        return BookContents.EMPTY;
    }

    public static BookBackground loadBackground(ResourceLocation backgroundId) {
        String path = getBackgroundFilePath(backgroundId);

        try (InputStream inputStream = VampirismMod.class.getResourceAsStream(path)) {
            if (inputStream != null) {
                try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    return BookBackground.decode(jsonElement.getAsJsonObject());
                } catch (Exception exception) {
                    LOGGER.error("Failed to read background file '{}'", path, exception);
                }
            } else {
                LOGGER.warn("Background file not found at path: {}", path);
            }
        } catch (IOException exception) {
            LOGGER.error("Failed to start an input stream for background file: {}", path, exception);
        }

        return ModVampireBooks.DIARY_BACKGROUND;
    }

    private static String getBackgroundFilePath(ResourceLocation backgroundId) {
        return "/assets/" + backgroundId.getNamespace() + "/vampire_book_backgrounds/" + backgroundId.getPath() +".json";
    }
}
