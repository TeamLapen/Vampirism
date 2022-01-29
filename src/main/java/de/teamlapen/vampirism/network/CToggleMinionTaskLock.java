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

import java.util.function.Supplier;


public class CToggleMinionTaskLock implements IMessage {
    static void encode(CToggleMinionTaskLock msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.minionID);
    }

    static CToggleMinionTaskLock decode(FriendlyByteBuf buf) {
        return new CToggleMinionTaskLock(buf.readVarInt());
    }

    static void handle(CToggleMinionTaskLock msg, Supplier<NetworkEvent.Context> contextSupplier) {
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

    private final int minionID;

    public CToggleMinionTaskLock(int minionID) {
        this.minionID = minionID;
    }
}
