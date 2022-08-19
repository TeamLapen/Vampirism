package de.teamlapen.vampirism.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Allows en/disabling recipes based on config options
 */
public class ConfigCondition implements ICondition {

    private static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "config");
    private final @NotNull Function<IContext, Boolean> tester;
    private final @NotNull String option;

    public ConfigCondition(@NotNull String option) {
        this.tester = getTester(option);
        this.option = option;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(IContext context) {
        return tester.apply(context);
    }

    private @NotNull Function<IContext, Boolean> getTester(@NotNull String option) {
        return switch (option) {
            case "auto_convert" -> (context) -> VampirismConfig.COMMON.autoConvertGlassBottles.get();
            case "umbrella" -> (context) -> VampirismConfig.COMMON.umbrella.get();
            default -> throw new JsonSyntaxException("Unknown config option: " + option);
        };
    }

    public static class Serializer implements IConditionSerializer<ConfigCondition> {

        @Override
        public @NotNull ResourceLocation getID() {
            return ID;
        }

        @Override
        public @NotNull ConfigCondition read(@NotNull JsonObject json) {
            String option = json.get("option").getAsString();
            return new ConfigCondition(option);
        }

        @Override
        public void write(@NotNull JsonObject json, @NotNull ConfigCondition value) {
            json.addProperty("option", value.option);
        }
    }
}
