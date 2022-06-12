package de.teamlapen.vampirism.network;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.inventory.container.HunterBasicContainer;
import de.teamlapen.vampirism.inventory.container.HunterTrainerContainer;
import de.teamlapen.vampirism.inventory.container.VampirismContainer;
import de.teamlapen.vampirism.items.OblivionItem;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Sends any input related event to the server
 */
public class InputEventPacket implements IMessage {

    public static final String SUCKBLOOD = "sb";
    public static final String ENDSUCKBLOOD = "esb";
    public static final String TOGGLEACTION = "ta";
    public static final String UNLOCKSKILL = "us";
    public static final String RESETSKILL = "rs";
    public static final String TRAINERLEVELUP = "tl";
    public static final String REVERTBACK = "rb";
    public static final String VAMPIRE_VISION_TOGGLE = "vvt";
    public static final String BASICHUNTERLEVELUP = "bl";
    public static final String DRINK_BLOOD_BLOCK = "db";
    public static final String NAME_ITEM = "ni";
    public static final String SELECT_CALL_MINION = "sm";
    public static final String TOGGLE_LOCK_MINION_TASK = "lt";
    public static final String RESURRECT = "rst";
    public static final String DIE = "die";
    public static final String OPEN_VAMPIRISM_MENU = "ovm";
    public static final String DELETE_REFINEMENT = "dr";


    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SPLIT = "&";

