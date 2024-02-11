package de.teamlapen.vampirism.recipes;

import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Allows en/disabling recipes based on config options
 */
public record ConfigCondition(@NotNull String option, @NotNull Function<IContext, Boolean> tester) implements ICondition {

    public static final Codec<ConfigCondition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("option").forGetter(c -> c.option)
    ).apply(inst, ConfigCondition::new));

    public ConfigCondition(@NotNull String option) {
        this(option, getTester(option));
    }

    @Override
    public boolean test(IContext context) {
        return tester.apply(context);
    }

    @Override
    public Codec<? extends ICondition> codec() {
        return CODEC;
    }

    private static @NotNull Function<IContext, Boolean> getTester(@NotNull String option) {
        return switch (option) {
            case "auto_convert" -> (context) -> VampirismConfig.COMMON.autoConvertGlassBottles.get();
            case "umbrella" -> (context) -> VampirismConfig.COMMON.umbrella.get();
            default -> throw new JsonSyntaxException("Unknown config option: " + option);
        };
    }

}
