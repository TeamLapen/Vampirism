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
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;


public class FactionArgument implements ArgumentType<IPlayableFaction> {
    private static final Collection<String> EXAMPLES = Arrays.asList("vampirism:vampire", "vampirism:hunter");

    private static final DynamicCommandExceptionType FACTION_NOT_FOUND = new DynamicCommandExceptionType((id) -> new TranslationTextComponent("command.vampirism.argument.faction.notfound", id));
    private static final DynamicCommandExceptionType FACTION_NOT_PLAYABLE = new DynamicCommandExceptionType((id) -> new TranslationTextComponent("command.vampirism.argument.faction.notplayable", id));

    public static IPlayableFaction<IFactionPlayer> getFaction(CommandContext<CommandSource> context, String id) throws CommandSyntaxException {
        return (IPlayableFaction) context.getArgument(id, IFaction.class);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(Arrays.stream(VampirismAPI.factionRegistry().getFactions()).map(i -> i.getID().toString()), builder);
    }

    @Override
    public IPlayableFaction parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        IFaction faction = VampirismAPI.factionRegistry().getFactionByID(id);
        if (faction == null) throw FACTION_NOT_FOUND.create(id);
        if (!(faction instanceof IPlayableFaction)) throw FACTION_NOT_PLAYABLE.create(id);
        return (IPlayableFaction) faction;
    }
}
