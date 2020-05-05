package de.teamlapen.vampirism.data.recipebuilder;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public interface FinishedSkillNode {
    void serialize(JsonObject json);

    default JsonObject getSkillNodeJson(){
        JsonObject jsonObject = new JsonObject();
        this.serialize(jsonObject);
        return jsonObject;
    }

    ResourceLocation getID();
}
