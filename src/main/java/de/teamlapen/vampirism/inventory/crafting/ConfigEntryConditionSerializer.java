package de.teamlapen.vampirism.inventory.crafting;

import com.google.gson.JsonObject;

import de.teamlapen.vampirism.config.Configs;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionSerializer;

import java.util.function.BooleanSupplier;

/**
 * Used to use configuration as condition for recipe registration.
 * Manual config assignment for now.
 */
public class ConfigEntryConditionSerializer implements IConditionSerializer {
    public ConfigEntryConditionSerializer() {
    }

    @Override
    public BooleanSupplier parse(JsonObject json) {
        String key = JsonUtils.getString(json, "key");
        switch (key) {
            case "auto_convert_blood_bottles":
                return () -> Configs.autoConvertGlasBottles;
            default:
                return () -> false;
        }

    }
}
