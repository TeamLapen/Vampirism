package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.function.Supplier;


public class SelectMinionTaskPacket implements IMessage {
    private static final Logger LOGGER = LogManager.getLogger();
    public final static ResourceLocation RECALL = new ResourceLocation(REFERENCE.MODID, "recall");
    public final static ResourceLocation RESPAWN = new ResourceLocation(REFERENCE.MODID, "respawn");


    public static void handle(final SelectMinionTaskPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> FactionPlayerHandler.getOpt(ctx.getSender()).ifPresent(fp -> {
            PlayerMinionController controller = MinionWorldData.getData(ctx.getSender().server).getOrCreateController(fp);
            if (RECALL.equals(msg.taskID)) {
                if (msg.minionID < 0) {
                    Collection<Integer> ids = controller.recallMinions();
                    for (Integer id : ids) {
                        controller.createMinionEntityAtPlayer(id, ctx.getSender());
                    }
                } else {
                    if (controller.recallMinion(msg.minionID)) {
                        controller.createMinionEntityAtPlayer(msg.minionID, ctx.getSender());
                    }
                }
            } else if (RESPAWN.equals(msg.taskID)) {
                Collection<Integer> ids = controller.getUnclaimedMinions();
                for (Integer id : ids) {
                    controller.createMinionEntityAtPlayer(id, ctx.getSender());
                }
            } else {
                IMinionTask<?, MinionData> task = (IMinionTask<?, MinionData>) ModRegistries.MINION_TASKS.getValue(msg.taskID);
                if (task == null) {
                    LOGGER.error("Cannot find action to activate {}", msg.taskID);
                } else if (msg.minionID < -1) {
                    LOGGER.error("Illegal minion id {}", msg.minionID);
                } else {
                    controller.activateTask(msg.minionID, task);
                }
            }


        }));
        ctx.setPacketHandled(true);
    }

    static void encode(SelectMinionTaskPacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.minionID);
        buf.writeResourceLocation(msg.taskID);
    }

    static SelectMinionTaskPacket decode(PacketBuffer buf) {
        return new SelectMinionTaskPacket(buf.readVarInt(), buf.readResourceLocation());
    }

    public final int minionID;
    public final ResourceLocation taskID;

    public SelectMinionTaskPacket(int minionID, ResourceLocation taskID) {
        assert minionID >= -1;
        this.minionID = minionID;
        this.taskID = taskID;
    }
}
