package de.teamlapen.vampirism.data.recipebuilder;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public interface FinishedSkillNode {
    ResourceLocation getID();

    default JsonObject getSkillNodeJson() {
        JsonObject jsonObject = new JsonObject();
        this.serialize(jsonObject);
        return jsonObject;
    }

    void serialize(JsonObject json);
}
