package de.teamlapen.factions.common.server.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.common.core.ModRegistries;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;


public class FactionArgument implements ArgumentType<Holder.Reference<IFaction<?>>> {
    private static final Collection<String> EXAMPLES = Arrays.asList("vampirism:vampire", "vampirism:hunter");

    private static final DynamicCommandExceptionType FACTION_NOT_FOUND = new DynamicCommandExceptionType((id) -> Component.translatable("command.vampirism.argument.faction.notfound", id));
    private static final DynamicCommandExceptionType FACTION_NOT_PLAYABLE = new DynamicCommandExceptionType((id) -> Component.translatable("command.vampirism.argument.faction.notplayable", id));

    public FactionArgument(boolean onlyPlayableFactions, HolderLookup.RegistryLookup<IFaction<?>> registryLookup) {
        this.onlyPlayableFactions = onlyPlayableFactions;
        this.registryLookup = registryLookup;
    }

    public static @NotNull FactionArgument playableFactions(CommandBuildContext context) {
        return new FactionArgument(true, context.lookupOrThrow(FactionRegistries.Keys.FACTION));
    }

    public static @NotNull ResourceArgument<IFaction<?>> factions(CommandBuildContext context) {
        return ResourceArgument.resource(context, FactionRegistries.Keys.FACTION);
    }

    public static Holder.Reference<IFaction<?>> getFaction(CommandContext<CommandSourceStack> pContext, String pArgument) throws CommandSyntaxException {
        return ResourceArgument.getResource(pContext, pArgument, FactionRegistries.Keys.FACTION);
    }

    @SuppressWarnings("unchecked")
    public static Holder<IPlayableFaction<?>> getPlayableFaction(CommandContext<CommandSourceStack> pContext, String pArgument) throws CommandSyntaxException {
        var holder = ResourceArgument.getResource(pContext, pArgument, FactionRegistries.Keys.FACTION);
        if (holder.value() instanceof IPlayableFaction<?>) {
            return (Holder<IPlayableFaction<?>>) (Object) holder;
        } else {
            throw FACTION_NOT_PLAYABLE.create(holder.key().location());
        }
    }

    private final boolean onlyPlayableFactions;
    private final HolderLookup.RegistryLookup<IFaction<?>> registryLookup;

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        Stream<Holder.Reference<IFaction<?>>> factions = ModRegistries.FACTIONS.listElements();
        if (this.onlyPlayableFactions) {
            factions = factions.filter(f -> f.value() instanceof IPlayableFaction);
        }
        return SharedSuggestionProvider.suggest(factions.map(i -> i.key().location().toString()), builder);
    }

    @Override
    public @NotNull Holder.Reference<IFaction<?>> parse(@NotNull StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        var key = ResourceKey.create(FactionRegistries.Keys.FACTION, id);
        return this.registryLookup.get(key).orElseThrow(() -> FACTION_NOT_FOUND.create(id));
    }

    public static class Info implements ArgumentTypeInfo<FactionArgument, Info.Template> {
        @Override
        public void serializeToNetwork(@NotNull Template template, @NotNull FriendlyByteBuf buffer) {
            buffer.writeBoolean(template.onlyPlayableFaction);
        }

        @NotNull
        @Override
        public Template deserializeFromNetwork(@NotNull FriendlyByteBuf buffer) {
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
            public FactionArgument instantiate(@NotNull CommandBuildContext context) {
                return new FactionArgument(this.onlyPlayableFaction, context.lookupOrThrow(FactionRegistries.Keys.FACTION));
            }

            @NotNull
            @Override
            public ArgumentTypeInfo<FactionArgument, ?> type() {
                return Info.this;
            }
        }
    }
}