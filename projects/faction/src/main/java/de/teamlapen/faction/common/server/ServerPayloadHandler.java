package de.teamlapen.faction.common.server;

import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.actions.IActionHandler;
import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.api.factions.refinements.IRefinementHandler;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.core.FactionItems;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.factions.actions.ActionHandler;
import de.teamlapen.faction.common.factions.minions.MinionData;
import de.teamlapen.faction.common.factions.minions.MinionEntity;
import de.teamlapen.faction.common.factions.minions.MinionWorldData;
import de.teamlapen.faction.common.factions.minions.PlayerMinionController;
import de.teamlapen.faction.common.factions.skills.SkillTreeGraphs;
import de.teamlapen.faction.common.factions.tasks.TaskManager;
import de.teamlapen.faction.common.network.packets.client.ClientboundRequestMinionSelectPacket;
import de.teamlapen.faction.common.network.packets.server.*;
import de.teamlapen.faction.common.util.RegUtil;
import de.teamlapen.faction.common.world.inventory.InventoryHelper;
import de.teamlapen.faction.common.world.items.OblivionPotionItem;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static de.teamlapen.faction.common.network.packets.server.ServerboundSelectMinionTaskPacket.*;

public class ServerPayloadHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void handleDeleteRefinementPacket(ServerboundDeleteRefinementPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> IRefinementHandler.get(context.player()).ifPresent(handler -> handler.removeRefinement(msg.slot())));
    }

    public static void handleSelectMinionTaskPacket(ServerboundSelectMinionTaskPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            FactionPlayerHandler.get(context.player()).getPlayerLord().ifPresent(player -> {
                PlayerMinionController controller = MinionWorldData.getData(context.player().level()).get().getOrCreateController(player);
                if (RECALL.equals(msg.taskID())) {
                    if (msg.minionID() < 0) {
                        Collection<Integer> ids = controller.recallMinions(false);
                        for (Integer id : ids) {
                            controller.createMinionEntityAtPlayer(id, context.player());
                        }
                        printRecoveringMinions(((ServerPlayer) context.player()), controller.getRecoveringMinionNames());

                    } else {
                        if (controller.recallMinion(msg.minionID())) {
                            controller.createMinionEntityAtPlayer(msg.minionID(), context.player());
                        } else {
                            context.player().sendOverlayMessage(Component.translatable("message.factionapi.minion.still_recovering", controller.contactMinionData(msg.minionID(), MinionData::getFormattedName).orElseGet(() -> Component.literal("1"))));
                        }
                    }
                } else if (RESPAWN.equals(msg.taskID())) {
                    Collection<Integer> ids = controller.getUnclaimedMinions();
                    for (Integer id : ids) {
                        controller.createMinionEntityAtPlayer(id, context.player());
                    }
                    printRecoveringMinions(((ServerPlayer) context.player()), controller.getRecoveringMinionNames());

                } else {
                    //noinspection unchecked
                    IMinionTask<?, MinionData> task = (IMinionTask<?, MinionData>) RegUtil.getMinionTask(msg.taskID());
                    if (task == null) {
                        LOGGER.error("Cannot find action to activate {}", msg.taskID());
                    } else if (msg.minionID() < -1) {
                        LOGGER.error("Illegal minion id {}", msg.minionID());
                    } else {
                        controller.activateTask(msg.minionID(), task);
                    }
                }
            });

        });
    }

    public static void handleSimpleInputEvent(ServerboundSimpleInputEvent msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            //Try to keep this simple
            switch (msg.event()) {
                case RESET_SKILLS -> {
                    ItemStack potion = InventoryHelper.getFirst(player.getInventory(), FactionItems.OBLIVION_POTION.get());
                    int portions = potion == null ? 0 : OblivionPotionItem.portionsOf(potion);
                    InventoryHelper.removeItemFromInventory(player.getInventory(), new ItemStack((FactionItems.OBLIVION_POTION).get()));
                    handler.getCurrentSkillPlayer().ifPresent(skillPlayer -> OblivionPotionItem.applyEffect(skillPlayer, portions));
                }
                case SHOW_MINION_CALL_SELECTION -> ClientboundRequestMinionSelectPacket.createRequestForPlayer(player, ClientboundRequestMinionSelectPacket.Action.CALL).ifPresent(a -> player.connection.send(a));
                case FACTION_MENU -> handler.openFactionMenu();
            }
        });
    }

    public static void handleTaskActionPacket(ServerboundTaskActionPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            FactionPlayerHandler.get(context.player()).getTaskManager().ifPresent(m -> {
                ((TaskManager) m).handleTaskActionMessage(msg);
                CustomPacketPayload updatePacket = m.getUpdatePacket(msg.entityId());
                if (updatePacket != null) {
                    context.reply(updatePacket);
                }
            });
        });
    }

    public static <T extends IFactionPlayer<T> & ISkillPlayer<T>> void handleToggleActionPacket(ServerboundToggleActionPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            IActionHandler.<T>get(player).ifPresent(handler -> {
                IAction.ActivationContext activationContext = msg.target() != null ? msg.target().map(entityId -> {
                    Entity e = player.level().getEntity(entityId);
                    if (e == null) {
                        LOGGER.warn("Could not find entity {} the player was looking at when toggling action", entityId);
                    }
                    return new ActionHandler.ActivationContext(e);
                }, ActionHandler.ActivationContext::new) : new ActionHandler.ActivationContext();

                Holder<? extends IAction<?>> action = msg.action();
                if (action != null) {
                    @SuppressWarnings("unchecked")
                    IActionResult r = handler.toggleAction((Holder<IAction<T>>) (Object) action, activationContext);
                    if (!r.successful()) {
                        if (r.sendToStatusBar()) {
                            player.sendOverlayMessage(r.message());
                        } else {
                            player.sendSystemMessage(r.message());
                        }
                    } else {
                        handler.sync();
                    }
                }
            });
        });
    }

    public static void handleToggleMinionTaskLock(ServerboundToggleMinionTaskLock msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            FactionPlayerHandler.get(context.player()).getPlayerLord().flatMap(x -> MinionWorldData.getData(context.player().level()).map(y -> y.getOrCreateController(x))).ifPresent(controller -> {
                controller.contactMinionData(msg.minionID(), data -> data.setTaskLocked(!data.isTaskLocked()));
                controller.contactMinion(msg.minionID(), MinionEntity::onTaskChanged);
            });
        });
    }

    public static <T extends IFactionPlayer<T> & ISkillPlayer<T>> void handleUnlockSkillPacket(ServerboundUnlockSkillPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Optional<T> factionPlayerOpt = FactionPlayerHandler.get(player).getCurrentSkillPlayer();
            factionPlayerOpt.ifPresent(factionPlayer -> {
                Holder<ISkill<?>> skill = msg.skill();
                if (skill != null) {
                    ISkillHandler<T> skillHandler = factionPlayer.getSkillHandler();
                    ISkillHandler.Result result = skillHandler.canSkillBeEnabled(skill, msg.skillTree());
                    if (result == ISkillHandler.Result.OK) {
                        //noinspection unchecked
                        skillHandler.enableSkill((Holder<ISkill<T>>) (Object) skill, msg.skillTree());

                    } else {
                        LOGGER.warn("Skill {} cannot be activated for {} ({})", skill, player, result);
                    }
                }
            });
        });
    }

    public static <T extends IFactionPlayer<T> & ISkillPlayer<T>> void handleForgetSkillPacket(ServerboundForgetSkillPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            Optional<T> factionPlayerOpt = FactionPlayerHandler.get(player).getCurrentSkillPlayer();
            factionPlayerOpt.ifPresent(factionPlayer -> {
                Holder<ISkill<?>> skill = msg.skill();
                ISkillHandler<T> skillHandler = factionPlayer.getSkillHandler();
                List<Holder<? extends ISkill<?>>> cascade = SkillTreeGraphs.get(player.level()).forgetCascade(msg.skillTree(), skill, skillHandler::isSkillEnabled);

                if (cascade.isEmpty()) {
                    LOGGER.warn("Skill {} cannot be forgotten by {} in skill tree {}", skill, player, msg.skillTree());
                    return;
                }

                boolean free = player.isCreative();
                if (!free && OblivionPotionItem.countPortions(player) < cascade.size()) {
                    LOGGER.warn("Player {} has too few oblivion potion portions to forget {} skill(s)", player, cascade.size());
                    return;
                }

                for (Holder<? extends ISkill<?>> forgotten : cascade) {
                    //noinspection unchecked
                    skillHandler.disableSkill((Holder<ISkill<T>>) forgotten, msg.skillTree());
                }

                if (!free) {
                    OblivionPotionItem.consumePortions(player, cascade.size());
                }
            });
        });
    }

    public static void handleUpgradeMinionStatPacket(ServerboundUpgradeMinionStatPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Entity entity = player.level().getEntity(msg.entityId());
            if (entity instanceof MinionEntity<?> minion) {
                if (minion.getMinionData().map(d -> d.upgradeStat(msg.stat(), minion)).orElse(false)) {
                    minion.sync();
                }
            }
        });
    }

    public static void handleResetMinionStatPacket(ServerboundResetMinionStatPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Entity entity = player.level().getEntity(msg.entityId());
            if (entity instanceof MinionEntity<?> minion) {
                minion.getMinionData().ifPresent(d -> {
                    d.resetStats(minion);
                    minion.sync();
                });
            }
        });
    }
}
