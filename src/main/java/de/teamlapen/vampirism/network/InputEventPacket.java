package de.teamlapen.vampirism.network;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Sends any input related event to the server
 */
public class InputEventPacket implements IMessage {

    public static final String TOGGLEACTION = "ta";
    public static final String UNLOCKSKILL = "us";
    public static final String NAME_ITEM = "ni";
    public static final String TOGGLE_LOCK_MINION_TASK = "lt";
    public static final String DELETE_REFINEMENT = "dr";


    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SPLIT = "&";

    static void encode(InputEventPacket msg, PacketBuffer buf) {
        buf.writeUtf(msg.action + SPLIT + msg.param);
    }

    static InputEventPacket decode(PacketBuffer buf) {
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

    public static void handle(final InputEventPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        Validate.notNull(msg.action);
        ServerPlayerEntity player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            Optional<? extends IFactionPlayer<?>> factionPlayerOpt = FactionPlayerHandler.getOpt(player).map(FactionPlayerHandler::getCurrentFactionPlayer).orElseGet(Optional::empty);
            switch (msg.action) {

                case TOGGLEACTION: {
                    ResourceLocation id = new ResourceLocation(msg.param);
                    factionPlayerOpt.ifPresent(factionPlayer -> {
                        IActionHandler<?> actionHandler = factionPlayer.getActionHandler();
                        IAction action = ModRegistries.ACTIONS.getValue(id);
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
                            LOGGER.error("Failed to find action with id {}", id);
                        }
                    });


                    break;
                }
                case UNLOCKSKILL:
                    factionPlayerOpt.ifPresent(factionPlayer -> {
                        ISkill skill = ModRegistries.SKILLS.getValue(new ResourceLocation(msg.param));
                        if (skill != null) {
                            ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
                            ISkillHandler.Result result = skillHandler.canSkillBeEnabled(skill);
                            if (result == ISkillHandler.Result.OK) {
                                skillHandler.enableSkill(skill);
                                if (factionPlayer instanceof ISyncable.ISyncableEntityCapabilityInst && skillHandler instanceof SkillHandler) {
                                    //does this cause problems with addons?
                                    CompoundNBT sync = new CompoundNBT();
                                    ((SkillHandler<?>) skillHandler).writeUpdateForClient(sync);
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
                case NAME_ITEM:
                    String name = msg.param;
                    if (VampirismVampireSword.DO_NOT_NAME_STRING.equals(name)) {
                        ItemStack stack = player.getMainHandItem();
                        if (stack.getItem() instanceof VampirismVampireSword) {
                            ((VampirismVampireSword) stack.getItem()).doNotName(stack);
                        }
                    } else if (!org.apache.commons.lang3.StringUtils.isBlank(name)) {
                        ItemStack stack = player.getMainHandItem();
                        stack.setHoverName(new StringTextComponent(name).withStyle(TextFormatting.AQUA));
                    }
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
