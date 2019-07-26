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
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ActionArgument implements ArgumentType<IAction> {
    private static final Collection<String> EXAMPLES = Arrays.asList("action", "modid:action");
    public static final DynamicCommandExceptionType ACTION_NOT_FOUND = new DynamicCommandExceptionType((p_208673_0_) -> {
        return new TranslationTextComponent("command.vampirism.argument.action.notfound", p_208673_0_);
    });

    public static ActionArgument actions() {
        return new ActionArgument();
    }

    public static IAction getAction(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, IAction.class);
    }

    @Override
    public IAction parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        IAction action = ModRegistries.ACTIONS.getValue(id);
        if (action == null)
            throw ACTION_NOT_FOUND.create(id);
        return action;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestIterable(ModRegistries.ACTIONS.getKeys(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
