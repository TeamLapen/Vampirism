package de.teamlapen.vampirism.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SkillArgument implements ArgumentType<ISkill<?>> {
    public static final DynamicCommandExceptionType SKILL_NOT_FOUND = new DynamicCommandExceptionType((particle) -> Component.translatable("command.vampirism.argument.skill.notfound", particle));
    private static final Collection<String> EXAMPLES = Arrays.asList("skill", "modid:skill");

    public static @NotNull SkillArgument skills() {
        return new SkillArgument();
    }

    public static ISkill<?> getSkill(@NotNull CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, ISkill.class);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(RegUtil.keys(ModRegistries.SKILLS), builder);
    }

    @Override
    public @NotNull ISkill<?> parse(@NotNull StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        ISkill<?> skill = RegUtil.getSkill(id);
        if (skill == null)
            throw SKILL_NOT_FOUND.create(id);
        return skill;
    }
}
