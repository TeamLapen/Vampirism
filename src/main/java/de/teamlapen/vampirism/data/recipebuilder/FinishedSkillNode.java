package de.teamlapen.vampirism.data.recipebuilder;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface FinishedSkillNode {
    ResourceLocation getID();

    default @NotNull JsonObject serializeSkillNode() {
        JsonObject jsonObject = new JsonObject();
        this.serialize(jsonObject);
        return jsonObject;
    }

    void serialize(JsonObject json);
}
