package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public class CToggleActionPacket implements IMessage {
    private static final Logger LOGGER = LogManager.getLogger();

    static void encode(CToggleActionPacket msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.actionId);
    }

    static CToggleActionPacket decode(FriendlyByteBuf buf) {
        return new CToggleActionPacket(buf.readResourceLocation());
    }

    static void handle(CToggleActionPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayer player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            Optional<? extends IFactionPlayer<?>> factionPlayerOpt = FactionPlayerHandler.getOpt(player).map(FactionPlayerHandler::getCurrentFactionPlayer).orElseGet(Optional::empty);
            factionPlayerOpt.ifPresent(factionPlayer -> {
                IActionHandler<?> actionHandler = factionPlayer.getActionHandler();
                IAction action = RegUtil.getAction(msg.actionId);
                if (action != null) {
                    IAction.PERM r = actionHandler.toggleAction(action);
                    switch (r) {
                        case NOT_UNLOCKED:
                            player.displayClientMessage(Component.translatable("text.vampirism.action.not_unlocked"), true);
                            break;
                        case DISABLED:
                            player.displayClientMessage(Component.translatable("text.vampirism.action.deactivated_by_serveradmin"), false);
                            break;
                        case COOLDOWN:
                            player.displayClientMessage(Component.translatable("text.vampirism.action.cooldown_not_over"), true);
                            break;
                        case DISALLOWED:
                            player.displayClientMessage(Component.translatable("text.vampirism.action.disallowed"), true);
                        default://Everything alright
                    }
                } else {
                    LOGGER.error("Failed to find action with id {}", msg.actionId);
                }
            });
        });
        ctx.setPacketHandled(true);
    }

    private final ResourceLocation actionId;

    public CToggleActionPacket(ResourceLocation actionId) {
        this.actionId = actionId;
    }
}
