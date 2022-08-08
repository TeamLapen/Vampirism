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
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
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

    public static MinionId getId(CommandContext<CommandSource> source, String argumentId) {
        return source.getArgument(argumentId, MinionId.class);
    }

    private Collection<MinionId> getPlayerMinionIds() {
        MinionWorldData data = MinionWorldData.getData(ServerLifecycleHooks.getCurrentServer());
        PlayerProfileCache profileCache = ServerLifecycleHooks.getCurrentServer().getProfileCache();
        //noinspection ConstantConditions
        return data.getControllers().entrySet().stream()
                .filter(entry -> profileCache.get(entry.getKey()) != null)
                .flatMap(id -> id.getValue().getMinionIdForName(profileCache.get(id.getKey()).getName()).stream()).collect(Collectors.toList());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String s = builder.getRemaining();
        filterResources(playerMinionIds.get(), s, MinionId::toString, builder::suggest);
        return builder.buildFuture();
    }

    <T> void filterResources(Iterable<T> p_210512_0_, String p_210512_1_, Function<T, String> p_210512_2_, Consumer<String> consumer) {
        for(T t : p_210512_0_) {
            String id = p_210512_2_.apply(t);
            if (ISuggestionProvider.matchesSubStr(p_210512_1_, id)) {
                consumer.accept(id);
            }
        }
    }

    @Override
    public MinionId parse(StringReader reader) throws CommandSyntaxException {
        StringBuilder builder = new StringBuilder();
        while (reader.canRead()) {
            char c = reader.peek();
            if (c == ' ') {
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
    }

    public static class MinionArgumentSerializer implements IArgumentSerializer<MinionArgument> {

        @Override
        public void serializeToNetwork(@Nonnull MinionArgument argument, @Nonnull PacketBuffer buffer) {
            Collection<MinionId> ids = argument.playerMinionIds.get();
            buffer.writeVarInt(ids.size());
            ids.forEach(id -> buffer.writeUtf(id.toString()));
        }

        @Nonnull
        @Override
        public MinionArgument deserializeFromNetwork(@Nonnull PacketBuffer buffer) {
            int size = buffer.readVarInt();
            Collection<MinionId> ids = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ids.add(new MinionId(buffer.readUtf()));
            }
            return new MinionArgument(ids);
        }

        @Override
        public void serializeToJson(@Nonnull MinionArgument argument, @Nonnull JsonObject json) {
            JsonArray array = new JsonArray();
            argument.playerMinionIds.get().forEach(id -> array.add(id.toString()));
            json.add("playerMinionIds", array);
        }
    }
}