    static void encode(InputEventPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.action + SPLIT + msg.param);
    }

    static InputEventPacket decode(FriendlyByteBuf buf) {
        String[] s = buf.readUtf(50).split(SPLIT);
        InputEventPacket msg = new InputEventPacket();
        msg.action = s[0];
        if (s.length > 1) {
            msg.param = s[1];
        } else {
            msg.param = "";
        }
        return msg;
    }

    public static <T extends IFactionPlayer<T>> void handle(final InputEventPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        Validate.notNull(msg.action);
        ServerPlayer player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            //noinspection unchecked
            Optional<T> factionPlayerOpt = (Optional<T>) FactionPlayerHandler.getOpt(player).map(FactionPlayerHandler::getCurrentFactionPlayer).orElse(Optional.empty());
            switch (msg.action) {
                case SUCKBLOOD: {
                    try {
                        int id = Integer.parseInt(msg.param);
                        if (id != 0) {
                            VampirePlayer.getOpt(player).ifPresent(vampire -> vampire.biteEntity(id));
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.error("Receiving invalid param {} for {}", msg.param, msg.action);
                    }

                    break;
                }
                case ENDSUCKBLOOD:
                    VampirePlayer.getOpt(player).ifPresent(vampire -> vampire.endFeeding(true));
                    break;
                case TOGGLEACTION: {
                    ResourceLocation id = new ResourceLocation(msg.param);
                    factionPlayerOpt.ifPresent(factionPlayer -> {
                        IActionHandler<T> actionHandler = factionPlayer.getActionHandler();
                        //noinspection unchecked
                        IAction<T> action = (IAction<T>) ModRegistries.ACTIONS.getValue(id);
                        if (action != null) {
                            IAction.PERM r = actionHandler.toggleAction(action);
                            switch (r) {
                                case NOT_UNLOCKED:
                                    player.displayClientMessage(new TranslatableComponent("text.vampirism.action.not_unlocked"), true);
                                    break;
                                case DISABLED:
                                    player.displayClientMessage(new TranslatableComponent("text.vampirism.action.deactivated_by_serveradmin"), false);
                                    break;
                                case COOLDOWN:
                                    player.displayClientMessage(new TranslatableComponent("text.vampirism.action.cooldown_not_over"), true);
                                    break;
                                case DISALLOWED:
                                    player.displayClientMessage(new TranslatableComponent("text.vampirism.action.disallowed"), true);
                                default://Everything alright
                            }
                        } else {
                            LOGGER.error("Failed to find action with id {}", id);
                        }
                    });


                    break;
                }
                case DRINK_BLOOD_BLOCK:
                    String[] coords = msg.param.split(":");
                    if (coords.length == 3) {
                        BlockPos pos = new BlockPos(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
                        VampirePlayer.getOpt(player).ifPresent(v -> v.biteBlock(pos));
                    } else {
                        LOGGER.warn("Received invalid {} parameter", DRINK_BLOOD_BLOCK);
                    }
                    break;
                case UNLOCKSKILL:
                    factionPlayerOpt.ifPresent(factionPlayer -> {
                        //noinspection unchecked
                        ISkill<T> skill = (ISkill<T>) ModRegistries.SKILLS.getValue(new ResourceLocation(msg.param));
                        if (skill != null) {
                            ISkillHandler<T> skillHandler = factionPlayer.getSkillHandler();
                            ISkillHandler.Result result = skillHandler.canSkillBeEnabled(skill);
                            if (result == ISkillHandler.Result.OK) {
                                skillHandler.enableSkill(skill);
                                if (factionPlayer instanceof ISyncable.ISyncableEntityCapabilityInst && skillHandler instanceof SkillHandler) {
                                    //does this cause problems with addons?
                                    CompoundTag sync = new CompoundTag();
                                    ((SkillHandler<T>) skillHandler).writeUpdateForClient(sync);
                                    HelperLib.sync((ISyncable.ISyncableEntityCapabilityInst) factionPlayer, sync, factionPlayer.getRepresentingPlayer(), false);
                                }

                            } else {
                                LOGGER.warn("Skill {} cannot be activated for {} ({})", skill, player, result);
                            }
                        } else {
                            LOGGER.warn("Skill {} was not found so {} cannot activate it", msg.param, player);
                        }
                    });


                    break;
                case RESETSKILL:
                    InventoryHelper.removeItemFromInventory(player.getInventory(), new ItemStack(ModItems.OBLIVION_POTION.get()));
                    factionPlayerOpt.ifPresent(OblivionItem::applyEffect);
                    break;
                case TRAINERLEVELUP:
                    if (player.containerMenu instanceof HunterTrainerContainer) {
                        ((HunterTrainerContainer) player.containerMenu).onLevelupClicked();
                    }
                    break;
                case REVERTBACK:
                    FactionPlayerHandler.getOpt(player).ifPresent(handler -> {
                        handler.setFactionAndLevel(null, 0);
                        player.displayClientMessage(new TranslatableComponent("command.vampirism.base.level.successful", player.getName(), VReference.VAMPIRE_FACTION.getName(), 0), true);
                        LOGGER.debug("Player {} left faction", player);
                        if (!ServerLifecycleHooks.getCurrentServer().isHardcore()) {
                            player.hurt(DamageSource.MAGIC, 1000);

                        }
                    });
                    break;
                case VAMPIRE_VISION_TOGGLE:
                    VampirePlayer.getOpt(player).ifPresent(VampirePlayer::switchVision);
                    break;
                case BASICHUNTERLEVELUP:
                    if (player.containerMenu instanceof HunterBasicContainer) {
                        ((HunterBasicContainer) player.containerMenu).onLevelUpClicked();
                    }
                    break;
                case NAME_ITEM:
                    String name = msg.param;
                    if (VampirismVampireSword.DO_NOT_NAME_STRING.equals(name)) {
                        ItemStack stack = player.getMainHandItem();
                        if (stack.getItem() instanceof VampirismVampireSword) {
                            ((VampirismVampireSword) stack.getItem()).doNotName(stack);
                        }
                    } else if (!org.apache.commons.lang3.StringUtils.isBlank(name)) {
                        ItemStack stack = player.getMainHandItem();
                        stack.setHoverName(new TextComponent(name).withStyle(ChatFormatting.AQUA));
                    }
                    break;
                case SELECT_CALL_MINION:
                    FactionPlayerHandler.getOpt(ctx.getSender()).ifPresent(fp -> {
                        PlayerMinionController controller = MinionWorldData.getData(ctx.getSender().server).getOrCreateController(fp);
                        Collection<Integer> ids = controller.getCallableMinions();
                        if (ids.size() > 0) {
                            List<Pair<Integer, Component>> minions = new ArrayList<>(ids.size());
                            ids.forEach(id -> controller.contactMinionData(id, data -> data.getFormattedName().copy()).ifPresent(n -> minions.add(Pair.of(id, n))));
                            VampirismMod.dispatcher.sendTo(new RequestMinionSelectPacket(RequestMinionSelectPacket.Action.CALL, minions), ctx.getSender());
                        } else {
                            SelectMinionTaskPacket.printRecoveringMinions(ctx.getSender(), controller.getRecoveringMinionNames());
                        }

                    });
                    break;
                case TOGGLE_LOCK_MINION_TASK:
                    try {
                        int id = Integer.parseInt(msg.param);
                        FactionPlayerHandler.getOpt(ctx.getSender()).ifPresent(fp -> {
                            PlayerMinionController controller = MinionWorldData.getData(ctx.getSender().server).getOrCreateController(fp);
                            controller.contactMinionData(id, data -> data.setTaskLocked(!data.isTaskLocked()));
                            controller.contactMinion(id, MinionEntity::onTaskChanged);

                        });
                    } catch (NumberFormatException e) {
                        LOGGER.error("Receiving invalid param {} for {}", msg.param, msg.action);
                    }
                    break;
                case OPEN_VAMPIRISM_MENU:
                    factionPlayerOpt.ifPresent(fPlayer -> {
                        if (player.isAlive()) {
                            player.openMenu(new MenuProvider() {
                                @Nonnull
                                @Override
                                public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
                                    return new VampirismContainer(i, playerInventory);
                                }

                                @Nonnull
                                @Override
                                public Component getDisplayName() {
                                    return new TranslatableComponent("");
                                }
                            });
                            fPlayer.getTaskManager().openVampirismMenu();
                        }
                    });
                    break;
                case RESURRECT:
                    VampirePlayer.getOpt(player).ifPresent(VampirePlayer::tryResurrect);
                    break;
                case DIE:
                    VampirePlayer.getOpt(player).ifPresent(VampirePlayer::giveUpDBNO);
                    break;
                case DELETE_REFINEMENT:
                    FactionPlayerHandler.getOpt(player).ifPresent(fph -> fph.getCurrentFactionPlayer().ifPresent(fp -> fp.getSkillHandler().removeRefinementItem(IRefinementItem.AccessorySlotType.valueOf(msg.param))));
                    break;
            }
            ctx.setPacketHandled(true);
        });
    }

    private String param;
    private String action;

    public InputEventPacket(String action, String param) {
        this.action = action;
        this.param = param;
    }

    private InputEventPacket() {

    }

}
