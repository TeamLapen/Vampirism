package de.teamlapen.vampirism.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class VampirismGsonHelper extends GsonHelper {

    public static void addProperty(JsonObject json, String property, Number value, Number fallback) {
        if (Objects.equals(value, fallback)) return;
        json.addProperty(property, value);
    }

    public static void addProperty(JsonObject json, String property, Boolean value, Boolean fallback) {
        if (Objects.equals(value, fallback)) return;
        json.addProperty(property, value);
    }

    public static ResourceLocation getAsResourceLocation(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return ResourceLocation.tryParse(convertToString(json.get(memberName), memberName));
        }

        throw new JsonSyntaxException("Missing " + memberName + ", expected to find a resource location");
    }

    @Nullable
    public static ResourceLocation getAsResourceLocation(JsonObject json, String memberName, @Nullable ResourceLocation fallback) {
        return json.has(memberName) ? getAsResourceLocation(json, memberName) : fallback;
    }
}
