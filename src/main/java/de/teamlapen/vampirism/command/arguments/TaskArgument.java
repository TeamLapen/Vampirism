package de.teamlapen.vampirism.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class TaskArgument implements ArgumentType<Task> {
    public static final DynamicCommandExceptionType TASK_NOT_FOUND = new DynamicCommandExceptionType((p_208673_0_) -> {
        return new TranslationTextComponent("command.vampirism.argument.task.notfound", p_208673_0_);
    });
    private static final Collection<String> EXAMPLES = Collections.singletonList("modid:task");

    public static TaskArgument tasks() {
        return new TaskArgument();
    }

    public static Task getTask(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, Task.class);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestIterable(ModRegistries.TASKS.getKeys(), builder);
    }

    @Override
    public Task parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        Task task = ModRegistries.TASKS.getValue(id);
        if (task == null)
            throw TASK_NOT_FOUND.create(id);
        return task;
    }
}
