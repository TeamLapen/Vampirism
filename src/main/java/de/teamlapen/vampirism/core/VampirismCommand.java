package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.PlayableFaction;
import de.teamlapen.vampirism.api.entity.player.FactionRegistry;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Set;

/**
 * Central command for this mod
 */
public class VampirismCommand extends BasicCommand {

    public VampirismCommand() {
        if(VampirismMod.inDev){
            aliases.add("v");
        }
        final PlayableFaction[] pfactions = FactionRegistry.getPlayableFactions();
        final String[] pfaction_names = new String[pfactions.length];
        for (int i = 0; i < pfactions.length; i++) {
            pfaction_names[i] = pfactions[i].name;
        }
        addSub(new SubCommand() {
            @Override
            public boolean canCommandSenderUseCommand(ICommandSender var1) {
                return var1.canCommandSenderUseCommand(3, getCommandName());
            }

            @Override
            public String getCommandName() {
                return "resetBalance";
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) {
                String cat;
                if(var2==null||var2.length==0){
                    cat="all";
                }
                else{
                    cat=var2[0];
                }
                boolean p=Balance.reset(cat);
                if(p){
                    var1.addChatMessage(new ChatComponentText("Successfully reset "+cat+" balance category. Please restart MC."));
                }
                else{
                    var1.addChatMessage(new ChatComponentText("Did not find "+cat+" balance category."));
                }
            }

            @Override
            public String getCommandUsage(ICommandSender var1) {
                return getCommandName()+" <all/[category]>";
            }

            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return (args.length==1)?getListOfStringsMatchingLastWord(args,getCategories()):null;
            }

            private String[] getCategories(){
                Set<String> categories= Balance.getCategories().keySet();
                String[] result=categories.toArray(new String[categories.size()+1]);
                result[result.length-1]="all";
                return result;
            }
        });
        addSub(new SubCommand() {

            @Override
            public boolean canCommandSenderUseCommand(ICommandSender var1) {
                if (!(var1 instanceof EntityPlayer)) {
                    return false;//TODO set level for other players (via console)
                }
                return isSenderCreative(var1);
            }

            @Override
            public String getCommandName() {
                return "level";
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) {
                EntityPlayer player = (EntityPlayer) var1;//var1 is a player because canCommandSenderUse checks that
                if (var2.length != 2) {
                    sendMessage(var1, "Usage: " + getCommandUsage(var1));
                } else {
                    int level = 0;
                    try {
                        level = Integer.parseInt(var2[1]);
                    } catch (NumberFormatException e) {
                        sendMessage(var1, "<level> has to be a number");
                        return;
                    }
                    //Search factions
                    for (int i = 0; i < pfaction_names.length; i++) {
                        if (pfaction_names[i].equalsIgnoreCase(var2[0])) {

                            if (level != 0) {//If level is higher than 0 set other factions level back to zero
                                for (int j = 0; j < pfactions.length; j++) {
                                    if (i != j) {
                                        pfactions[j].getProp(player).setLevel(0);
                                    }
                                }
                            }
                            if (level > pfactions[i].getHighestReachableLevel()) {
                                level = pfactions[i].getHighestReachableLevel();
                            }
                            pfactions[i].getProp(player).setLevel(level);
                            IChatComponent msg = var1.getDisplayName().appendSibling(new ChatComponentText(" is now a " + pfaction_names[i] + " level " + level));
                            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(msg);
                            return;
                        }
                    }
                    sendMessage(var1, "Did not find faction " + var2[0]);

                }
            }

            @Override
            public String getCommandUsage(ICommandSender var1) {

                return getCommandName() + " " + ArrayUtils.toString(pfaction_names) + " <level>";
            }

            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return args.length == 1 ? getListOfStringsMatchingLastWord(args, pfaction_names) : null;
            }
        });
    }

    @Override
    public String getCommandName() {
        return "vampirism";
    }

    public boolean isSenderCreative(ICommandSender sender) {
        if (VampirismMod.inDev)
            return true;
        return sender.canCommandSenderUseCommand(2, this.getCommandName());
    }
}
