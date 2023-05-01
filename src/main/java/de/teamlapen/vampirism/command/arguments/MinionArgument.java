package de.teamlapen.vampirism.command.arguments;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.players.GameProfileCache;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * commands are statically generated on player login. Since the minion data is stored only on the server we need to send the data to the client.<br>
 * This can be either achieved by sending the data once on login and force the player to reconnect or calling {@code /reload} on the client.
 * The way would be to sync the data if it changes.
 */
public class MinionArgument implements ArgumentType<MinionArgument.MinionId> {

    private final Supplier<Collection<MinionId>> playerMinionIds;

    public MinionArgument() {
        this.playerMinionIds = this::getPlayerMinionIds;
    }

    public MinionArgument(Collection<MinionId> playerMinionIds) {
        this.playerMinionIds = () -> playerMinionIds;
    }

    public static MinionArgument minions() {
        return new MinionArgument();
    }

    public static MinionId getId(CommandContext<CommandSourceStack> source, String argumentId) {
        return source.getArgument(argumentId, MinionId.class);
    }

    private Collection<MinionId> getPlayerMinionIds() {
        MinionWorldData data = MinionWorldData.getData(ServerLifecycleHooks.getCurrentServer());
        GameProfileCache profileCache = ServerLifecycleHooks.getCurrentServer().getProfileCache();
        return data.getControllers().entrySet().stream()
                .flatMap(entry -> profileCache.get(entry.getKey()).stream().flatMap(k -> entry.getValue().getMinionIdForName(k.getName()).stream())).collect(Collectors.toList());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String s = builder.getRemaining();
        filterResources(playerMinionIds.get(), s, MinionId::toEscaped, builder::suggest);
        return builder.buildFuture();
    }

    <T> void filterResources(Iterable<T> p_210512_0_, String p_210512_1_, Function<T, String> p_210512_2_, Consumer<String> consumer) {
        for(T t : p_210512_0_) {
            String id = p_210512_2_.apply(t);
            if (SharedSuggestionProvider.matchesSubStr(p_210512_1_, id)) {
                consumer.accept(id);
            }
        }
    }

    @Override
    public MinionId parse(StringReader reader) throws CommandSyntaxException {
        StringBuilder builder = new StringBuilder();
        boolean isQuotes = false;
        while (reader.canRead()) {
            char c = reader.peek();
            if (c == '\"') {
                isQuotes = !isQuotes;
                reader.skip();
                continue;
            } else if (c == ' ' && !isQuotes) {
                break;
            }
            builder.append(c);
            reader.skip();
        }
        return new MinionId(builder.toString());
    }

    public static class MinionId {
        public final String player;
        public final int id;
        public String name;

        public MinionId(String player, int id, String name) {
            this.player = player;
            this.id = id;
            this.name = name;
        }

        public MinionId(String id) throws NumberFormatException{
            int first = id.indexOf(':');
            this.player = id.substring(0, first);
            int second = id.indexOf('/');
            if (second == -1) {
                this.id = Integer.parseInt(id.substring(first + 1));
                this.name = "";
            } else {
                this.id = Integer.parseInt(id.substring(first + 1, second));
                if (id.length() > second + 1) {
                    this.name = id.substring(second + 1);
                } else {
                    this.name = "";
                }
            }
        }

        public void updateName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return player + ":" + id + "/" + name;
        }

        public String toEscaped() {
            var res = StringEscapeUtils.escapeJava(toString());
            if (res.contains(" ")) {
                res = "\"" + res + "\"";
            }
            return res;
        }
    }

    public static class Info implements ArgumentTypeInfo<MinionArgument, Info.Template> {

        @Override
        public void serializeToNetwork(Template tempalte, FriendlyByteBuf buffer) {
            Collection<MinionId> ids = tempalte.ids;
            buffer.writeVarInt(ids.size());
            ids.forEach(id -> buffer.writeUtf(id.toString()));
        }

        @Override
        public @NotNull Template deserializeFromNetwork(FriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            Collection<MinionId> ids = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ids.add(new MinionId(buffer.readUtf()));
            }
            return new Template(ids);
        }

        @Override
        public void serializeToJson(Template template, JsonObject json) {
            JsonArray array = new JsonArray();
            template.ids.forEach(id -> array.add(id.toString()));
            json.add("playerMinionIds", array);
        }

        @Override
        public @NotNull Template unpack(@NotNull MinionArgument argument) {
            return new Template(argument.playerMinionIds.get());
        }

        public class Template implements ArgumentTypeInfo.Template<MinionArgument> {

            final Collection<MinionId> ids;

            public Template(Collection<MinionId> ids) {
                this.ids = ids;
            }

            @Override
            public @NotNull MinionArgument instantiate(@NotNull CommandBuildContext context) {
                return new MinionArgument(ids);
            }

            @Override
            public @NotNull ArgumentTypeInfo<MinionArgument, ?> type() {
                return Info.this;
            }
        }
    }
}
