package de.teamlapen.vampirism.network;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.BloodPotionTableContainer;
import de.teamlapen.vampirism.inventory.HunterBasicContainer;
import de.teamlapen.vampirism.inventory.HunterTrainerContainer;
import de.teamlapen.vampirism.player.actions.ActionHandler;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Sends any input related event to the server
 */
public class InputEventPacket implements IMessage {


    public static final String SUCKBLOOD = "sb";
    public static final String TOGGLEACTION = "ta";
    public static final String UNLOCKSKILL = "us";
    public static final String RESETSKILL = "rs";
    public static final String TRAINERLEVELUP = "tl";
    public static final String REVERTBACK = "rb";
    public static final String WAKEUP = "wu";
    public static final String VAMPIRE_VISION_TOGGLE = "vvt";
    public static final String CRAFT_BLOOD_POTION = "cb";
    public static final String OPEN_BLOOD_POTION = "ob";
    public static final String BASICHUNTERLEVELUP = "bl";
    private final static String TAG = "InputEventPacket";
    private final String SPLIT = "-";
    private String param;
    private String action;

    /**
     * Don't use
     */
    public InputEventPacket() {

    }

    public InputEventPacket(String action, String param) {
        this.action = action;
        this.param = param;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        String[] s = ByteBufUtils.readUTF8String(buf).split(SPLIT);
        action = s[0];
        if (s.length > 1) {
            param = s[1];
        } else {
            param = "";
        }

    }

    @Override
    public void toBytes(ByteBuf buf) {

        ByteBufUtils.writeUTF8String(buf, action + SPLIT + param);

    }

    public static class Handler extends AbstractServerMessageHandler<InputEventPacket> {

