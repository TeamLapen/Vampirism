package de.teamlapen.factions.common.server;

import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.factions.api.actions.IActionResult;
import de.teamlapen.factions.api.entities.minion.IMinionTask;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.refinements.IRefinementHandler;
import de.teamlapen.factions.api.skills.ISkill;
import de.teamlapen.factions.api.skills.ISkillHandler;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import de.teamlapen.factions.api.tasks.ITaskManager;
import de.teamlapen.factions.common.actions.ActionHandler;
import de.teamlapen.factions.common.core.FactionItems;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.inventory.InventoryHelper;
import de.teamlapen.factions.common.items.OblivionPotionItem;
import de.teamlapen.factions.common.minions.MinionData;
import de.teamlapen.factions.common.minions.MinionEntity;
import de.teamlapen.factions.common.minions.MinionWorldData;
import de.teamlapen.factions.common.minions.PlayerMinionController;
import de.teamlapen.factions.common.network.packets.client.ClientboundRequestMinionSelectPacket;
import de.teamlapen.factions.common.network.packets.server.*;
import de.teamlapen.factions.common.skills.ClientboundSkillTreePacket;
import de.teamlapen.factions.common.skills.ServerSkillTreeData;
import de.teamlapen.factions.common.tasks.TaskManager;
import de.teamlapen.factions.common.util.RegUtil;
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
import java.util.Optional;

import static de.teamlapen.factions.common.network.packets.server.ServerboundSelectMinionTaskPacket.*;

public class ServerPayloadHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void handleActionBindingPacket(ServerboundActionBindingPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> FactionPlayerHandler.get(context.player()).setBoundAction(msg.actionBindingId(), msg.action(), false));
    }

    public static void handleDeleteRefinementPacket(ServerboundDeleteRefinementPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> IRefinementHandler.get(context.player()).ifPresent(handler -> handler.removeRefinement(msg.slot())));
    }

    public static void handleSelectMinionTaskPacket(ServerboundSelectMinionTaskPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            FactionPlayerHandler.get(context.player()).getLordPlayer().ifPresent(player -> {
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
                            context.player().displayClientMessage(Component.translatable("text.factions.minion.minion_is_still_recovering", controller.contactMinionData(msg.minionID(), MinionData::getFormattedName).orElseGet(() -> Component.literal("1"))), true);
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
                    InventoryHelper.removeItemFromInventory(player.getInventory(), new ItemStack((FactionItems.OBLIVION_POTION).get()));
                    handler.getCurrentSkillPlayer().ifPresent(OblivionPotionItem::applyEffect);
                }
                case SHOW_MINION_CALL_SELECTION -> ClientboundRequestMinionSelectPacket.createRequestForPlayer(player, ClientboundRequestMinionSelectPacket.Action.CALL).ifPresent(a -> player.connection.send(a));
                case FACTION_MENU -> handler.getTaskManager().ifPresent(ITaskManager::openFactionMenu);
            }
        });
    }

    public static void handleTaskActionPacket(ServerboundTaskActionPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            FactionPlayerHandler.get(context.player()).getTaskManager().ifPresent(m -> {
                ((TaskManager<?>) m).handleTaskActionMessage(msg);
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
            FactionPlayerHandler.get(player).<T>getActionHandler().ifPresent(handler -> {
                IAction.ActivationContext activationContext = msg.target() != null ? msg.target().map(entityId -> {
                    Entity e = player.level().getEntity(entityId);
                    if (e == null) {
                        LOGGER.warn("Could not find entity {} the player was looking at when toggling action", entityId);
                    }
                    return new ActionHandler.ActivationContext(e);
                }, ActionHandler.ActivationContext::new) : new ActionHandler.ActivationContext();

                Holder<IAction<?>> action = msg.action();
                if (action != null) {
                    @SuppressWarnings("unchecked")
                    IActionResult r = handler.toggleAction((Holder<IAction<T>>) (Object) action, activationContext);
                    if (!r.successful()) {
                        player.displayClientMessage(r.message(), r.sendToStatusBar());
                    } else {
                        handler.sync();
                    }
                }
            });
        });
    }

    public static void handleToggleMinionTaskLock(ServerboundToggleMinionTaskLock msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            FactionPlayerHandler.get(context.player()).getLordPlayer().flatMap(x -> MinionWorldData.getData(context.player().level()).map(y -> y.getOrCreateController(x))).ifPresent(controller -> {
                controller.contactMinionData(msg.minionID(), data -> data.setTaskLocked(!data.isTaskLocked()));
                controller.contactMinion(msg.minionID(), MinionEntity::onTaskChanged);
            });
        });
    }

    public static void handleRequestSkillTreePacket(ServerboundRequestSkillTreePacket msg, IPayloadContext context) {
        context.reply(ClientboundSkillTreePacket.of(ServerSkillTreeData.instance().getConfigurations()));
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

    public static void handleUpgradeMinionStatPacket(ServerboundUpgradeMinionStatPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Entity entity = player.level().getEntity(msg.entityId());
            if (entity instanceof MinionEntity) {
                if (((MinionEntity<?>) entity).getMinionData().map(d -> d.upgradeStat(msg.statId(), (MinionEntity<?>) entity)).orElse(false)) {
//                    SyncHelper.sync((MinionEntity<?>) entity); TODO
                }
            }
        });
    }
}
