package de.teamlapen.vampirism.network;

import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.inventory.HunterBasicMenu;
import de.teamlapen.vampirism.inventory.HunterTrainerMenu;
import de.teamlapen.vampirism.items.OblivionItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Collection of simple input events that do not need any additional information
 */
public record ServerboundSimpleInputEvent(Type type) implements IMessage {

    static void encode(@NotNull ServerboundSimpleInputEvent msg, @NotNull FriendlyByteBuf buf) {
        buf.writeEnum(msg.type);
    }

    static @NotNull ServerboundSimpleInputEvent decode(@NotNull FriendlyByteBuf buf) {
        Type t = buf.readEnum(Type.class);
        return new ServerboundSimpleInputEvent(t);
    }

    static void handle(final @NotNull ServerboundSimpleInputEvent msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayer player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            Optional<? extends IFactionPlayer<?>> factionPlayerOpt = FactionPlayerHandler.getOpt(player).map(FactionPlayerHandler::getCurrentFactionPlayer).orElseGet(Optional::empty);
            //Try to keep this simple
            switch (msg.type) {
                case FINISH_SUCK_BLOOD:
                    VampirePlayer.getOpt(player).ifPresent(vampire -> vampire.endFeeding(true));
                    break;
                case RESET_SKILLS:
                    InventoryHelper.removeItemFromInventory(player.getInventory(), new ItemStack(ModItems.OBLIVION_POTION.get()));
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
                    if (player.containerMenu instanceof HunterTrainerMenu) {
                        ((HunterTrainerMenu) player.containerMenu).onLevelupClicked();
                    }
                    break;
                case BASIC_HUNTER_LEVELUP:
                    if (player.containerMenu instanceof HunterBasicMenu) {
                        ((HunterBasicMenu) player.containerMenu).onLevelUpClicked();
                    }
                    break;
                case SHOW_MINION_CALL_SELECTION:
                    ClientboundRequestMinionSelectPacket.createRequestForPlayer(player, ClientboundRequestMinionSelectPacket.Action.CALL).ifPresent(VampirismMod.dispatcher::sendToServer);
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

    public enum Type {
        FINISH_SUCK_BLOOD, RESET_SKILLS, REVERT_BACK, TOGGLE_VAMPIRE_VISION, TRAINER_LEVELUP, BASIC_HUNTER_LEVELUP, SHOW_MINION_CALL_SELECTION, VAMPIRISM_MENU, RESURRECT, GIVE_UP
    }
}
