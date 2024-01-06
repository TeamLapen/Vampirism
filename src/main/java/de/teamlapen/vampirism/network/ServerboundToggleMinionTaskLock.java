package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;


public record ServerboundToggleMinionTaskLock(int minionID) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "toggle_minion_task_lock");
    public static final Codec<ServerboundToggleMinionTaskLock> CODEC = Codec.INT.xmap(ServerboundToggleMinionTaskLock::new, msg -> msg.minionID);

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
