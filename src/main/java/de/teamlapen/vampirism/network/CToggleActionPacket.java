package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public class CToggleActionPacket implements IMessage {
    private static final Logger LOGGER = LogManager.getLogger();

    static void encode(CToggleActionPacket msg, PacketBuffer buf) {
        buf.writeResourceLocation(msg.actionId);
    }

    static CToggleActionPacket decode(PacketBuffer buf) {
        return new CToggleActionPacket(buf.readResourceLocation());
    }

    static void handle(CToggleActionPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayerEntity player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            Optional<? extends IFactionPlayer<?>> factionPlayerOpt = FactionPlayerHandler.getOpt(player).map(FactionPlayerHandler::getCurrentFactionPlayer).orElseGet(Optional::empty);
            factionPlayerOpt.ifPresent(factionPlayer -> {
                IActionHandler<?> actionHandler = factionPlayer.getActionHandler();
                IAction action = ModRegistries.ACTIONS.getValue(msg.actionId);
                if (action != null) {
                    IAction.PERM r = actionHandler.toggleAction(action);
                    switch (r) {
                        case NOT_UNLOCKED:
                            player.displayClientMessage(new TranslationTextComponent("text.vampirism.action.not_unlocked"), true);
                            break;
                        case DISABLED:
                            player.displayClientMessage(new TranslationTextComponent("text.vampirism.action.deactivated_by_serveradmin"), false);
                            break;
                        case COOLDOWN:
                            player.displayClientMessage(new TranslationTextComponent("text.vampirism.action.cooldown_not_over"), true);
                            break;
                        case DISALLOWED:
                            player.displayClientMessage(new TranslationTextComponent("text.vampirism.action.disallowed"), true);
                        default://Everything alright
                    }
                } else {
                    LOGGER.error("Failed to find action with id {}", msg.actionId);
                }
            });
        });
    }

    private final ResourceLocation actionId;

    public CToggleActionPacket(ResourceLocation actionId) {
        this.actionId = actionId;
    }
}
