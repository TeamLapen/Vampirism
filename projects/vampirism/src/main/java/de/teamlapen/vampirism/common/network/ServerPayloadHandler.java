package de.teamlapen.vampirism.common.network;

import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.factions.minions.MinionEntity;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IDraculaPlayer;
import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundHeritagePacket;
import de.teamlapen.vampirism.common.network.packets.server.*;
import de.teamlapen.vampirism.common.util.supporter.Supporter;
import de.teamlapen.vampirism.common.world.heritage.HeritageData;
import de.teamlapen.vampirism.common.world.heritage.HeritageWorldData;
import de.teamlapen.vampirism.common.world.heritage.HeritageManager;
import de.teamlapen.faction.common.core.FactionItems;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

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
                case RUN_AWAY_FROM_HERITAGE -> {
                    ItemStack potion = player.getMainHandItem();
                    if (!potion.is(FactionItems.OBLIVION_POTION.get())) {
                        potion = player.getOffhandItem();
                    }
                    if (potion.is(FactionItems.OBLIVION_POTION.get()) && HeritageData.get(player).getMembership().isPresent()) {
                        if (!player.getAbilities().instabuild) {
                            potion.shrink(1);
                        }
                        HeritageManager.runAwayFromHeritage(player);
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
                        player.sendOverlayMessage(Component.translatable("message.vampirism.heritage_run_away.success"));
                    }
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
            HeritageData.get(player).getMembership().ifPresentOrElse(membership -> {
                String namedNpc = membership.namedNpc();
                var supporterManager = VampirismMod.services().supporterManager();
                var predefinedHeritage = namedNpc == null
                        ? java.util.Optional.<de.teamlapen.vampirism.common.util.SupporterManager.PredefinedHeritage>empty()
                        : supporterManager.getPredefinedHeritageById(namedNpc).or(() -> supporterManager.getPredefinedHeritage(namedNpc));
                var members = HeritageWorldData.getData(player.level().getServer()).getMembers(membership.heritageId()).values().stream()
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
                player.connection.send(new ClientboundHeritagePacket(founderName, staticMembers, members));
            }, () -> player.connection.send(new ClientboundHeritagePacket(null, java.util.List.of(), java.util.List.of())));
        });
    }

}
