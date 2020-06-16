package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;


public class ActivateMinionTaskPacket implements IMessage {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void handle(final ActivateMinionTaskPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> FactionPlayerHandler.getOpt(ctx.getSender()).ifPresent(fp -> {
            IMinionTask<?> task = ModRegistries.MINION_TASKS.getValue(msg.taskID);
            if (task == null) {
                LOGGER.error("Cannot find action to activate {}", msg.taskID);
            } else if (msg.minionID < 0) {
                LOGGER.error("Illegal minion id {}", msg.minionID);
            } else {
                MinionWorldData.getData(ctx.getSender().server).getOrCreateController(fp).activateTask(msg.minionID, task);
            }

        }));
        ctx.setPacketHandled(true);
    }

    static void encode(ActivateMinionTaskPacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.minionID);
        buf.writeResourceLocation(msg.taskID);
    }

    static ActivateMinionTaskPacket decode(PacketBuffer buf) {
        return new ActivateMinionTaskPacket(buf.readVarInt(), buf.readResourceLocation());
    }

    public final int minionID;
    public final ResourceLocation taskID;

    public ActivateMinionTaskPacket(int minionID, ResourceLocation taskID) {
        this.minionID = minionID;
        this.taskID = taskID;
    }
}
