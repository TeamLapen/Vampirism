package de.teamlapen.vampirism.inventory.recipes;

import com.google.gson.JsonObject;

import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.crafting.IConditionSerializer;

import java.util.function.BooleanSupplier;

/**
 * Used to use configuration as condition for recipe registration.
 * Manual config assignment for now.
 */
public class ConfigEntryConditionSerializer implements IConditionSerializer {//TODO 1.14 test
    public ConfigEntryConditionSerializer() {
    }

    @Override
    public BooleanSupplier parse(JsonObject json) {
        String key = JSONUtils.getString(json, "key");
        switch (key) {
            case "auto_convert_blood_bottles":
                return VampirismConfig.SERVER.autoConvertGlassBottles::get;
            default:
                return () -> false;
        }

    }
}
