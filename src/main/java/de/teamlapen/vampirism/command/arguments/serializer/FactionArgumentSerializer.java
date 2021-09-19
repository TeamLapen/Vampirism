package de.teamlapen.vampirism.command.arguments.serializer;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.command.arguments.FactionArgument;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;

public class FactionArgumentSerializer implements ArgumentSerializer<FactionArgument> {
    
    @Override
    public void serializeToNetwork(@Nonnull FactionArgument pArgument, @Nonnull FriendlyByteBuf pBuffer) {
        pBuffer.writeBoolean(pArgument.onlyPlayableFactions);
    }

    @Nonnull
    @Override
    public FactionArgument deserializeFromNetwork(@Nonnull FriendlyByteBuf pBuffer) {
        return new FactionArgument(pBuffer.readBoolean());
    }

    @Override
    public void serializeToJson(@Nonnull FactionArgument pArgument, @Nonnull JsonObject pJson) {
        pJson.addProperty("onlyPlayer", pArgument.onlyPlayableFactions);
    }
}
