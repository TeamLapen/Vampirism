package de.teamlapen.vampirism.common.network;

import de.teamlapen.factions.common.network.packets.server.*;
import de.teamlapen.vampirism.api.items.IHunterCrossbow;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.minions.MinionEntity;
import de.teamlapen.vampirism.common.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.inventory.HunterBasicMenu;
import de.teamlapen.vampirism.common.inventory.HunterTrainerMenu;
import de.teamlapen.vampirism.common.inventory.RevertBackMenu;
import de.teamlapen.vampirism.common.inventory.VampireBeaconMenu;
import de.teamlapen.vampirism.common.items.VampireSwordItem;
import de.teamlapen.vampirism.common.network.packets.server.*;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundSimpleInputEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPayloadHandler {

    public static void handleAppearancePacket(ServerboundAppearancePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity1 = context.player().level().getEntity(msg.entityId());
            if (entity1 instanceof Player player) {
                VampirePlayer.get(player).setSkinData(msg.data());
            } else if (entity1 instanceof MinionEntity<?> minion) {
                minion.getMinionData().ifPresent(minionData -> minionData.handleMinionAppearanceConfig(msg.name(), msg.data()));
//                SyncHelper.sync(minion); TODO
            }
        });
    }

    public static void handleNameItemPacket(ServerboundNameItemPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            msg.name().ifPresentOrElse(name -> {
                if (!org.apache.commons.lang3.StringUtils.isBlank(name)) {
                    ItemStack stack = context.player().getMainHandItem();
                    stack.set(DataComponents.CUSTOM_NAME, Component.literal(name).withStyle(ChatFormatting.AQUA));
                }
            }, () -> {
                ItemStack stack = context.player().getMainHandItem();
                if (stack.getItem() instanceof VampireSwordItem swordItem) {
                    swordItem.doNotName(stack);
                }
            });
        });
    }

    public static void handleSelectAmmoTypePacket(ServerboundSelectAmmoTypePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            ItemStack stack = context.player().getMainHandItem();
            if (stack.getItem() instanceof IHunterCrossbow crossbow && crossbow.canSelectAmmunition(stack)) {
                crossbow.setAmmunition(stack, msg.ammoId());
            }
            ItemStack offhand = context.player().getOffhandItem();
            if (offhand.getItem() instanceof IHunterCrossbow crossbow && crossbow.canSelectAmmunition(offhand)) {
                crossbow.setAmmunition(offhand, msg.ammoId());
            }
        });
    }

    public static void handleSetVampireBeaconPacket(ServerboundSetVampireBeaconPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof VampireBeaconMenu beaconMenu && beaconMenu.stillValid(context.player())) {
                beaconMenu.updateEffects(msg.effect(), msg.amplifier());
            }
        });
    }

    public static void handleSimpleInputEvent(ServerboundSimpleInputEvent msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            //Try to keep this simple
            final VampirePlayer vampirePlayer = VampirePlayer.get(player);
            switch (msg.event()) {
                case FINISH_SUCK_BLOOD -> vampirePlayer.endFeeding(true);
                case REVERT_BACK -> {
                    if (player.containerMenu instanceof RevertBackMenu menu) {
                        menu.consume(player);
                    }
                    handler.leaveFaction(!player.level().getServer().isHardcore());
                }
                case TOGGLE_VAMPIRE_VISION -> {
                    vampirePlayer.switchVision();
                    vampirePlayer.sync();
                }
                case TRAINER_LEVELUP -> {
                    if (player.containerMenu instanceof HunterTrainerMenu) {
                        ((HunterTrainerMenu) player.containerMenu).onLevelupClicked();
                    }
                }
                case BASIC_HUNTER_LEVELUP -> {
                    if (player.containerMenu instanceof HunterBasicMenu menu) {
                        menu.onLevelUpClicked(player);
                    }
                }
                case RESURRECT -> vampirePlayer.tryResurrect();
                case GIVE_UP -> vampirePlayer.giveUpDBNO();
            }
        });
    }

    public static void handleStartFeedingPacket(ServerboundStartFeedingPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            VampirePlayer vampire = VampirePlayer.get(context.player());
            msg.target().ifLeft(vampire::biteEntity);
            msg.target().ifRight(blockContact -> vampire.biteBlock(blockContact.pos(), blockContact.side()));
        });
    }

}
