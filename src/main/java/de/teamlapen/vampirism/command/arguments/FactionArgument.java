package de.teamlapen.vampirism.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;


@SuppressWarnings("ClassCanBeRecord")
public class FactionArgument implements ArgumentType<IFaction<?>> {
    private static final Collection<String> EXAMPLES = Arrays.asList("vampirism:vampire", "vampirism:hunter");

    private static final DynamicCommandExceptionType FACTION_NOT_FOUND = new DynamicCommandExceptionType((id) -> Component.translatable("command.vampirism.argument.faction.notfound", id));
    private static final DynamicCommandExceptionType FACTION_NOT_PLAYABLE = new DynamicCommandExceptionType((id) -> Component.translatable("command.vampirism.argument.faction.notplayable", id));

    public static IFaction<?> getFaction(CommandContext<CommandSourceStack> context, String id) {
        return (IFaction<?>) context.getArgument(id, IFaction.class);
    }

    public static IPlayableFaction<?> getPlayableFaction(CommandContext<CommandSourceStack> context, String id) {
        return (IPlayableFaction<?>) context.getArgument(id, IFaction.class);
    }

    public static FactionArgument playableFactions() {
        return new FactionArgument(true);
    }

    public static FactionArgument factions() {
        return new FactionArgument(false);
    }

    public final boolean onlyPlayableFactions;

    public FactionArgument(boolean onlyPlayableFactions) {
        this.onlyPlayableFactions = onlyPlayableFactions;
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(Arrays.stream(this.onlyPlayableFactions ? VampirismAPI.factionRegistry().getPlayableFactions(): VampirismAPI.factionRegistry().getFactions()).map(i -> i.getID().toString()), builder);
    }

    @Override
    public IFaction<?> parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        IFaction<?> faction = VampirismAPI.factionRegistry().getFactionByID(id);
        if (faction == null) throw FACTION_NOT_FOUND.create(id);
        if (this.onlyPlayableFactions & !(faction instanceof IPlayableFaction)) throw FACTION_NOT_PLAYABLE.create(id);
        return faction;
    }
}