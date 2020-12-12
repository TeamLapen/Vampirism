package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;


public class SelectMinionTaskPacket implements IMessage {
    public final static ResourceLocation RECALL = new ResourceLocation(REFERENCE.MODID, "recall");
    public final static ResourceLocation RESPAWN = new ResourceLocation(REFERENCE.MODID, "respawn");
    private static final Logger LOGGER = LogManager.getLogger();

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
                    printRecoveringMinions(ctx.getSender(), controller.getRecoveringMinionNames());

                } else {
                    if (controller.recallMinion(msg.minionID)) {
                        controller.createMinionEntityAtPlayer(msg.minionID, ctx.getSender());
                    } else {
                        ctx.getSender().sendStatusMessage(new TranslationTextComponent("text.vampirism.minion_is_still_recovering", controller.contactMinionData(msg.minionID, MinionData::getFormattedName).orElse(new StringTextComponent("1"))), true);
                    }
                }
            } else if (RESPAWN.equals(msg.taskID)) {
                Collection<Integer> ids = controller.getUnclaimedMinions();
                for (Integer id : ids) {
                    controller.createMinionEntityAtPlayer(id, ctx.getSender());
                }
                printRecoveringMinions(ctx.getSender(), controller.getRecoveringMinionNames());

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

    private static void printRecoveringMinions(ServerPlayerEntity player, List<IFormattableTextComponent> recoveringMinions) {
        if (recoveringMinions.size() == 1) {
            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.minion_is_still_recovering", recoveringMinions.get(0)), true);
        } else if (recoveringMinions.size() > 1) {
            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.n_minions_are_still_recovering", recoveringMinions.size()), true);
        }
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
