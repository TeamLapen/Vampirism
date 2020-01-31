package de.teamlapen.vampirism.network;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.BloodPotionTableContainer;
import de.teamlapen.vampirism.inventory.container.HunterBasicContainer;
import de.teamlapen.vampirism.inventory.container.HunterTrainerContainer;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public static final String CRAFT_BLOOD_POTION = "cb";
    public static final String OPEN_BLOOD_POTION = "ob";
    public static final String BASICHUNTERLEVELUP = "bl";
    public static final String DRINK_BLOOD_BLOCK = "db";
    public static final String NAME_ITEM = "ni";

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SPLIT = "&";

    static void encode(InputEventPacket msg, PacketBuffer buf) {
        buf.writeString(msg.action + SPLIT + msg.param);
    }

    static InputEventPacket decode(PacketBuffer buf) {
        String[] s = buf.readString(50).split(SPLIT);
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
            IFactionPlayer factionPlayer = FactionPlayerHandler.get(player).getCurrentFactionPlayer();
            switch (msg.action) {
                case SUCKBLOOD: {
                    int id = 0;
                    try {
                        id = Integer.parseInt(msg.param);
                    } catch (NumberFormatException e) {
                        LOGGER.error("Receiving invalid param {} for {}", msg.param, msg.action);
                    }
                    if (id != 0) {
                        VampirePlayer.get(player).biteEntity(id);
                    }
                    break;
                }
                case ENDSUCKBLOOD:
                    VampirePlayer.get(player).endFeeding(true);
                    break;
                case TOGGLEACTION: {
                    ResourceLocation id = new ResourceLocation(msg.param);
                    if (factionPlayer != null) {
                        IActionHandler actionHandler = factionPlayer.getActionHandler();
                        IAction action = ModRegistries.ACTIONS.getValue(id);
                        if (action != null) {
                            IAction.PERM r = actionHandler.toggleAction(action);
                            switch (r) {
                                case NOT_UNLOCKED:
                                    player.sendMessage(new TranslationTextComponent("text.vampirism.action.not_unlocked"));
                                    break;
                                case DISABLED:
                                    player.sendMessage(new TranslationTextComponent("text.vampirism.action.deactivated_by_serveradmin"));
                                    break;
                                case COOLDOWN:
                                    player.sendMessage(new TranslationTextComponent("text.vampirism.action.cooldown_not_over"));
                                    break;
                                case DISALLOWED:
                                    player.sendMessage(new TranslationTextComponent("text.vampirism.action.disallowed"));
                                default://Everything alright
                            }
                        } else {
                            LOGGER.error("Failed to find action with id {}", id);
                        }
                    } else {
                        LOGGER.error("Player {} is in no faction, so he cannot use action {}", player, id);
                    }


                    break;
                }
                case DRINK_BLOOD_BLOCK:
                    String[] coords = msg.param.split(":");
                    if (coords.length == 3) {
                        BlockPos pos = new BlockPos(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
                        VampirePlayer.get(player).biteBlock(pos);
                    } else {
                        LOGGER.warn("Received invalid {} parameter", DRINK_BLOOD_BLOCK);
                    }
                    break;
                case UNLOCKSKILL:
                    if (factionPlayer != null) {
                        ISkill skill = ModRegistries.SKILLS.getValue(new ResourceLocation(msg.param));
                        if (skill != null) {
                            ISkillHandler skillHandler = factionPlayer.getSkillHandler();
                            ISkillHandler.Result result = skillHandler.canSkillBeEnabled(skill);
                            if (result == ISkillHandler.Result.OK) {
                                skillHandler.enableSkill(skill);
                                if (factionPlayer instanceof ISyncable.ISyncableEntityCapabilityInst && skillHandler instanceof SkillHandler) {
                                    //does this cause problems with addons?
                                    CompoundNBT sync = new CompoundNBT();
                                    ((SkillHandler) skillHandler).writeUpdateForClient(sync);
                                    HelperLib.sync((ISyncable.ISyncableEntityCapabilityInst) factionPlayer, sync, factionPlayer.getRepresentingPlayer(), false);
                                }

                            } else {
                                LOGGER.warn("Skill {} cannot be activated for {} ({})", skill, player, result);
                            }
                        } else {
                            LOGGER.warn("Skill {} was not found so {} cannot activate it", msg.param, player);
                        }
                    } else {
                        LOGGER.error("Player {} is in no faction, so he cannot unlock skills", player);
                    }


                    break;
                case RESETSKILL:
                    if (factionPlayer != null) {
                        ISkillHandler skillHandler = factionPlayer.getSkillHandler();
                        skillHandler.resetSkills();
                        if (!VampirismMod.inDev && !VampirismMod.instance.getVersionInfo().getCurrentVersion().isTestVersion()) {
                            int l = factionPlayer.getLevel();
                            if (l > 1) {
                                FactionPlayerHandler.get(player).setFactionLevel(factionPlayer.getFaction(), l - 1);
                            }
                        }
                        if (factionPlayer instanceof ISyncable.ISyncableEntityCapabilityInst && skillHandler instanceof SkillHandler) {
                            //does this cause problems with addons?
                            CompoundNBT sync = new CompoundNBT();
                            ((SkillHandler) skillHandler).writeUpdateForClient(sync);
                            HelperLib.sync((ISyncable.ISyncableEntityCapabilityInst) factionPlayer, sync, factionPlayer.getRepresentingPlayer(), false);
                        }
                        player.sendMessage(new TranslationTextComponent("text.vampirism.skill.skills_reset"));
                    } else {
                        LOGGER.error("Player %s is in no faction, so he cannot reset skills");
                    }
                    break;
                case TRAINERLEVELUP:
                    if (player.openContainer instanceof HunterTrainerContainer) {
                        ((HunterTrainerContainer) player.openContainer).onLevelupClicked();
                    }
                    break;
                case REVERTBACK:
                    FactionPlayerHandler.get(player).setFactionAndLevel(null, 0);
                    player.sendMessage(new TranslationTextComponent("command.vampirism.base.level.successful", player.getName(), VReference.VAMPIRE_FACTION.getName(), 0));
                    LOGGER.debug("Player {} left faction", player);
                    if (!ServerLifecycleHooks.getCurrentServer().isHardcore()) {
                        player.attackEntityFrom(DamageSource.MAGIC, 1000);

                    }
                    break;
                case VAMPIRE_VISION_TOGGLE:
                    VampirePlayer.get(player).switchVision();
                    break;
                case CRAFT_BLOOD_POTION:
                    if (player.openContainer instanceof BloodPotionTableContainer) {
                        ((BloodPotionTableContainer) player.openContainer).onCraftingClicked();
                    }
                    break;
                case OPEN_BLOOD_POTION:

                    IHunterPlayer hunter = HunterPlayer.get(player);
                    if (hunter.getLevel() > 0) {
                        if (hunter.getSkillHandler().isSkillEnabled(HunterSkills.blood_potion_portable_crafting)) {
                            if (!player.world.isRemote()) {
                                NetworkHooks.openGui(player, new SimpleNamedContainerProvider((id, playerInventory, playerIn) -> new BloodPotionTableContainer(id, playerInventory, IWorldPosCallable.of(playerIn.world, new BlockPos(playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ()))), new TranslationTextComponent("container.crafting")), player.getPosition());
                            }
                        } else {
                            player.sendMessage(new TranslationTextComponent("text.vampirism.can_only_be_used_with_skill", new TranslationTextComponent(HunterSkills.blood_potion_portable_crafting.getTranslationKey())));
                        }
                    } else {
                        player.sendMessage(new TranslationTextComponent("text.vampirism.can_only_be_used_by", VReference.HUNTER_FACTION.getName()));
                    }
                    break;
                case BASICHUNTERLEVELUP:
                    if (player.openContainer instanceof HunterBasicContainer) {
                        ((HunterBasicContainer) player.openContainer).onLevelUpClicked();
                    }
                    break;
                case NAME_ITEM:
                    String name = msg.param;
                    if (VampirismVampireSword.DO_NOT_NAME_STRING.equals(name)) {
                        ItemStack stack = player.getHeldItemMainhand();
                        if (stack.getItem() instanceof VampirismVampireSword) {
                            ((VampirismVampireSword) stack.getItem()).doNotName(stack);
                        }
                    } else if (!org.apache.commons.lang3.StringUtils.isBlank(name)) {
                        ItemStack stack = player.getHeldItemMainhand();
                        stack.setDisplayName(new StringTextComponent(name));
                    }
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
