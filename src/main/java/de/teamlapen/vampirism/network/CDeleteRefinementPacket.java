package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.Validate;

import java.util.Optional;
import java.util.function.Supplier;


public class CDeleteRefinementPacket implements IMessage {

    static void encode(CDeleteRefinementPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.slot);
    }

    static CDeleteRefinementPacket decode(FriendlyByteBuf buf) {
        return new CDeleteRefinementPacket(buf.readEnum(IRefinementItem.AccessorySlotType.class));
    }

    static void handle(CDeleteRefinementPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayer player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            Optional<? extends IFactionPlayer<?>> factionPlayerOpt = FactionPlayerHandler.getOpt(player).map(FactionPlayerHandler::getCurrentFactionPlayer).orElseGet(Optional::empty);
            factionPlayerOpt.ifPresent(fp -> fp.getSkillHandler().removeRefinementItem(msg.slot));
        });
        ctx.setPacketHandled(true);
    }

    private final IRefinementItem.AccessorySlotType slot;

    public CDeleteRefinementPacket(IRefinementItem.AccessorySlotType slot) {
        this.slot = slot;
    }
}
