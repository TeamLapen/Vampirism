package de.teamlapen.vampirism.command.arguments;

import com.google.gson.JsonObject;
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
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;


@SuppressWarnings("ClassCanBeRecord")
public class FactionArgument implements ArgumentType<IFaction<?>> {
    private static final Collection<String> EXAMPLES = Arrays.asList("vampirism:vampire", "vampirism:hunter");

    private static final DynamicCommandExceptionType FACTION_NOT_FOUND = new DynamicCommandExceptionType((id) -> Component.translatable("command.vampirism.argument.faction.notfound", id));
    private static final DynamicCommandExceptionType FACTION_NOT_PLAYABLE = new DynamicCommandExceptionType((id) -> Component.translatable("command.vampirism.argument.faction.notplayable", id));

    public static IFaction<?> getFaction(@NotNull CommandContext<CommandSourceStack> context, String id) {
        return (IFaction<?>) context.getArgument(id, IFaction.class);
    }

    public static IPlayableFaction<?> getPlayableFaction(@NotNull CommandContext<CommandSourceStack> context, String id) {
        return (IPlayableFaction<?>) context.getArgument(id, IFaction.class);
    }

    public static @NotNull FactionArgument playableFactions() {
        return new FactionArgument(true);
    }

    public static @NotNull FactionArgument factions() {
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
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(Arrays.stream(this.onlyPlayableFactions ? VampirismAPI.factionRegistry().getPlayableFactions(): VampirismAPI.factionRegistry().getFactions()).map(i -> i.getID().toString()), builder);
    }

    @Override
    public @NotNull IFaction<?> parse(@NotNull StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        IFaction<?> faction = VampirismAPI.factionRegistry().getFactionByID(id);
        if (faction == null) throw FACTION_NOT_FOUND.create(id);
        if (this.onlyPlayableFactions & !(faction instanceof IPlayableFaction)) throw FACTION_NOT_PLAYABLE.create(id);
        return faction;
    }

    public static class Info implements ArgumentTypeInfo<FactionArgument, Info.Template> {
        @Override
        public void serializeToNetwork(@NotNull Template template, @NotNull FriendlyByteBuf buffer) {
            buffer.writeBoolean(template.onlyPlayableFaction);
        }

        @NotNull
        @Override
        public  Template deserializeFromNetwork(@NotNull FriendlyByteBuf buffer) {
            return new Template(buffer.readBoolean());
        }

        @Override
        public void serializeToJson(@NotNull Template template, @NotNull JsonObject json) {
            json.addProperty("onlyPlayableFactions", template.onlyPlayableFaction);
        }

        @NotNull
        @Override
        public Template unpack(@NotNull FactionArgument argument) {
            return new Template(argument.onlyPlayableFactions);
        }

        public class Template implements ArgumentTypeInfo.Template<FactionArgument> {

            final boolean onlyPlayableFaction;

            Template(boolean onlyPlayableFaction) {
                this.onlyPlayableFaction = onlyPlayableFaction;
            }

            @NotNull
            @Override
            public  FactionArgument instantiate(@NotNull CommandBuildContext context) {
                return new FactionArgument(this.onlyPlayableFaction);
            }

            @NotNull
            @Override
            public ArgumentTypeInfo<FactionArgument, ?> type() {
                return Info.this;
            }
        }
    }
}