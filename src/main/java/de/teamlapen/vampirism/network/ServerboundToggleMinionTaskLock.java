package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;


public record ServerboundToggleMinionTaskLock(int minionID) implements IMessage.IServerBoundMessage {
    static void encode(@NotNull ServerboundToggleMinionTaskLock msg, @NotNull FriendlyByteBuf buf) {
        buf.writeVarInt(msg.minionID);
    }

    static @NotNull ServerboundToggleMinionTaskLock decode(@NotNull FriendlyByteBuf buf) {
        return new ServerboundToggleMinionTaskLock(buf.readVarInt());
    }

    static void handle(@NotNull ServerboundToggleMinionTaskLock msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayer player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            FactionPlayerHandler.getOpt(ctx.getSender()).ifPresent(fp -> {
                PlayerMinionController controller = MinionWorldData.getData(ctx.getSender().server).getOrCreateController(fp);
                controller.contactMinionData(msg.minionID, data -> data.setTaskLocked(!data.isTaskLocked()));
                controller.contactMinion(msg.minionID, MinionEntity::onTaskChanged);

            });
        });
        ctx.setPacketHandled(true);
    }
}
