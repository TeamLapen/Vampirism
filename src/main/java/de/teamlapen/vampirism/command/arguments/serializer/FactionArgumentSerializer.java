package de.teamlapen.vampirism.command.arguments.serializer;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.command.arguments.FactionArgument;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;


public class FactionArgumentSerializer implements ArgumentTypeInfo<FactionArgument, FactionArgumentSerializer.Template> {


    @Override
    public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {
        buffer.writeBoolean(template.onlyPlayer);
    }

    @Override
    public @NotNull Template deserializeFromNetwork(FriendlyByteBuf buffer) {
        return new Template(buffer.readBoolean());
    }

    @Override
    public void serializeToJson(Template template, JsonObject json) {
        json.addProperty("onlyPlayer", template.onlyPlayer);
    }

    @Override
    public @NotNull Template unpack(FactionArgument p_235372_) {
        return new Template(p_235372_.onlyPlayableFactions);
    }

    public final class Template implements ArgumentTypeInfo.Template<FactionArgument> {

        final boolean onlyPlayer;

        public Template(boolean onlyPlayer) {
            this.onlyPlayer = onlyPlayer;
        }

        @Override
        public @NotNull FactionArgument instantiate(@NotNull CommandBuildContext p_235378_) {
            return new FactionArgument(onlyPlayer);
        }

        @Override
        public @NotNull ArgumentTypeInfo<FactionArgument, ?> type() {
            return FactionArgumentSerializer.this;
        }
    }
}
