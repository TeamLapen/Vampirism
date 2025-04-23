package de.teamlapen.vampirism.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.components.IVampireBook;
import de.teamlapen.vampirism.items.component.VampireBook;
import io.netty.handler.codec.DecoderException;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class VampireBookLoader {

    private static final Logger LOGGER = LogManager.getLogger();

    public static List<String> loadBookContents(IVampireBook vampireBook) {
        String path = getContentsFilePath(vampireBook, VampLib.proxy.getActiveLanguage());

        if (VampirismMod.class.getResource(path) == null) {
            path = getContentsFilePath(vampireBook, "en_us");
        }

        List<String> contents = loadContentsFile(path);
        return contents.isEmpty() ? List.of("§4Failed to load the contents file: " + path + "§r") : contents;
    }

    private static String getContentsFilePath(IVampireBook vampireBook, String languageCode) {
        return "/assets/" + vampireBook.id().getNamespace() + "/vampire_books/" + vampireBook.id().getPath() + "/" + languageCode +".json";
    }

    private static List<String> loadContentsFile(String path) {
        List<String> lines = new ArrayList<>();

        try (InputStream inputStream = VampirismMod.class.getResourceAsStream(path)) {
            if (inputStream != null) {
                try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    lines = Codec.STRING.listOf().parse(new Dynamic<>(JsonOps.INSTANCE, jsonElement)).getOrThrow();
                } catch (Exception exception) {
                    LOGGER.warn("Failed to read vampire book file: {}", path, exception);
                }
            } else {
                LOGGER.warn("Vampire book file not found at path: {}", path);
            }
        } catch (IOException exception) {
            LOGGER.warn("Failed to start an input stream for vampire book file: {}", path, exception);
        }

        return lines;
    }

    public static VampireBook.IBookBackground loadBackground(ResourceLocation backgroundId) {
        String path = getBackgroundFilePath(backgroundId);

        try (InputStream inputStream = VampirismMod.class.getResourceAsStream(path)) {
            if (inputStream != null) {
                try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    return VampireBook.BookBackground.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(DecoderException::new);
                } catch (Exception exception) {
                    LOGGER.error("Failed to read background file '{}'", path, exception);
                }
            } else {
                LOGGER.warn("Background file not found at path: {}", path);
            }
        } catch (IOException exception) {
            LOGGER.error("Failed to start an input stream for background file: {}", path, exception);
        }

        return VampireBook.BookBackground.DEFAULT;
    }

    private static String getBackgroundFilePath(ResourceLocation backgroundId) {
        return "/assets/" + backgroundId.getNamespace() + "/vampire_book_backgrounds/" + backgroundId.getPath() +".json";
    }
}
