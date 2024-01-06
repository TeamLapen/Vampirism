package de.teamlapen.vampirism.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ActionArgument implements ArgumentType<IAction<?>> {
    public static final DynamicCommandExceptionType ACTION_NOT_FOUND = new DynamicCommandExceptionType((particle) -> Component.translatable("command.vampirism.argument.action.notfound", particle));
    private static final Collection<String> EXAMPLES = Arrays.asList("action", "modid:action");

    public static @NotNull ActionArgument actions() {
        return new ActionArgument();
    }

    public static IAction<?> getAction(@NotNull CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, IAction.class);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(ModRegistries.ACTIONS.keySet(), builder);
    }

    @Override
    public @NotNull IAction<?> parse(@NotNull StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        IAction<?> action = ModRegistries.ACTIONS.get(id);
        if (action == null) {
            throw ACTION_NOT_FOUND.create(id);
        }
        return action;
    }
}
