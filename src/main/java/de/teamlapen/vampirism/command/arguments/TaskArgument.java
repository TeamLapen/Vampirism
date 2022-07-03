package de.teamlapen.vampirism.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class TaskArgument implements ArgumentType<Task> {
    public static final DynamicCommandExceptionType TASK_NOT_FOUND = new DynamicCommandExceptionType((particle) -> {
        return Component.translatable("command.vampirism.argument.task.notfound", particle);
    });
    private static final Collection<String> EXAMPLES = Collections.singletonList("modid:task");

    public static TaskArgument tasks() {
        return new TaskArgument();
    }

    public static Task getTask(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, Task.class);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(RegUtil.keys(VampirismRegistries.TASKS), builder);
    }

    @Override
    public Task parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        Task task = RegUtil.getTask(id);
        if (task == null)
            throw TASK_NOT_FOUND.create(id);
        return task;
    }
}
