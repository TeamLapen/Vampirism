package de.teamlapen.vampirism.misc;

import com.google.gson.*;
import de.teamlapen.vampirism.api.settings.Supporter;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;
import java.util.Map;

public class SupporterDeserializer implements JsonDeserializer<Supporter> {
    @Override
    public Supporter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        String faction = object.get("faction").getAsString();

        String name = object.get("name").getAsString();
        String texture = object.get("texture").getAsString();
        String bookId = null;
        if (object.has("bookId")) {
            JsonElement bookId1 = object.get("bookId");
            if (!bookId1.isJsonNull()) {
                bookId = bookId1.getAsString();
            }
        }
        JsonElement appearanceJson = object.get("appearance");
        Map<String, String> appearance;
        if (appearanceJson != null && appearanceJson.isJsonObject()) {
            appearance = context.deserialize(appearanceJson, Map.class);
        } else {
            appearance = Map.of();
        }
        return new Supporter(ResourceLocation.parse(faction), name, texture, bookId, appearance);
    }
}
