package de.teamlapen.vampirism.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
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

public class RefinementSetArgument implements ArgumentType<IRefinementSet> {
    public static final DynamicCommandExceptionType REFINEMENT_NOT_FOUND = new DynamicCommandExceptionType((particle) -> Component.translatable("command.vampirism.argument.refinement_set.notfound", particle));
    private static final Collection<String> EXAMPLES = Arrays.asList("refinement_set", "modid:refinement_set");

    public static @NotNull RefinementSetArgument set() {
        return new RefinementSetArgument();
    }

    public static IRefinementSet getSet(@NotNull CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, IRefinementSet.class);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(RegUtil.keys(ModRegistries.REFINEMENT_SETS), builder);
    }

    @Override
    public @NotNull IRefinementSet parse(@NotNull StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        IRefinementSet set = RegUtil.getRefinementSet(id);
        if (set == null)
            throw REFINEMENT_NOT_FOUND.create(id);
        return set;
    }
}