        @Override
        public IMessage handleServerMessage(EntityPlayer player, InputEventPacket message, MessageContext ctx) {
            if (message.action == null)
                return null;
            IFactionPlayer factionPlayer = FactionPlayerHandler.get(player).getCurrentFactionPlayer();
            if (message.action.equals(SUCKBLOOD)) {
                int id = 0;
                try {
                    id = Integer.parseInt(message.param);
                } catch (NumberFormatException e) {
                    VampirismMod.log.e(TAG, e, "Receiving invalid param for %s", message.action);
                }
                if (id != 0) {
                    VampirePlayer.get(player).biteEntity(id);
                }
            } else if (message.action.equals(TOGGLEACTION)) {
                int id = -1;
                try {
                    id = Integer.parseInt(message.param);
                } catch (NumberFormatException e) {
                    VampirismMod.log.e(TAG, e, "Receiving invalid param for %s", message.action);
                }
                if (id != -1) {
                    if (factionPlayer != null) {
                        IActionHandler actionHandler = VampirismAPI.getFactionPlayerHandler(player).getCurrentFactionPlayer().getActionHandler();
                        IAction action = ((ActionHandler) actionHandler).getActionFromId(id);
                        if (action != null) {
                            IAction.PERM r = actionHandler.toggleAction(action);
                            switch (r) {
                                case NOT_UNLOCKED:
                                    player.addChatMessage(new TextComponentTranslation("text.vampirism.action.not_unlocked"));
                                    break;
                                case DISABLED:
                                    player.addChatMessage(new TextComponentTranslation("text.vampirism.action.deactivated_by_serveradmin"));
                                    break;
                                case COOLDOWN:
                                    player.addChatMessage(new TextComponentTranslation("text.vampirism.action.cooldown_not_over"));
                                    break;
                                default://Everything alright
                            }
                        } else {
                            VampirismMod.log.e(TAG, "Failed to find action with id %d", id);
                        }
                    } else {
                        VampirismMod.log.e(TAG, "Player %s is in no faction, so he cannot use action %d", player, id);
                    }


                }

            } else if (message.action.equals(UNLOCKSKILL)) {
                if (factionPlayer != null) {
                    ISkill skill = VampirismAPI.skillRegistry().getSkill(factionPlayer.getFaction(), message.param);
                    if (skill != null) {
                        ISkillHandler skillHandler = factionPlayer.getSkillHandler();
                        ISkillHandler.Result result = skillHandler.canSkillBeEnabled(skill);
                        if (result == ISkillHandler.Result.OK) {
                            skillHandler.enableSkill(skill);
                            if (factionPlayer instanceof ISyncable.ISyncableEntityCapabilityInst && skillHandler instanceof SkillHandler) {
                                //TODO does this cause problems with addons?
                                NBTTagCompound sync = new NBTTagCompound();
                                ((SkillHandler) skillHandler).writeUpdateForClient(sync);
                                HelperLib.sync((ISyncable.ISyncableEntityCapabilityInst) factionPlayer, sync, factionPlayer.getRepresentingPlayer(), false);
                            }

                        } else {
                            VampirismMod.log.w(TAG, "Skill %s cannot be activated for %s (%s)", skill, player, result);
                        }
                    } else {
                        VampirismMod.log.w(TAG, "Skill %s was not found so %s cannot activate it", message.param, player);
                    }
                } else {
                    VampirismMod.log.e(TAG, "Player %s is in no faction, so he cannot unlock skills");
                }


            } else if (message.action.equals(RESETSKILL)) {
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
                        //TODO does this cause problems with addons?
                        NBTTagCompound sync = new NBTTagCompound();
                        ((SkillHandler) skillHandler).writeUpdateForClient(sync);
                        HelperLib.sync((ISyncable.ISyncableEntityCapabilityInst) factionPlayer, sync, factionPlayer.getRepresentingPlayer(), false);
                    }
                    player.addChatComponentMessage(new TextComponentTranslation("text.vampirism.skill.skillsReset"));
                } else {
                    VampirismMod.log.e(TAG, "Player %s is in no faction, so he cannot reset skills");
                }
            } else if (message.action.equals(TRAINERLEVELUP)) {
                if (player.openContainer instanceof HunterTrainerContainer) {
                    ((HunterTrainerContainer) player.openContainer).onLevelupClicked();
                }
            } else if (message.action.equals(REVERTBACK)) {

                FactionPlayerHandler.get(player).setFactionAndLevel(null, 0);
                VampirismMod.log.d(TAG, "Player %s left faction", player);
                player.attackEntityFrom(DamageSource.magic, 1000);

            } else if (message.action.equals(WAKEUP)) {
                VampirePlayer.get(player).wakeUpPlayer(false, true, true);
            } else if (message.action.equals(VAMPIRE_VISION_TOGGLE)) {
                VampirePlayer.get(player).switchVision();
            } else if (message.action.equals(CRAFT_BLOOD_POTION)) {
                if (player.openContainer != null && player.openContainer instanceof BloodPotionTableContainer) {
                    ((BloodPotionTableContainer) player.openContainer).onCraftingClicked();
                }
            } else if (message.action.equals(OPEN_BLOOD_POTION)) {

                IHunterPlayer hunter = HunterPlayer.get(player);
                if (hunter.getLevel() > 0) {
                    if (hunter.getSkillHandler().isSkillEnabled(HunterSkills.bloodPotion_portableCrafting)) {
                        player.openGui(VampirismMod.instance, ModGuiHandler.ID_BLOOD_POTION_TABLE, player.worldObj, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
                    } else {
                        player.addChatComponentMessage(new TextComponentTranslation("text.vampirism.can_only_be_used_with_skill", new TextComponentTranslation(HunterSkills.bloodPotion_portableCrafting.getUnlocalizedName())));
                    }
                } else {
                    player.addChatComponentMessage(new TextComponentTranslation("text.vampirism.can_only_be_used_by", new TextComponentTranslation(VReference.HUNTER_FACTION.getUnlocalizedName())));
                }
            } else if (message.action.equals(BASICHUNTERLEVELUP)) {
                if (player.openContainer instanceof HunterBasicContainer) {
                    ((HunterBasicContainer) player.openContainer).onLevelUpClicked();
                }
            }
            return null;
        }

        @Override
        protected boolean handleOnMainThread() {
            return true;
        }
    }

}
