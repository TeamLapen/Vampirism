package de.teamlapen.vampirism.server;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.lib.lib.storage.IAttachedSyncable;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.data.ServerSkillTreeData;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.entity.player.TaskManager;
import de.teamlapen.vampirism.entity.player.actions.ActionHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.inventory.HunterBasicMenu;
import de.teamlapen.vampirism.inventory.HunterTrainerMenu;
import de.teamlapen.vampirism.inventory.RevertBackMenu;
import de.teamlapen.vampirism.inventory.VampireBeaconMenu;
import de.teamlapen.vampirism.items.OblivionItem;
import de.teamlapen.vampirism.items.VampireSwordItem;
import de.teamlapen.vampirism.network.*;
import de.teamlapen.vampirism.util.RegUtil;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Optional;

import static de.teamlapen.vampirism.network.ServerboundSelectMinionTaskPacket.*;

public class ServerPayloadHandler {

    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();
    private static final Logger LOGGER = LogManager.getLogger();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleActionBindingPacket(ServerboundActionBindingPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> FactionPlayerHandler.get(context.player()).setBoundAction(msg.actionBindingId(), msg.action(), false, false));
    }

    public void handleAppearancePacket(ServerboundAppearancePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity1 = context.player().level().getEntity(msg.entityId());
            if (entity1 instanceof Player player) {
                VampirePlayer.get(player).setSkinData(msg.data());
            } else if (entity1 instanceof MinionEntity<?> minion) {
                minion.getMinionData().ifPresent(minionData -> minionData.handleMinionAppearanceConfig(msg.name(), msg.data()));
                HelperLib.sync(minion);
            }
        });
    }

    public void handleDeleteRefinementPacket(ServerboundDeleteRefinementPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> FactionPlayerHandler.getCurrentFactionPlayer(context.player()).ifPresent(fp -> fp.getSkillHandler().removeRefinementItem(msg.slot())));
    }

    public void handleNameItemPacket(ServerboundNameItemPacket msg, IPayloadContext context) {
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

    public void handleSelectAmmoTypePacket(ServerboundSelectAmmoTypePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
                ItemStack stack = context.player().getMainHandItem();
                if (stack.getItem() instanceof IVampirismCrossbow crossbow && crossbow.canSelectAmmunition(stack)) {
                    crossbow.setAmmunition(stack, msg.ammoId());
                }
        });
    }

    public void handleSelectMinionTaskPacket(ServerboundSelectMinionTaskPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
                FactionPlayerHandler fp = FactionPlayerHandler.get(context.player());
                PlayerMinionController controller = MinionWorldData.getData(context.player().level()).get().getOrCreateController(fp);
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
                            context.player().displayClientMessage(Component.translatable("text.vampirism.minion_is_still_recovering", controller.contactMinionData(msg.minionID(), MinionData::getFormattedName).orElseGet(() -> Component.literal("1"))), true);
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
    }

    public void handleSetVampireBeaconPacket(ServerboundSetVampireBeaconPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
                if (context.player().containerMenu instanceof VampireBeaconMenu beaconMenu && beaconMenu.stillValid(context.player())) {
                    beaconMenu.updateEffects(msg.effect(), msg.amplifier());
                }
        });
    }

    public void handleSimpleInputEvent(ServerboundSimpleInputEvent msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            Optional<? extends IFactionPlayer<?>> factionPlayerOpt = FactionPlayerHandler.getCurrentFactionPlayer(player);
            //Try to keep this simple
            switch (msg.event()) {
                case FINISH_SUCK_BLOOD -> VampirePlayer.get(player).endFeeding(true);
                case RESET_SKILLS -> {
                    InventoryHelper.removeItemFromInventory(player.getInventory(), new ItemStack(ModItems.OBLIVION_POTION.get()));
                    factionPlayerOpt.ifPresent(OblivionItem::applyEffect);
                }
                case REVERT_BACK -> {
                    if (player.containerMenu instanceof RevertBackMenu menu) {
                        menu.consume();
                    }
                    FactionPlayerHandler.get(player).leaveFaction(!player.server.isHardcore());
                }
                case TOGGLE_VAMPIRE_VISION -> VampirePlayer.get(player).switchVision();
                case TRAINER_LEVELUP -> {
                    if (player.containerMenu instanceof HunterTrainerMenu) {
                        ((HunterTrainerMenu) player.containerMenu).onLevelupClicked();
                    }
                }
                case BASIC_HUNTER_LEVELUP -> {
                    if (player.containerMenu instanceof HunterBasicMenu) {
                        ((HunterBasicMenu) player.containerMenu).onLevelUpClicked();
                    }
                }
                case SHOW_MINION_CALL_SELECTION -> ClientboundRequestMinionSelectPacket.createRequestForPlayer(player, ClientboundRequestMinionSelectPacket.Action.CALL).ifPresent(a -> player.connection.send(a));
                case VAMPIRISM_MENU -> factionPlayerOpt.ifPresent(fPlayer -> fPlayer.getTaskManager().openVampirismMenu());
                case RESURRECT -> VampirePlayer.get(player).tryResurrect();
                case GIVE_UP -> VampirePlayer.get(player).giveUpDBNO();
            }
        });
    }

    public void handleStartFeedingPacket(ServerboundStartFeedingPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            VampirePlayer vampire = VampirePlayer.get(context.player());
                msg.target().ifLeft(vampire::biteEntity);
                msg.target().ifRight(vampire::biteBlock);
        });
    }

    public void handleTaskActionPacket(ServerboundTaskActionPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> FactionPlayerHandler.getCurrentFactionPlayer(context.player()).map(IFactionPlayer::getTaskManager).ifPresent(m -> ((TaskManager) m).handleTaskActionMessage(msg)));
    }

    public void handleToggleActionPacket(ServerboundToggleActionPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Optional<? extends IFactionPlayer<?>> factionPlayerOpt = FactionPlayerHandler.getCurrentFactionPlayer(player);
            factionPlayerOpt.ifPresent(factionPlayer -> {
                IAction.ActivationContext activationContext = msg.target() != null ? msg.target().map(entityId -> {
                    Entity e = player.getCommandSenderWorld().getEntity(entityId);
                    if (e == null) {
                        LOGGER.warn("Could not find entity {} the player was looking at when toggling action", entityId);
                    }
                    return new ActionHandler.ActivationContext(e);
                }, ActionHandler.ActivationContext::new) : new ActionHandler.ActivationContext();

                IActionHandler<?> actionHandler = factionPlayer.getActionHandler();
                IAction action = RegUtil.getAction(msg.actionId());
                if (action != null) {
                    IAction.PERM r = actionHandler.toggleAction(action, activationContext);
                    switch (r) {
                        case NOT_UNLOCKED -> player.displayClientMessage(Component.translatable("text.vampirism.action.not_unlocked"), true);
                        case DISABLED -> player.displayClientMessage(Component.translatable("text.vampirism.action.deactivated_by_serveradmin"), false);
                        case COOLDOWN -> player.displayClientMessage(Component.translatable("text.vampirism.action.cooldown_not_over"), true);
                        case DISALLOWED -> player.displayClientMessage(Component.translatable("text.vampirism.action.disallowed"), true);
                        case PERMISSION_DISALLOWED -> player.displayClientMessage(Component.translatable("text.vampirism.action.permission_disallowed"), false);
                        default -> {
                            //Everything alright
                        }
                    }
                } else {
                    LOGGER.error("Failed to find action with id {}", msg.actionId());
                }
            });
        });
    }

    public void handleToggleMinionTaskLock(ServerboundToggleMinionTaskLock msg, IPayloadContext context) {
        context.enqueueWork(() -> {
                FactionPlayerHandler fp = FactionPlayerHandler.get(context.player());
                PlayerMinionController controller = MinionWorldData.getData(context.player().level()).get().getOrCreateController(fp);
                controller.contactMinionData(msg.minionID(), data -> data.setTaskLocked(!data.isTaskLocked()));
                controller.contactMinion(msg.minionID(), MinionEntity::onTaskChanged);
        });
    }

    public void handleUnlockSkillPacket(ServerboundUnlockSkillPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
        Player player = context.player();
        Optional<? extends IFactionPlayer<?>> factionPlayerOpt = FactionPlayerHandler.getCurrentFactionPlayer(player);
        factionPlayerOpt.ifPresent(factionPlayer -> {
            ISkill skill = RegUtil.getSkill(msg.skillId());
            if (skill != null) {
                ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
                ISkillHandler.Result result = skillHandler.canSkillBeEnabled(skill);
                if (result == ISkillHandler.Result.OK) {
                    skillHandler.enableSkill(skill);
                    if (factionPlayer instanceof IAttachedSyncable && skillHandler instanceof SkillHandler<?> skillHandler1) {
                        //does this cause problems with addons?
                        CompoundTag sync = new CompoundTag();
                        sync.put(skillHandler1.nbtKey(), skillHandler1.serializeUpdateNBT(player.registryAccess()));
                        HelperLib.sync((IAttachedSyncable) factionPlayer, sync, ((IAttachedSyncable) factionPlayer).asEntity(), true);
                    }

                } else {
                    LOGGER.warn("Skill {} cannot be activated for {} ({})", skill, player, result);
                }
            } else {
                LOGGER.warn("Skill {} was not found so {} cannot activate it", msg.skillId(), player);
            }
        });
        });
    }

    public void handleUpgradeMinionStatPacket(ServerboundUpgradeMinionStatPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Entity entity = player.level().getEntity(msg.entityId());
            if (entity instanceof MinionEntity) {
                if (((MinionEntity<?>) entity).getMinionData().map(d -> d.upgradeStat(msg.statId(), (MinionEntity<?>) entity)).orElse(false)) {
                    HelperLib.sync((MinionEntity<?>) entity);
                }
            }
        });
    }

    public void handleRequestSkillTreePacket(ServerboundRequestSkillTreePacket msg, IPayloadContext context) {
        context.reply(ClientboundSkillTreePacket.of(ServerSkillTreeData.instance().getConfigurations()));
    }

}
