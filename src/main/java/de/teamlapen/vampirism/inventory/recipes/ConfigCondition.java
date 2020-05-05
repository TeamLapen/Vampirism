package de.teamlapen.vampirism.inventory.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.function.Supplier;

/**
 * Allows en/disabling recipes based on config options
 */
public class ConfigCondition implements ICondition {

    private static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "config");
    private final Supplier<Boolean> tester;
    private final String option;

    public ConfigCondition(String option) {
        this.tester = getTester(option);
        this.option = option;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test() {
        return tester.get();
    }

    private Supplier<Boolean> getTester(String option) {
        switch (option){
            case "auto_convert":
                return VampirismConfig.SERVER.autoConvertGlassBottles::get;
            case "umbrella":
                return VampirismConfig.SERVER.umbrella::get;
            default:
                throw new JsonSyntaxException("Unknown config option: " + option);
        }
    }

    public static class Serializer implements IConditionSerializer<ConfigCondition> {

        @Override
        public ResourceLocation getID() {
            return ID;
        }

        @Override
        public ConfigCondition read(JsonObject json) {
            String option = json.get("option").getAsString();
            return new ConfigCondition(option);
        }

        @Override
        public void write(JsonObject json, ConfigCondition value) {
            json.addProperty("option", value.option);
        }
    }
}
