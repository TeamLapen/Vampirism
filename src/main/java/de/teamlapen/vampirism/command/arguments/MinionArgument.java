package de.teamlapen.vampirism.command.arguments;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * commands are statically generated on player login. Since the minion data is stored only on the server we need to send the data to the client.<br>
 * This can be either achieved by sending the data once on login and force the player to reconnect or calling {@code /reload} on the client.
 * The way would be to sync the data if it changes.
 */
public class MinionArgument extends ResourceLocationArgument {

    private final Supplier<Collection<ResourceLocation>> playerMinionIds;

    public MinionArgument() {
        this.playerMinionIds = this::getPlayerMinionIds;
    }

    public MinionArgument(Collection<ResourceLocation> playerMinionIds) {
        this.playerMinionIds = () -> playerMinionIds;
    }

    public static MinionArgument minions() {
        return new MinionArgument();
    }

    private Collection<ResourceLocation> getPlayerMinionIds() {
        MinionWorldData data = MinionWorldData.getData(ServerLifecycleHooks.getCurrentServer());
        PlayerProfileCache profileCache = ServerLifecycleHooks.getCurrentServer().getProfileCache();
        return data.getControllers().entrySet().stream()
                .filter(entry -> profileCache.get(entry.getKey()) != null)
                .flatMap(id -> id.getValue().getAllMinionData().stream()
                        .map(minionData -> String.format("%s/%s", minionData.getKey(), minionData.getValue().getName()))
                        .map(minionIdName -> new ResourceLocation(profileCache.get(id.getKey()).getName().toLowerCase(Locale.ROOT), minionIdName.toLowerCase(Locale.ROOT)))).collect(Collectors.toList());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestResource(playerMinionIds.get(), builder);
    }

    public static class MinionArgumentSerializer implements IArgumentSerializer<MinionArgument> {

        @Override
        public void serializeToNetwork(@Nonnull MinionArgument argument, @Nonnull PacketBuffer buffer) {
            Collection<ResourceLocation> ids = argument.playerMinionIds.get();
            buffer.writeVarInt(ids.size());
            ids.forEach(buffer::writeResourceLocation);
        }

        @Nonnull
        @Override
        public MinionArgument deserializeFromNetwork(@Nonnull PacketBuffer buffer) {
            int size = buffer.readVarInt();
            Collection<ResourceLocation> ids = new java.util.ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ids.add(buffer.readResourceLocation());
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
