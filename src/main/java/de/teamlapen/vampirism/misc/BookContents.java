package de.teamlapen.vampirism.misc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.general.IBookContents;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import static de.teamlapen.vampirism.util.VampirismGsonHelper.*;

public record BookContents(List<String> contents, ResourceLocation background, List<IImageEntry> images) implements IBookContents {

    public static final BookContents EMPTY = new BookContents(List.of(), null, List.of());

    public static BookContents decode(JsonObject json) {
        List<String> contents = new ArrayList<>();
        List<IImageEntry> images = new ArrayList<>();
        ResourceLocation background = null;

        if (json.has("contents") && json.get("contents").isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray("contents")) {
                contents.add(element.getAsString());
            }
        }

        if (json.has("background") && json.get("background").isJsonPrimitive()) {
            background = ResourceLocation.parse(json.getAsJsonPrimitive("background").getAsString());
        }

        if (json.has("images") && json.get("images").isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray("images")) {
                images.add(ImageEntry.decode(element.getAsJsonObject()));
            }
        }

        return new BookContents(contents, background, images);
    }

    public record ImageEntry(int id, ResourceLocation texture, int page, int x, int y, int width, int height) implements IBookContents.IImageEntry {

        public static ImageEntry decode(JsonObject json) {
            return new ImageEntry(
                    getAsInt(json, "id", 1),
                    getAsResourceLocation(json, "texture"),
                    getAsInt(json, "page", 0),
                    getAsInt(json, "x", 0),
                    getAsInt(json, "y", 0),
                    getAsInt(json, "width", 100),
                    getAsInt(json, "height", 100)
            );
        }
    }
}
