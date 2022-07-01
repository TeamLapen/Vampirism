package de.teamlapen.vampirism.network;

import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.HunterBasicContainer;
import de.teamlapen.vampirism.inventory.container.HunterTrainerContainer;
import de.teamlapen.vampirism.items.OblivionItem;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.Validate;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Collection of simple input events that do not need any additional information
 */
public class CSimpleInputEvent implements IMessage {

    static void encode(CSimpleInputEvent msg, PacketBuffer buf) {
        buf.writeEnum(msg.type);
    }

    static CSimpleInputEvent decode(PacketBuffer buf) {
        Type t = buf.readEnum(Type.class);
        return new CSimpleInputEvent(t);
    }

    static void handle(final CSimpleInputEvent msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayerEntity player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            Optional<? extends IFactionPlayer<?>> factionPlayerOpt = FactionPlayerHandler.getOpt(player).map(FactionPlayerHandler::getCurrentFactionPlayer).orElseGet(Optional::empty);
            //Try to keep this simple
            switch (msg.type) {
                case FINISH_SUCK_BLOOD:
                    VampirePlayer.getOpt(player).ifPresent(vampire -> vampire.endFeeding(true));
                    break;
                case RESET_SKILLS:
                    InventoryHelper.removeItemFromInventory(player.inventory, new ItemStack(ModItems.OBLIVION_POTION.get()));
                    factionPlayerOpt.ifPresent(OblivionItem::applyEffect);
                    break;
                case REVERT_BACK:
                    FactionPlayerHandler.getOpt(player).ifPresent(handler -> {
                        handler.leaveFaction(!player.server.isHardcore());
                    });
                    break;
                case TOGGLE_VAMPIRE_VISION:
                    VampirePlayer.getOpt(player).ifPresent(VampirePlayer::switchVision);
                    break;
                case TRAINER_LEVELUP:
                    if (player.containerMenu instanceof HunterTrainerContainer) {
                        ((HunterTrainerContainer) player.containerMenu).onLevelupClicked();
                    }
                    break;
                case BASIC_HUNTER_LEVELUP:
                    if (player.containerMenu instanceof HunterBasicContainer) {
                        ((HunterBasicContainer) player.containerMenu).onLevelUpClicked();
                    }
                    break;
                case SHOW_MINION_CALL_SELECTION:
                    SRequestMinionSelectPacket.createRequestForPlayer(player, SRequestMinionSelectPacket.Action.CALL).ifPresent(VampirismMod.dispatcher::sendToServer);
                    break;
                case VAMPIRISM_MENU:
                    factionPlayerOpt.ifPresent(fPlayer -> fPlayer.getTaskManager().openVampirismMenu());
                    break;
                case RESURRECT:
                    VampirePlayer.getOpt(player).ifPresent(VampirePlayer::tryResurrect);
                    break;
                case GIVE_UP:
                    VampirePlayer.getOpt(player).ifPresent(VampirePlayer::giveUpDBNO);
                    break;
            }
        });
        ctx.setPacketHandled(true);
    }
    private final Type type;

    public CSimpleInputEvent(Type type) {
        this.type = type;
    }

    public enum Type {
        FINISH_SUCK_BLOOD, RESET_SKILLS, REVERT_BACK, TOGGLE_VAMPIRE_VISION, TRAINER_LEVELUP, BASIC_HUNTER_LEVELUP, SHOW_MINION_CALL_SELECTION, VAMPIRISM_MENU, RESURRECT, GIVE_UP
    }
}
