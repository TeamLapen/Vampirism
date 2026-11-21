package de.teamlapen.vampirism.common.network;

import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.factions.minions.MinionEntity;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IDraculaPlayer;
import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundHeritagePacket;
import de.teamlapen.vampirism.common.network.packets.server.*;
import de.teamlapen.vampirism.common.util.supporter.Supporter;
import de.teamlapen.vampirism.common.world.heritage.HeritageLectern;
import de.teamlapen.vampirism.common.world.heritage.HeritageMembership;
import de.teamlapen.vampirism.common.world.heritage.HeritageWorldData;
import de.teamlapen.vampirism.common.world.items.HeritageBookItem;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.inventory.HunterBasicMenu;
import de.teamlapen.vampirism.common.world.inventory.HunterTrainerMenu;
import de.teamlapen.vampirism.common.world.inventory.RevertBackMenu;
import de.teamlapen.vampirism.common.world.inventory.VampireBeaconMenu;
import de.teamlapen.vampirism.common.world.items.VampireSwordItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ServerPayloadHandler {

    public static void handleAppearancePacket(ServerboundAppearancePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity1 = context.player().level().getEntity(msg.entityId());
            if (entity1 instanceof Player player) {
                VampirePlayer vampirePlayer = VampirePlayer.get(player);
                msg.data().data().forEach((k, v) -> vampirePlayer.setAppearanceData((de.teamlapen.faction.common.world.entities.appearance.AppearanceKey) k, v));
                vampirePlayer.sync();
            } else if (entity1 instanceof MinionEntity<?> minion) {
                minion.getMinionData().ifPresent(minionData -> {
                    msg.data().data().forEach((k, v) -> minionData.setAppearanceData((de.teamlapen.faction.common.world.entities.appearance.AppearanceKey) k, v));
                    minion.sync();
                });
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
                case JUMP -> IDraculaPlayer.getPresentDracula(player).ifPresent(IDraculaPlayer::swingWings);
                case GROW_WINGS -> IDraculaPlayer.getPresentDracula(player).ifPresent(IDraculaPlayer::toggleWings);
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

    public static void handleRequestHeritagePacket(ServerboundRequestHeritagePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            HeritageWorldData heritageData = HeritageWorldData.getData(player.level().getServer());
            if (msg.lecternPos() != null || msg.targetName() != null) {
                ItemStack book = msg.lecternPos() != null
                        ? HeritageLectern.getReadableLectern(player, msg.lecternPos()).map(LecternBlockEntity::getBook).orElse(ItemStack.EMPTY)
                        : findHeldHeritageBook(player, msg.targetName());
                String bookName = HeritageBookItem.getTargetName(book);
                if (bookName == null) {
                    sendUnavailableHeritagePacket(player, List.of(), null);
                    return;
                }
                if (!HeritageBookItem.isResearched(book)) {
                    String notice = HeritageBookItem.getResearchProgress(book) > 0 ? "gui.vampirism.heritage.researching" : "gui.vampirism.heritage.blank";
                    sendUnavailableHeritagePacket(player, List.of(), Component.translatable(notice));
                    return;
                }

                Optional<UUID> namedPlayer = heritageData.findPlayerByName(bookName);
                if (namedPlayer.isPresent()) {
                    sendPlayerHeritages(player, heritageData, namedPlayer.get(), msg.heritageId());
                } else {
                    findNpcHeritageMembership(heritageData, bookName).ifPresentOrElse(
                            membership -> sendHeritagePacket(player, heritageData, membership, List.of(membership.heritageId()), true),
                            () -> sendUnavailableHeritagePacket(player, List.of(), null)
                    );
                }
                return;
            }

            sendPlayerHeritages(player, heritageData, player.getUUID(), msg.heritageId());
        });
    }

    /**
     * Sends the requested one of the heritages a player was part of, by default the current one. A player without a
     * current heritage, e.g. after leaving the vampire faction, gets their first previous one. The ids of all of them
     * are sent along, so the screen can switch between them.
     */
    private static void sendPlayerHeritages(ServerPlayer receiver, HeritageWorldData heritageData, UUID playerId, @Nullable UUID requestedHeritageId) {
        List<HeritageWorldData.HeritageHistory> history = heritageData.getHeritagesForPlayer(playerId);
        Optional<HeritageMembership> current = heritageData.getMembership(playerId);
        List<UUID> heritageIds = new ArrayList<>();
        current.map(HeritageMembership::heritageId).ifPresent(heritageIds::add);
        history.stream()
                .map(HeritageWorldData.HeritageHistory::heritageId)
                .filter(heritageId -> !heritageIds.contains(heritageId))
                .sorted()
                .forEach(heritageIds::add);

        UUID selectedId = requestedHeritageId != null ? requestedHeritageId : heritageIds.isEmpty() ? null : heritageIds.getFirst();
        Optional<HeritageMembership> selected = current.filter(membership -> membership.heritageId().equals(selectedId))
                .or(() -> history.stream()
                        .filter(heritage -> heritage.heritageId().equals(selectedId))
                        .map(HeritageWorldData.HeritageHistory::membership)
                        .findFirst());
        selected.ifPresentOrElse(
                membership -> sendHeritagePacket(receiver, heritageData, membership, heritageIds,
                        current.map(currentMembership -> currentMembership.heritageId().equals(membership.heritageId())).orElse(false)),
                () -> sendUnavailableHeritagePacket(receiver, heritageIds, null)
        );
    }

    public static void handleReadLecternPacket(ServerboundReadLecternPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> HeritageLectern.read((ServerPlayer) context.player(), msg.pos()));
    }

    public static void handleTakeLecternBookPacket(ServerboundTakeLecternBookPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> HeritageLectern.takeBook((ServerPlayer) context.player(), msg.pos()));
    }

    public static void handleRunAwayPacket(ServerboundRunAwayPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> HeritageLectern.runAway((ServerPlayer) context.player(), msg.pos()));
    }

    private static ItemStack findHeldHeritageBook(ServerPlayer player, String targetName) {
        for (ItemStack stack : List.of(player.getMainHandItem(), player.getOffhandItem())) {
            if (targetName.equals(HeritageBookItem.getTargetName(stack))) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static Optional<HeritageMembership> findNpcHeritageMembership(HeritageWorldData heritageData, String targetName) {
        return heritageData.findNamedNpcMembership(targetName)
                .or(() -> findPredefinedHeritageMembership(targetName));
    }

    private static Optional<HeritageMembership> findPredefinedHeritageMembership(String targetName) {
        var supporterManager = VampirismMod.services().supporterManager();
        return supporterManager.getSupporter()
                .filter(supporter -> supporter.name().getString().equalsIgnoreCase(targetName))
                .map(Supporter::player)
                .findFirst()
                .flatMap(supporterManager::getPredefinedHeritage)
                .map(heritage -> HeritageWorldData.membershipForNamedNpc(heritage.id()));
    }

    /**
     * @param notice shown instead of the default text that no heritage has been recorded
     */
    private static void sendUnavailableHeritagePacket(ServerPlayer player, List<UUID> heritageIds, @Nullable Component notice) {
        // nothing to show, so clean pages
        player.connection.send(new ClientboundHeritagePacket(null, List.of(), List.of(), null, heritageIds, true, notice));
    }

    private static void sendHeritagePacket(ServerPlayer player, HeritageWorldData heritageData, HeritageMembership membership, List<UUID> heritageIds, boolean currentHeritage) {
        String namedNpc = membership.namedNpc();
        var supporterManager = VampirismMod.services().supporterManager();
        var predefinedHeritage = namedNpc == null
                ? Optional.<de.teamlapen.vampirism.common.util.SupporterManager.PredefinedHeritage>empty()
                : supporterManager.getPredefinedHeritageById(namedNpc).or(() -> supporterManager.getPredefinedHeritage(namedNpc));
        var members = heritageData.getMembers(membership.heritageId()).values().stream()
                .map(member -> {
                    String parentNpcId = member.parentNpcId();
                    if (parentNpcId == null && member.parentPlayerId() == null && namedNpc != null && predefinedHeritage.isPresent()
                            && predefinedHeritage.get().members().stream().anyMatch(staticMember -> staticMember.id().equals(namedNpc))) {
                        parentNpcId = namedNpc;
                    }
                    return new ClientboundHeritagePacket.Member(member.playerId(), member.playerName(), member.parentPlayerId(), parentNpcId);
                })
                .toList();
        var staticMembers = predefinedHeritage.stream()
                .flatMap(heritage -> heritage.members().stream())
                .map(member -> new ClientboundHeritagePacket.StaticMember(member.id(), member.name(), member.parentId(), member.lostHistory()))
                .toList();
        String founderName = predefinedHeritage.isPresent() ? null : namedNpc;
        if (founderName != null) {
            founderName = supporterManager.getSupporter(founderName)
                    .map(Supporter::name)
                    .map(Component::getString)
                    .orElse(founderName);
        }
        player.connection.send(new ClientboundHeritagePacket(founderName, staticMembers, members, membership.heritageId(), heritageIds, currentHeritage));
    }

}
