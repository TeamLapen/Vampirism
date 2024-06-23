package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;


public record ServerboundToggleMinionTaskLock(int minionID) implements CustomPacketPayload {

    public static final Type<ServerboundToggleMinionTaskLock> TYPE = new Type<>(VResourceLocation.mod("toggle_minion_task_lock"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundToggleMinionTaskLock> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundToggleMinionTaskLock::minionID,
            ServerboundToggleMinionTaskLock::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
